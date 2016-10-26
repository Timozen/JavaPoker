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
	
}
