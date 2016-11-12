package game;

import game.models.BettingOperations;
import game.models.CircularList;
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
	
	private Player actualPlayer;                //Aktueller Spieler für Runde
	
	private CircularList<Player> playersInRound;
	private boolean isShowdown;
	private int actualSetRoundBet;
	
	
	public GameTable(Table table)
	{
		this.table = table;
		roundRNG = table.GetRandom();
		
		deck = new Deck(roundRNG);
	}
	
	public GameTable(Table table, String filePath)
	{
		this.table = table;
		roundRNG = table.GetRandom();
		
		deck = Deck.LoadPreDefinedDeck(filePath);
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
		
		if (!table.IsPreDef()) {
			deck.CreateAndShuffle();
		}
		
		table.SetRoundState(RoundState.PREFLOP);
		table.SetPot(0);
		playersInRound = (CircularList<Player>) table.GetPlayersOnTable();
		isShowdown = false;
		
		playersInRound.forEach((Player p) -> p.SetPlayerState(PlayerState.PLAYING));
		
		electivePlayersCount = playersInRound.size();                //Counts Players who have the right to vote
		
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
		if (electivePlayersCount <= 1) {
			isShowdown = true;
		}
		
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
	 *
	 * @return Burn one card and put 3 more on the board
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
	 *
	 * @return Burn one card and put 1 more on the board
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
	 *
	 * @return Burn one card and put 1 more aond the board
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
	 *
	 * @return Compare hands
	 */
	public int Showdown()
	{
		table.SetRoundState(RoundState.SHOWDOWN);
		
		roundBeginOutput();
		
		
		WinnerHandler winner = new WinnerHandler(table);
		CircularList<WinnerPlayer> winners;
		
		while (table.GetPotValue() > 0) {
			winners = winner.CalculateWinnerPlayerList();
			System.out.println("\nPot Value: " + table.GetPotValue());
			for (WinnerPlayer wp : winners) {
				System.out.println("Paying " + wp.GetPlayerHandle().GetNickname() + ". Old amount: " + wp.GetPlayerHandle().GetMoney());
				wp.GetPlayerHandle().IncreaseMoney(wp.GetWinAmount());
				table.DecreasePot(wp.GetWinAmount());
				System.out.println("New player amount: " + wp.GetPlayerHandle().GetMoney());
				System.out.println("New pot value: " + table.GetPotValue());
			}
			
			//Sicherstellung beim Split-Pot
			if (table.GetPotValue() <= winners.size()) {
				table.SetPot(0);
			}
		}
		
		/**
		 * Everything needed for showdown comes here
		 */
		
		return 0;
	}
	
	
	public void AddBoardCard(int amount)
	{
		deck.Draw();                                                        //Burn first
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
		table.GetSmallBlind().SetRoundBetCurrent(table.GetSmallBlindValue());
		System.out.println("Player " + table.GetSmallBlind().GetNickname() + " paid the SmallBlind of " + table.GetSmallBlindValue());
		
		PayMoney(table.GetBigBlindValue(), table.GetBigBlind());
		table.SetRoundBetCurrent(table.GetBigBlindValue());
		table.GetBigBlind().SetRoundBetCurrent(table.GetBigBlindValue());
		
		System.out.println("Player " + table.GetBigBlind().GetNickname() + " paid the BigBlind of " + table.GetBigBlindValue());
		System.out.println("ActualRoundBet: " + table.GetRoundBetCurrent());
		System.out.println("Pot Value: " + table.GetPotValue());
		System.out.println();
	}
	
	public void PayMoney(int moneyAmount, Player player)
	{
		if (moneyAmount != 0) {
			if (player.GetMoney() - moneyAmount > 0) {
				System.out.println(player.GetNickname() + " paid " + moneyAmount + "(moneyAmount)");
				table.IncreasePot(moneyAmount);
				player.IncreaseRoundBetAll(moneyAmount);
				player.DecreaseMoney(moneyAmount);
			} else {
				System.out.println(player.GetNickname() + " paid " + player.GetMoney() + "(Player Money)");
				table.IncreasePot(player.GetMoney());
				player.IncreaseRoundBetAll(player.GetMoney());
				player.SetMoney(0);
				player.SetPlayerState(PlayerState.ALLIN);
				electivePlayersCount -= 1;
				
				//All in notification
				System.out.println("Player " + player.GetNickname() + " went All-In");
			}
		}
	}
	
	public int PokerRound()
	{
		roundBeginOutput();
		System.out.println("Pot Value: " + table.GetPotValue());
		table.SetBettingOperationsState(BettingOperations.CHECK);                                                                                                                //Das BettingOperations ist lediglich um Spieler zu informieren
		SetPlayersUncalled();                                                                                                                                                                                        //Initialisiere alle Spieler mit uncalled state
		
		while (!IsAllPlayersCalled() && !isShowdown) {                                                                                                                                        //Solange nicht alle (Playing) Spieler gecallt / gecheckt haben
			if (actualPlayer.GetPlayerState().GetNumeric() == 0) {                                                                                                                //Wenn gewählter Spieler noch Wahlmöglichkeit hat (State 1, 0 = AllIn/Fold)
				//--------------------------
				table.GetPlayerAction(actualPlayer);
				BettingOperations playerAction = actualPlayer.GetBettingAction();                                                                                //Hier muss gewartet werden!!!
				//--------------------------
				if (playerAction != BettingOperations.FOLD) {                                                                                                                        //Spieler foldet nicht
					int needToPay = table.GetRoundBetCurrent() + actualPlayer.GetBetAmountFromInput();
					//Spieler Raised / Bettet (wird Höchstbietender)
					if (playerAction == BettingOperations.RAISE || playerAction == BettingOperations.BET) {
						//=> Alle müssen neu wählen
						//=> Rundeneinsatz wird incrementiert
						SetPlayersUncalled();
						table.SetRoundBetCurrent(needToPay);
						//TODO informiere clients, falls Bet => Raise & Call
					}
					//Kommentar: Bei einem Check / Call wird der GetBetAmountFromInput auf 0 gesetzt, dann geht Fkt weiterhin
					
					int diff = needToPay - actualPlayer.GetRoundBetCurrent();
					PayMoney(diff, actualPlayer);
					actualPlayer.SetRoundBetCurrent(needToPay);
					
					actualPlayer.SetIsCalledHighestBet(true);                                                                                                                        //Hat höchsten Eisnatz gecallt / selbst gestellt (bleibt egal)
				} else {                                                                                                                                                                                                //Spieler foldet
					actualPlayer.SetPlayerState(PlayerState.FOLD);
					electivePlayersCount -= 1;
					//Happens when only 1 Player left
					if (GetPlayingPlayers() == 1) {
						int nextPlayer = playersInRound.indexOf(actualPlayer) + 1;
						//Bestimmung des Spielers welcher noch spielt (einer muss es sein)
						while (table.GetPlayersOnTable().get(nextPlayer).GetPlayerState().GetNumeric() > 1) {
							nextPlayer += 1;
						}
						//
						playersInRound.get(nextPlayer).IncreaseMoney(table.GetPotValue());
						table.SetPot(0);
						table.SetGameFinished(true);
						isShowdown = true;
						System.out.println("Only " + playersInRound.get(nextPlayer).GetNickname() + " left.");
						System.out.println("Money increased about " + table.GetPotValue());
						//Spieler sind handlungsunfähig, alle haben gecallt und mehr als 2 drin
					} else if (electivePlayersCount == 1 && IsAllPlayersCalled()) {
						//return ShowdownPreRiver();
						//Müssen durchgehen, also hier Schleife abbrechen
						System.out.println("Earlier showdown set");
						isShowdown = true;
					}
				}
			}
			//--------------------------
			actualPlayer = playersInRound.get(playersInRound.indexOf(actualPlayer) + 1);
			//die Information dass der Spieler dran ist erfolgt erst später
			//da es sein könnte, dass der Listen Nächste keine Wahlmöglichkeit hat
			System.out.println("Current Pot Value: " + table.GetPotValue());
		}
		
		//Zurücksetzen der Spieler für die nächste Setzrunde
		actualPlayer = null;
		
		roundEndOutput();
		resetRoundBetCurrent();
		
		return 1;
	}
	
	/**
	 * SelectStartPlayerPreFlop - Selects beginner of a game (PreFlop Player Selection)
	 *
	 * @return -1 on failure
	 */
	//Nur vom PreFlop aufgerufen
	public int SelectStartPlayerPreFlop()
	{
		table.SetDealer(playersInRound.get(dealerIndex));                        //Dealer
		
		//Mehr als 2 Spieler
		if (playersInRound.size() > 2) {
			table.SetSmallBlind(playersInRound.get(dealerIndex + 1));        //Small Blind
			table.SetBigBlind(playersInRound.get(dealerIndex + 2));                //Big Blind
			actualPlayer = playersInRound.get(dealerIndex + 3);                        //Spieler neben dem Big Blind
		} else if (playersInRound.size() == 2) {
			table.SetSmallBlind(playersInRound.get(dealerIndex));                //Button = Small Blind
			table.SetBigBlind(playersInRound.get(dealerIndex + 1));                //Big Blind = Andere Person
			actualPlayer = table.GetSmallBlind();                                                //Dealer beginnt
		}
		
		System.out.println();
		System.out.println("Dealer: " + table.GetDealer().GetNickname());
		System.out.println("SmallBlind: " + table.GetSmallBlind().GetNickname());
		System.out.println("BigBlind: " + table.GetBigBlind().GetNickname());
		System.out.println();
		
		return -1;
	}
	
	public int SelectStartPlayerPostFlop()
	{
		//Beginnen tut Spieler nach dem Button
		for (int i = dealerIndex; i < dealerIndex + playersInRound.size(); i++) {
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
		playersInRound.stream().filter(p -> p.GetPlayerState() == PlayerState.PLAYING).forEach(p -> {
			p.SetIsCalledHighestBet(false);
		});
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
			if (p.GetPlayerState().GetNumeric() <= 1) {                                                                                                                                        //Playing || All-In
				playerCount += 1;
			}
		}
		System.out.println("Remaining Players: " + playerCount);
		return playerCount;
	}
	
	//OUTPUT
	private void roundBeginOutput()
	{
		System.out.println("####################################################");
		System.out.println("Current Round: " + table.GetRoundState());
		System.out.print("Board: " + ((table.GetBoardCards().isEmpty()) ? "---" : ""));
		for (Card card : table.GetBoardCards()) {
			System.out.print(card.toString() + ", ");
		}
		System.out.println();
	}
	
	private void roundEndOutput()
	{
		System.out.println("\n#######################################");
		System.out.println("Elective Player Count: " + electivePlayersCount);
		System.out.print("Remaining players: ");
		playersInRound.stream().filter(player -> player.GetPlayerState() == PlayerState.PLAYING ||
			player.GetPlayerState() == PlayerState.ALLIN).forEach(player -> {
			System.out.print(player.GetNickname() + " (" + player.GetMoney() + "), ");
		});
		System.out.println();
		System.out.println("Current Pot:" + table.GetPotValue());
		System.out.println("#######################################\n");
		
	}
	
	private void resetRoundBetCurrent()
	{
		for (Player p : playersInRound) {
			p.SetRoundBetCurrent(0);
		}
	}
}
