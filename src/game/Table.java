package game;

import game.models.BettingOperations;
import game.models.RoundState;

import java.util.ArrayList;
import java.util.List;

public class Table {
	private boolean gameInProgress;
	private CircularList<Player> playersOnTable;
	private Player dealer;
	private Player smallBlind;
	private Player bigBlind;
	
	private int pot = 0;
	private int smallBlindValue;
	private int bigBlindValue;
	
	private GameTable gameTable;
	private RoundState roundState;
	private boolean firstRound;
	private BettingOperations bettingOperationsState;
	
	private int actualRoundBet;
	private boolean isFinished = false;
	
	private List<Card> boardCards = new ArrayList<>();
	
	public Table()
	{
		playersOnTable = new CircularList<>();
		
		smallBlindValue = 100;
		bigBlindValue = 2 * smallBlindValue;
		
		gameTable = new GameTable(this);
		firstRound = true;
	}
	
	public void StartGame()
	{
		while (playersOnTable.size() != 1) {
			actualRoundBet = 0;
			isFinished = false;
			gameTable.PreFlop();
			if (!isFinished) {
				actualRoundBet = 0;
				gameTable.Flop();
			}
			if (!isFinished) {
				actualRoundBet = 0;
				gameTable.Turn();
			}
			if (!isFinished) {
				actualRoundBet = 0;
				gameTable.River();
				gameTable.Showdown();
			}
			//---- Nach jeder Stufe muss eine Notification an die Player erfolgen
			//Post-Showdown
			for (Player p : playersOnTable) {                //There is a workaround in need too, bc dealer index sucks after this
				if (p.GetMoney() == 0) {
					playersOnTable.remove(p);
				}
			}
		}
	}
	
	public BettingOperations GetPlayerAction(Player player)
	{
		//Platzhalter, wird für Multiplayer interessant
		return BettingOperations.BET;
	}
	
	/**
	 * AddPlayerToTable
	 *
	 * @param nickname The player name that will be placed at the table
	 *                 <p>
	 *                 Adds a new player to the table
	 *                 TODO: Add return parameter if everything went fine
	 */
	public void AddPlayerToTable(String nickname)
	{
		Player player = new Player(nickname);
		if (!gameInProgress) {
			this.playersOnTable.add(player);
		}
	}
	
	/**
	 * RemovePlayerFromTable
	 *
	 * @param player game.Player to be removed
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
	public void IncreaseSmallBlind(int increaseValue)
	{
		smallBlindValue = increaseValue;
	}
	
	/**
	 * IncreaseBigBlind
	 *
	 * @param increaseValue Value the small blind should be increased about
	 */
	public void IncreaseBigBlind(int increaseValue)
	{
		smallBlindValue += increaseValue;
	}
	
	public CircularList<Player> GetPlayersOnTable()
	{
		return playersOnTable;
	}
	
	public void SetPlayersOnTable(CircularList<Player> playersOnTable)
	{
		this.playersOnTable = playersOnTable;
	}
	
	public Player GetDealer()
	{
		return dealer;
	}
	
	public void SetDealer(Player dealer)
	{
		this.dealer = dealer;
	}
	
	public Player GetSmallBlind()
	{
		return smallBlind;
	}
	
	public void SetSmallBlind(Player smallBlind)
	{
		this.smallBlind = smallBlind;
	}
	
	public Player GetBigBlind()
	{
		return bigBlind;
	}
	
	public void SetBigBlind(Player bigBlind)
	{
		this.bigBlind = bigBlind;
	}
	
	public int GetPotValue()
	{
		return pot;
	}
	
	public void SetPot(int pot)
	{
		this.pot = pot;
	}
	
	public void IncreasePot(int pot)
	{
		this.pot += pot;
	}
	
	public int DecreasePot(int pot)
	{
		int oldPot = this.pot;
		if (this.pot - pot < 0) {
			this.pot = 0;
			return oldPot;
		}
		this.pot -= pot;
		return pot;
	}
	
	public int GetSmallBlindValue()
	{
		return smallBlindValue;
	}
	
	public void SetSmallBlindValue(int smallBlindValue)
	{
		this.smallBlindValue = smallBlindValue;
	}
	
	public int GetBigBlindValue()
	{
		return bigBlindValue;
	}
	
	public void SetBigBlindValue(int bigBlindValue)
	{
		this.bigBlindValue = bigBlindValue;
	}
	
	public RoundState GetRoundState()
	{
		return roundState;
	}
	
	public void SetRoundState(RoundState roundState)
	{
		this.roundState = roundState;
	}
	
	public boolean IsFirstRound()
	{
		return firstRound;
	}
	
	public void SetFirstRound(boolean firstRound)
	{
		this.firstRound = firstRound;
	}
	
	public List<Card> GetBoardCards()
	{
		return boardCards;
	}
	
	public void AddBoardCard(Card card)
	{
		boardCards.add(card);
	}
	
	public void ResetBoardCards()
	{
		boardCards.clear();
	}
	
	public void SetBettingOperationsState(BettingOperations tableState)
	{
		this.bettingOperationsState = tableState;
	}
	
	public BettingOperations GetBettingOperationsState()
	{
		return bettingOperationsState;
	}
	
	public int GetActualRoundBet()
	{
		return actualRoundBet;
	}
	
	public void SetActualRoundBet(int actualRoundBet)
	{
		this.actualRoundBet = actualRoundBet;
	}
	
	
	public void SetNextDealer()
	{
		//TODO
	}
	
	public int GetDealerIndex()
	{
		//TODO
		return 0;
	}
	
	public List<Card> GetBoardCardList()
	{
		return boardCards;
	}
	
	public void SetGameFinished(boolean isFinished)
	{
		this.isFinished = isFinished;
	}
}

