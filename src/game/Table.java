package game;

import game.models.BettingOperations;
import game.models.CircularList;
import game.models.RoundState;

import java.util.*;

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
	
	private int seed = -1;
	private Random random;
	private boolean generateRoundSeed = false;
	
	public Table(int seed){
		
		this.seed = seed;
		random = new Random(seed);
		
		playersOnTable = new CircularList<>();
		
		smallBlindValue = 100;
		bigBlindValue = 2 * smallBlindValue;
		
		gameTable = new GameTable(this);
		firstRound = true;
	}
	
	public Table()
	{
		seed = generateSeed();
		random = new Random(seed);
		playersOnTable = new CircularList<>();
		
		smallBlindValue = 100;
		bigBlindValue = 2 * smallBlindValue;
		
		gameTable = new GameTable(this);
		firstRound = true;
	}
	
	private int generateSeed()
	{
		return (int) (new Date().getTime()/1000);
	}
	
	public void StartGame()
	{
		System.out.println("The seed for this Table is: " + seed);
		
		while (playersOnTable.size() != 1) {
			
			if(generateRoundSeed){
				seed = generateSeed();
				random.setSeed(seed);
			}else {
				generateRoundSeed = true;
			}
						
			System.out.println("The seed for this Round is: " + seed);
						
			
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
			//There is a workaround in need too, bc dealer index sucks after this
			playersOnTable.stream().filter(p -> p.GetMoney() == 0).forEach(p -> {
				playersOnTable.remove(p);
			});
		}
	}
	
	public void GetPlayerAction(Player player)
	{
		//TODO das muss noch zur laufzeit rausgefunden werden!!!
		BettingOperations[] options = {BettingOperations.RAISE, BettingOperations.FOLD, BettingOperations.BET, BettingOperations.CALL, BettingOperations.CHECK};
		Scanner scanner = new Scanner(System.in);
		List<BettingOperations> operationsList = Arrays.asList(options);
		
		System.out.println("Turn of " + player.GetNickname());
		System.out.print("Options: ");
		
		for (BettingOperations op : options) {
			System.out.print(op + " ");
		}
		
		player.SetBettingAction(GetValidBettingOperationInput(operationsList, scanner));
		
		player.SetBetAmount(GetValidMoneyFromBettingOperation(player.GetBettingAction(), scanner));
	}
	
	private static int GetValidMoneyFromBettingOperation(BettingOperations op, Scanner scanner)
	{
		switch (op) {
			case FOLD:
				return 0;
			case CALL:
				return 0;
			case CHECK:
				return 0;
			case RAISE:
				return GetMoneyFromInput(scanner);
			case BET:
				return GetMoneyFromInput(scanner);
			case INVALID:
				System.out.println("This should not have happened...");
				break;
		}
		return 0;
	}
	
	private static int GetMoneyFromInput(Scanner scanner)
	{
		System.out.print("Value: ");
		while (!scanner.hasNextInt()){
			System.out.println("Invalid input!");
			System.out.print("Value: ");
			scanner.next();
		}
		System.out.println();
		return scanner.nextInt();
	}
	
	private static BettingOperations GetValidBettingOperationInput(List<BettingOperations> operationsList, Scanner scanner)
	{
		BettingOperations answerBet = BettingOperations.INVALID;
		
		while (answerBet == BettingOperations.INVALID) {
			System.out.print("\nInput:");
			
			String answer = scanner.nextLine();
			
			answerBet = CreateBettingOperationFromAnswer(answer);
			
			if (answerBet == BettingOperations.INVALID) {
				System.out.println("Input is invalid!");
				continue;
			}
			
			if (!operationsList.contains(answerBet)) {
				System.out.println("This option is not possible for you!");
				answerBet = BettingOperations.INVALID;
			}
		}
		return answerBet;
	}
	
	private static BettingOperations CreateBettingOperationFromAnswer(String answer)
	{
		answer = answer.toUpperCase();
		
		if (answer.contains("FOLD")) {
			return BettingOperations.FOLD;
		} else if (answer.contains("BET")) {
			return BettingOperations.BET;
		} else if (answer.contains("RAISE")) {
			return BettingOperations.RAISE;
		} else if (answer.contains("CALL")) {
			return BettingOperations.CALL;
		} else if (answer.contains("CHECK")) {
			return BettingOperations.CHECK;
		} else {
			return BettingOperations.INVALID;
		}
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
	
	public int GetSeed()
	{
		return seed;
	}
	
	public Random GetRandom()
	{
		return random;
	}
}

