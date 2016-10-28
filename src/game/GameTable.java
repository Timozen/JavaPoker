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


	private Player highestBetPlayer;	//Spieler, der in Setz Runde den höchsten Einsatz hält
	private Player actualPlayer;		//Aktueller Spieler für Runde
	
	public GameTable(Table table) {
		this.table = table;
		deck = new Deck();
		deck.CreateAndShuffle();

		//TODO add a seed generation algorithm
		roundRNG = new Random();
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
		Table.CircularList<Player> playersInRound = (Table.CircularList<Player>) table.GetPlayersOnTable().clone();
		electivePlayersCount = playersInRound.size();		//Counts Players who have the right to vote

		if(table.IsFirstRound()){
			dealerIndex = roundRNG.nextInt(playersInRound.size());
			table.SetFirstRound(false);
		} else {
			dealerIndex++;
		}

		SelectStartPlayerPreFlop();
		PayBlinds();

		//Falls aus einem unerfindlichen Grunde die Blinds höher sind als das Geld von Spieler X und Spieler Y
		//dann sollten wir gewappnet sein, um uns dem Kampf gegen den Deadlock der PokerRunde() zu stellen.
		//Dafür ziehen wir den Showdown einfach vor
		if (electivePlayersCount <= 1) { return ShowdownPreRiver(); }	//Deus Vult!
		SpreadPlayerCards();
		
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

		/*
		//burn 1 card
		deck.Draw();
		
		//Put 3 cards on the board
		table.AddBoardCard(deck.Draw());
		table.AddBoardCard(deck.Draw());
		table.AddBoardCard(deck.Draw());
		*/
		AddBoardCard(3);
		SelectPlayerPostPreFlop();
		PokerRound();
		/**
		 * flop gameplay comes here
		 */
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

		/*
		//burn 1 card
		deck.Draw();
		
		//Put 1 card on the board
		table.AddBoardCard(deck.Draw());
		*/
		AddBoardCard(1);
		SelectPlayerPostPreFlop();
		PokerRound();
		/**
		 * turn gameplay comes here
		 */
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

		/*
		//burn 1 card
		deck.Draw();
		
		//Put 1 cards on the board
		table.AddBoardCard(deck.Draw());
		*/
		AddBoardCard(1);
		SelectPlayerPostPreFlop();
		PokerRound();
		/**
		 * river gameplay comes here
		 */
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
		if (electivePlayersCount > 1) {
		} else { //Nur noch einer übrig => Er bekommt alles Geld
			for(Player p : table.GetPlayersOnTable()){
				if (p.IsElective()) {
					p.IncreaseMoney(table.GetPot());
					/*End Logic*/
				}
			}
		}
		/**
		 * Everything needed for showdown comes here
		 */
		
		return 0;
	}

	public int ShowdownPreRiver()
	{
		switch (table.GetRoundState()) {
			case PREFLOP:
				SpreadPlayerCards();
				AddBoardCard(3);
				table.SetRoundState(RoundState.FLOP);	//redundant
			case FLOP:
				AddBoardCard(1);
				table.SetRoundState(RoundState.TURN);	//redundant
			case TURN:
				AddBoardCard(1);
				table.SetRoundState(RoundState.RIVER);	//redundant
		}
		return Showdown();
	}

	public void AddBoardCard(int amount)
	{
		deck.Draw();							//Burn first
		for (int i = 0; i < amount; i++) {
			table.AddBoardCard(deck.Draw());
			//VisibleState auf false setzen
			//An alle Clients verteilen
			//Zeit t warten
			//Aufdecken

		}
	}

	public void SpreadPlayerCards(){
		Table.CircularList<Player> playersInRound = (Table.CircularList<Player>) table.GetPlayersOnTable().clone();
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(dealerIndex + i)
					.AddCard(deck.Draw());
		}
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(dealerIndex + i)
					.AddCard(deck.Draw());
		}
	}

	public void PayBlinds()
	{
		PayMoney(table.GetSmallBlindValue(), table.GetSmallBlind());
		PayMoney(table.GetBigBlindValue(), table.GetBigBlind());
	}

	public void PayMoney(int moneyAmount, Player player)
	{
		if (player.GetMoney() - moneyAmount > 0) {
			table.IncreasePot(moneyAmount);
			player.IncreaseRoundBet(moneyAmount);
			player.DecreaseMoney(moneyAmount);
		} else {
			table.IncreasePot(player.GetMoney());
			player.IncreaseRoundBet(player.GetMoney());
			player.SetMoney(0);
			player.SetPlayerState(PlayerState.ALLIN);
			electivePlayersCount -= 1;
		}
	}

	public int PokerRound(){
		table.SetBettingOperationsState(BettingOperations.CHECK);
		while (actualPlayer != highestBetPlayer) {																		//Solange nicht einmal rundherum vom letzten Höchstbietenden
			if (actualPlayer.GetPlayerState().GetState() > 1) {															//Wenn gewählter Spieler noch Wahlmöglichkeit hat
				//--------------------------
				BettingOperations playerAction = actualPlayer.GetAction();	//Hier muss gewartet werden!!!
				//--------------------------
				if (playerAction != BettingOperations.FOLD) {															//Spieler foldet nicht
					if (playerAction == BettingOperations.RAISE || playerAction == BettingOperations.BET) {				//Spieler Raised / Bettet (wird Höchstbietender)
						if(playerAction == BettingOperations.BET){														//Sofern die Aktion Bet ist
							table.SetBettingOperationsState(BettingOperations.CALL);									//Setze Table auf "Call"-State (Bet = Raise, Check = Call)
						}
						highestBetPlayer = actualPlayer;																//aktueller Spieler ist nun Höchstbietender
					} else {																							//Spieler Callt / Checkt
						if (highestBetPlayer == null) { highestBetPlayer = actualPlayer; }								//Falls es noch keinen Höchstbietenden gibt, so setzen wir diesen auf die Person (da sie Erste am Zug ist)
					}
					PayMoney(actualPlayer.GetBetAmount(), actualPlayer);
					/*
						Jetzt wird das Geld gezahlt
						Dabei ist der Zeitpunkt der Zahlung irrelevant, da die PayMoney Funktion AllIn States berücksichtigt
						Zudem brauchen wir kein Fallback, wenn der Amount überschritten wird, da der Client die Optionen
						halten wird, zu überprüfen dass die Menge des gesetzten Geldes <= der Menge des Geldes ist
						Passt zudem direkt die RoundBet an
					 */
				} else {																								//Spieler foldet
					if (electivePlayersCount > 1) {																		//Mindestens 2 Spieler haben noch Wahlmöglichkeit
						electivePlayersCount -= 1;
					} else {
						return ShowdownPreRiver();
					}
				}
			}
		}

		//Zurücksetzen der Spieler für die nächste Setzrunde
		actualPlayer = null;
		highestBetPlayer = null;
		return 1;
	}
	/**
	 * SelectStartPlayerPreFlop - Selects beginner of a game (PreFlop Player Selection)
	 * @return -1 on failure
	 */
	//Nur vom PreFlop aufgerufen
	public int SelectStartPlayerPreFlop()
	{
		Table.CircularList<Player> playersInRound = (Table.CircularList<Player>) table.GetPlayersOnTable().clone();
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

	public int SelectPlayerPostPreFlop()
	{
		//Muss ich überarbeiten
		return -1;
	}
	
}
