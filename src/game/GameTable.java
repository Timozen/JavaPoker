package game;

import com.sun.org.apache.bcel.internal.generic.Select;
import game.models.PlayerState;
import game.models.RoundState;

import java.util.List;
import java.util.Random;

public class GameTable {
	
	private Table.CircularList<Player> playersInRound;

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
			Fuck players without money
			we dont want to have them on our table
			Go fuck yourself
			Also valid for Timozen: Go fuck yourself <3
		 */
		table.SetRoundState(RoundState.PREFLOP);
		table.SetPot(0);
		playersInRound = (Table.CircularList<Player>) table.GetPlayersOnTable().clone();
		electivePlayersCount = playersInRound.size();		//Counts Players who have the right to vote

		if(table.IsFirstRound()){
			dealerIndex = roundRNG.nextInt(playersInRound.size());
			table.SetFirstRound(false);
		} else {
			dealerIndex++;
		}

		SelectStartPlayer();
		PayBlinds();
		
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(dealerIndex + i)
				.AddCard(deck.Draw());
		}
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(dealerIndex + i)
				.AddCard(deck.Draw());
		}
		
		//Debug
		for (Player p : playersInRound) {
			List<Card> tmpCards = p.GetCards();
			System.out.println(p.GetNickname() + ":"
						   + tmpCards.get(0).toString() + ","
						   + tmpCards.get(1).toString());
		}
		
		
		/*
			Pokerrunde
		 */
		
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
		
		//burn 1 card
		deck.Draw();
		
		//Put 3 cards on the board
		table.AddBoardCard(deck.Draw());
		table.AddBoardCard(deck.Draw());
		table.AddBoardCard(deck.Draw());
		
		
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
		
		//burn 1 card
		deck.Draw();
		
		//Put 1 card on the board
		table.AddBoardCard(deck.Draw());
		
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
		
		//burn 1 card
		deck.Draw();
		
		//Put 3 cards on the board
		table.AddBoardCard(deck.Draw());
		
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
		/**
		 * Everything needed for showdown comes here
		 */
		
		return 0;
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
			player.SetRoundState(PlayerState.ALLIN);
			electivePlayersCount -= 1;
		}
	}

	/**
	 * SelectStartPlayer - Selects beginner of a game (PreFlop Player Selection)
	 * @return -1 on failure
	 */
	//Nur vom PreFlop aufgerufen
	public int SelectStartPlayer()
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
			actualPlayer = table.GetSmallBlind();				//Dealer beginnt
		}

		highestBetPlayer = actualPlayer;
		return -1;
	}
	
}
