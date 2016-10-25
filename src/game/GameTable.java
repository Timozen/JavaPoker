package game;

import game.models.RoundState;

import java.util.List;
import java.util.Random;

public class GameTable {
	
	private Table.CircularList<Player> playersInRound;
	private Deck deck;
	private Table table;
	private Random roundRNG;
	private int dealerIndex;
	public GameTable(Table table)
	{
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
		table.SetRoundState(RoundState.PREFLOP);
		table.SetPot(0);
		playersInRound = (Table.CircularList<Player>) table.GetPlayersOnTable().clone();
		
		if(table.IsFirstRound()){
			dealerIndex = roundRNG.nextInt(playersInRound.size());
			table.SetFirstRound(false);
		} else {
			dealerIndex++;
		}
		
		if (playersInRound.size() > 2) {
			table.SetDealer(playersInRound.get(dealerIndex));
			table.SetSmallBlind(playersInRound.get(dealerIndex + 1));
			table.SetBigBlind(playersInRound.get(dealerIndex + 2));
		} else if (playersInRound.size() == 2) {
			//TODO 2 man regelung
		} else {
			return -1;
		}
		
		
		//TODO the blinds have to give the money to the pot
		
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
		
		
		/**
		 * PreFlop gameplay comes here
		 */
		
		
		
		return 0;
	}
	
	public int FLOP()
	{
		/**
		 * flop gameplay comes here
		 */
		return 0;
	}
	
	public int TURN()
	{
		/**
		 * turn gameplay comes here
		 */
		return 0;
	}
	
	public int RIVER()
	{
		/**
		 * river gameplay comes here
		 */
		return 0;
	}
	
}
