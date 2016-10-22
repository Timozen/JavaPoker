import java.util.ArrayList;


public class Table {
	
	private CircularList<Player> players;
	private Deck deck;
	private boolean gameInProgress;
	private Player currentPlayer;
	private Player lastPlayer;
	
	public Table()
	{
		players = new CircularList<>();
		deck = new Deck();
		deck.CreateAndShuffle();
	}
	
	/**
	 * AddPlayerToTable
	 *
	 * @param player The player that will be placed at the table
	 *
	 * Adds a new player to the table
	 * TODO: Add return parameter if everything went fine
	 */
	public void AddPlayerToTable(Player player)
	{
		if (!gameInProgress) {
			this.players.add(player);
		}
	}
	
	/**
	 * RemovePlayerFromTable
	 * @param player Player to be removed
	 *
	 * Removes a player from the game
	 * TODO: Add return value if everyting went fine
	 */
	public void RemovePlayerFromTable(Player player)
	{
		this.players.remove(player);
	}
	
	/**
	 * A CircularList only needed for the table
	 * @param <E>
	 */
	class CircularList<E> extends ArrayList<E> {
		@Override
		public E get(int index)
		{
			return super.get(index % size());
		}
	}
}
