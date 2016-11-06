package game;

import game.models.BettingOperations;
import game.models.PlayerState;
import game.models.RoundState;

import java.util.List;
import java.util.Random;

public class GameTable {

	private Deck deck;
	private Table table;
	private Random roundRNG;
	private int dealerIndex;
	private int electivePlayersCount = 0;

	private Player actualPlayer;		//Aktueller Spieler für Runde

	private Table.CircularList<Player> playersInRound;
	private boolean isShowdown;
	private int actualSetRoundBet;
	

	public GameTable(Table table) {
		this.table = table;
		roundRNG = table.GetRandom();
		
		deck = new Deck(roundRNG);
		deck.CreateAndShuffle();
		
	}

	/**
	 * PreFlop - Initiate the round
	 * <p>
	 * Initiates the round like told in the rules of Texas Hold'em
	 */
	public int PreFlop()
	{
		/*
			(Learn to)
			Love our code <3
		 */
		table.SetRoundState(RoundState.PREFLOP);
		table.SetPot(0);
		playersInRound = (Table.CircularList<Player>) table.GetPlayersOnTable().clone();
		isShowdown = false;

		for(Player p : playersInRound){
			p.SetPlayerState(PlayerState.PLAYING);
		}

		electivePlayersCount = playersInRound.size();		//Counts Players who have the right to vote

		//DealerIndexWorkaround
		if (table.IsFirstRound()) {
			dealerIndex = roundRNG.nextInt(playersInRound.size());
			table.SetFirstRound(false);
			table.SetDealer(playersInRound.get(dealerIndex));
		} else {
			table.SetNextDealer();
			dealerIndex = table.GetDealerIndex();
		}

		SelectStartPlayerPreFlop();
		PayBlinds();
		SpreadPlayerCards();

		//Falls aus einem unerfindlichen Grunde die Blinds höher sind als das Geld von Spieler X und Spieler Y
		//dann sollten wir gewappnet sein, um uns dem Kampf gegen den Deadlock der PokerRunde() zu stellen.
		//Dafür ziehen wir den Showdown einfach vor
		if (electivePlayersCount <= 1) {isShowdown = true;}

		//Debug
		for (Player p : playersInRound) {
			List<Card> tmpCards = p.GetCards();
			System.out.println(p.GetNickname() + ":"
						   + tmpCards.get(0).toString() + ","
						   + tmpCards.get(1).toString());
		}

		PokerRound();

		return 0;
	}

	/**
	 * Flop
	 * @return
	 *
	 * Burn one card and put 3 more on the board
	 */
	public int Flop()
	{
		table.SetRoundState(RoundState.FLOP);

		AddBoardCard(3);
		SelectStartPlayerPostFlop();
		PokerRound();
		
		return 0;
	}

	/**
	 * Turn
	 * @return
	 *
	 * Burn one card and put 1 more on the board
	 */
	public int Turn()
	{
		table.SetRoundState(RoundState.TURN);

		AddBoardCard(1);
		SelectStartPlayerPostFlop();
		PokerRound();
		
		return 0;
	}

	/**
	 * River
	 * @return
	 *
	 * Burn one card and put 1 more aond the board
	 */
	public int River()
	{
		table.SetRoundState(RoundState.RIVER);
		
		AddBoardCard(1);
		SelectStartPlayerPostFlop();
		PokerRound();

		return 0;
	}

	/**
	 * Showdown
	 * @return
	 *
	 * Compare hands
	 */
	public int Showdown(){
		table.SetRoundState(RoundState.SHOWDOWN);
		WinnerObject winner = new WinnerObject(playersInRound, table);
		winner.CalculateActualWinnerList();


		int oldActualWinnerAmount = winner.GetActualWinnerAmount();
		Player actualWinner;
		while (table.GetPotValue() != 0) {
			winner.CalculateActualWinnerList();																			//Solange der Topf nicht ausgezahlt ist
			while ((actualWinner = winner.PopActualWinner()) != null) {													//Pop Top Element & look if not zero
				List<Player> actualWinnerList = winner.GetActualWinnerList();
				int actualWinnerAmount = winner.GetActualWinnerAmount() + 1;                                        	//Popped Top Element, so we need to increase about 1
				int moneyPerPlayer = 0;
				for (Player p : playersInRound) {
					moneyPerPlayer += p.DecreaseRoundBet(actualWinner.GetRoundBet());
				}
				table.DecreasePot(moneyPerPlayer);
				for (Player p : actualWinnerList) {
					p.IncreaseMoney(moneyPerPlayer / actualWinnerAmount);
				}
				//Work done for now, let the process start again
				//Will traverse through all the players, and if they've got 0 so no problem, nothing changes, perfect
			}
			if (table.GetPotValue() <= oldActualWinnerAmount) {															//Pot Value less than players who got paid
				//Vorerst PotValue 0 setzen
				table.SetPot(0);
				return 1;				//Success
			}
		}
		/**
		 * Everything needed for showdown comes here
		 */

		return 0;
	}

	public void AddBoardCard(int amount)
	{
		deck.Draw();							//Burn first
		for (int i = 0; i < amount; i++) {
			table.AddBoardCard(deck.Draw());
		}
	}

	public void SpreadPlayerCards()
	{
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(dealerIndex + i)
					.AddCard(deck.Draw());
		}
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(dealerIndex + i)
					.AddCard(deck.Draw());
		}
		//table muss Spieler informieren und Karten an Clients verteilen
	}

	public void PayBlinds()
	{
		PayMoney(table.GetSmallBlindValue(), table.GetSmallBlind());
		PayMoney(table.GetBigBlindValue(), table.GetBigBlind());
	}

	public void PayMoney(int moneyAmount, Player player)
	{
		table.IncreasePot(moneyAmount);
		if (moneyAmount != 0) {
			if (player.GetMoney() - moneyAmount > 0) {
				player.IncreaseRoundBet(moneyAmount);
				player.DecreaseMoney(moneyAmount);
			} else {
				player.IncreaseRoundBet(player.GetMoney());
				player.SetMoney(0);
				player.SetPlayerState(PlayerState.ALLIN);
				electivePlayersCount -= 1;
			}
		}
	}

	public int PokerRound()
	{
		System.out.println("Current Round: " + table.GetRoundState());
		System.out.print("Board: " + ((table.GetBoardCards().isEmpty()) ? "---\n":""));
		for(Card card : table.GetBoardCards()){
			System.out.print(card.toString() + " ");
		}
		
		table.SetBettingOperationsState(BettingOperations.CHECK);														//Das BettingOperations ist lediglich um Spieler zu informieren
		SetPlayersUncalled();																							//Initialisiere alle Spieler mit uncalled state

		while (!IsAllPlayersCalled() && !isShowdown) {																	//Solange nicht alle (Playing) Spieler gecallt / gecheckt haben
			if (actualPlayer.GetPlayerState().GetState() == 0) {															//Wenn gewählter Spieler noch Wahlmöglichkeit hat (State 1, 0 = AllIn/Fold)
				//--------------------------
				table.GetPlayerAction(actualPlayer);
				BettingOperations playerAction = actualPlayer.GetBettingAction(); 									//Hier muss gewartet werden!!!
				//--------------------------
				if (playerAction != BettingOperations.FOLD) {															//Spieler foldet nicht
					if (playerAction == BettingOperations.RAISE || playerAction == BettingOperations.BET) {				//Spieler Raised / Bettet (wird Höchstbietender)
						SetPlayersUncalled();																			//=> Alle müssen neu wählen
						table.SetActualRoundBet(actualPlayer.GetActualRoundBet() + actualPlayer.GetBetAmount());		//Setze aktuellen Rundeneinsatz
						if(playerAction == BettingOperations.BET){														//Sofern die Aktion Bet ist
							table.SetBettingOperationsState(BettingOperations.CALL);									//Setze Table auf "Call"-State (Bet = Raise, Check = Call)
							//------------
							//Informiere Clients, dass Schalter auf "CALL" umgelegt wurde statt "CHECK"
							//Diese Funktion sollte im table Implementiert werden, bei SetBettingOperationsState
							//------------
						}
					}

					actualPlayer.SetActualRoundBet(table.GetActualRoundBet());											//Spielers Rundeneinsatz wird darauf gesetzt (=> Table kann informieren)
					//Kommentar: Bei einem Check / Call wird der GetBetAmount auf 0 gesetzt, dann geht Fkt weiterhin
					PayMoney(actualPlayer.GetBetAmount(), actualPlayer);
					actualPlayer.SetIsCalledHighestBet(true);															//Hat höchsten Eisnatz gecallt / selbst gestellt (bleibt egal)
				} else {																								//Spieler foldet
					actualPlayer.SetPlayerState(PlayerState.FOLD);
					electivePlayersCount -= 1;
					if (GetPlayingPlayers() == 1) {
						actualPlayer.IncreaseMoney(table.GetPotValue());
						table.SetGameFinished(true);
					} else {
						if (electivePlayersCount > 1 && IsAllPlayersCalled()) {
							//return ShowdownPreRiver();
							//Müssen durchgehen, also hier Schleife abbrechen
							isShowdown = true;
						}
					}
				}
			}
			//--------------------------
			actualPlayer = playersInRound.get(playersInRound.indexOf(actualPlayer) + 1);
			//die Information dass der Spieler dran ist erfolgt erst später
			//da es sein könnte, dass der Listen Nächste keine Wahlmöglichkeit hat
		}

		//Zurücksetzen der Spieler für die nächste Setzrunde
		actualPlayer = null;
		return 1;
	}
	/**
	 * SelectStartPlayerPreFlop - Selects beginner of a game (PreFlop Player Selection)
	 * @return -1 on failure
	 */
	//Nur vom PreFlop aufgerufen
	public int SelectStartPlayerPreFlop()
	{
		table.SetDealer(playersInRound.get(dealerIndex));			//Dealer

		//Mehr als 2 Spieler
		if (playersInRound.size() > 2) {
			table.SetSmallBlind(playersInRound.get(dealerIndex + 1));	//Small Blind
			table.SetBigBlind(playersInRound.get(dealerIndex + 2));		//Big Blind
			actualPlayer = playersInRound.get(dealerIndex + 3);			//Spieler neben dem Big Blind
		} else if (playersInRound.size() == 2) {
			table.SetSmallBlind(playersInRound.get(dealerIndex));		//Button = Small Blind
			table.SetBigBlind(playersInRound.get(dealerIndex + 1));		//Big Blind = Andere Person
			actualPlayer = table.GetSmallBlind();						//Dealer beginnt
		}

		return -1;
	}

	public int SelectStartPlayerPostFlop()
	{
		//Beginnen tut Spieler nach dem Button
		for(int i = dealerIndex; i < dealerIndex + playersInRound.size(); i++){
			if (playersInRound.get(i).GetPlayerState() == PlayerState.PLAYING) {
				actualPlayer = playersInRound.get(i);
				return 1;
			}
		}
		return -1;
	}

	//Setzt alle Spieler auf uncalled
	private void SetPlayersUncalled()
	{
		for (Player p : playersInRound) {
			if (p.GetPlayerState() == PlayerState.PLAYING) {
				p.SetIsCalledHighestBet(false);
			}
		}
	}

	private boolean IsAllPlayersCalled()
	{
		for (Player p : playersInRound) {
			if (p.GetPlayerState() == PlayerState.PLAYING && !p.GetIsCalledHighestBet()) {
				return false;
			}
		}
		return true;
	}

	private int GetPlayingPlayers()
	{
		int playerCount = 0;
		for (Player p : playersInRound) {
			if (p.GetPlayerState().GetState() <= 1) {																	//Playing || All-In
				playerCount += 1;
			}
		}
		return playerCount;
	}
}
