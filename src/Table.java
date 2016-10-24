import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Table {
	
	private Deck deck;
	private boolean gameInProgress;
	private Random roundRNG;
	
	private CircularList<Player> playersOnTable;
	private CircularList<Player> playersInRound;
	private Player dealer;
	private Player smallBlind;
	private Player bigBlind;
	
	private int pot;
	private int smallBlindValue;
	private int bigBlindValue;

	public Table()
	{
		playersOnTable = new CircularList<>();
		deck = new Deck();
		deck.CreateAndShuffle();
		
		smallBlindValue = 100;
		bigBlindValue = 2 * smallBlindValue;
		
	}
	
	/**
	 * InitRound - Initiate the round
	 * <p>
	 * Initiates the round like told in the rules of Texas Hold'em
	 */
	public void InitRound()
	{
		playersInRound = (CircularList<Player>) playersOnTable.clone();
		
		//TODO add a seed generation algorithm
		roundRNG = new Random();
		
		pot = 0;
		
		int tmpDealerIndex = roundRNG.nextInt(playersInRound.size());
		dealer = playersInRound.get(tmpDealerIndex);
		smallBlind = playersInRound.get(tmpDealerIndex + 1);
		bigBlind = playersInRound.get(tmpDealerIndex + 2);
		
		
		//TODO the blinds have to give the money to the pot
		
		
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(tmpDealerIndex + i)
				.AddCard(deck.Draw());
		}
		for (int i = 0; i < playersInRound.size(); i++) {
			playersInRound.get(tmpDealerIndex + i)
				.AddCard(deck.Draw());
		}
		
		//Debug
		for (Player p : playersInRound) {
			List<Card> tmpCards = p.GetCards();
			System.out.println(p.GetNickname() + ":"
						   + tmpCards.get(0).toString() + ","
						   + tmpCards.get(1).toString());
		}
		
	}
	
	
	/**
	 * AddPlayerToTable
	 *
	 * @param player The player that will be placed at the table
	 *               <p>
	 *               Adds a new player to the table
	 *               TODO: Add return parameter if everything went fine
	 */
	public void AddPlayerToTable(Player player)
	{
		if (!gameInProgress) {
			this.playersOnTable.add(player);
		}
	}
	
	/**
	 * RemovePlayerFromTable
	 *
	 * @param player Player to be removed
	 *               <p>
	 *               Removes a player from the game
	 *               TODO: Add return value if everyting went fine
	 */
	public void RemovePlayerFromTable(Player player)
	{
		this.playersOnTable.remove(player);
	}
	
	/**
	 * A CircularList only needed for the table
	 *
	 * @param <E>
	 */
	class CircularList<E> extends ArrayList<E> {
		@Override
		public E get(int index)
		{
			return super.get(index % size());
		}
	}

	/**
	 * IncreaseSmallBlind
	 *
	 * @param increaseValue Value the small blind should be increased about
	 */
	public void IncreaseSmallBlind(int increaseValue) { smallBlindValue = increaseValue; }

	/**
	 * IncreaseBigBlind
	 *
	 * @param increaseValue Value the small blind should be increased about
	 */
	public void IncreaseBigBlind(int increaseValue) { smallBlindValue += increaseValue; }
}
