/*
 * JavaPoker - Online Poker Game Copyright (C) 2016 Tim Büchner, Matthias Döpmann
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */

//Milestone 1 finished
package game;

import connection.ConnectionEventListener;
import game.models.BettingOperations;
import game.models.CircularList;
import game.models.RoundState;
import handChecker.PokerCard;
import org.json.JSONObject;

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
	
	private boolean isPreDef;
	private int dealerIndex;
	
	private boolean preBet;
	private final BettingOperations[] betOptionsPreBet = {BettingOperations.FOLD, BettingOperations.BET, BettingOperations.CHECK};
	private final BettingOperations[] betOptionsPostBet = {BettingOperations.FOLD, BettingOperations.RAISE, BettingOperations.CALL};
	
	
	
	public Table(String filePath){
		seed = generateSeed();
		random = new Random(seed);
		playersOnTable = new CircularList<>();
		
		smallBlindValue = 100;
		bigBlindValue = 2 * smallBlindValue;
		
		gameTable = new GameTable(this, filePath);
		firstRound = true;
		isPreDef = true;
	}
	
	public Table(int seed)
	{
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
		return (int) (new Date().getTime() / 1000);
	}
	
	public void StartGame()
	{
		System.out.println("The seed for this Table is: " + seed);
		
		while (playersOnTable.size() > 1) {
			System.out.println("####################################################");
			System.out.println("Players & Money:");
			for(Player p : playersOnTable) {
				System.out.println(p.GetNickname() + " with " + p.GetMoney());
				//initializing standard values
				p.ResetPlayerExceptMoney();
				boardCards = new ArrayList<>();
			}
			if (generateRoundSeed) {
				seed = generateSeed();
				random.setSeed(seed);
			} else {
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
			RemovePlayersWithZeroMoneyAndRecalculateDealerIndex();
		}
		System.out.println("\n\nMaximum 1 Player left => Ending");
	}
	
	public void GetPlayerAction(Player player)
	{
		/*
		JSONObject answer = player.GetClient().sendMessage();
		
		while(gotNoAnswer && time < timeOut){
		
		}
		if(time > timeOut || !gotNotAnswer){
			default answer
		} else {
			last received answer
		}
		
		*/
		
		BettingOperations[] options = IsPreBet() ?  betOptionsPreBet : betOptionsPostBet;
		
		Scanner scanner = new Scanner(System.in);
		List<BettingOperations> operationsList = Arrays.asList(options);
		
		System.out.println("Turn of " + player.GetNickname() + "(" + player.GetMoney() + ")");
		System.out.print("Options: ");
		
		operationsList.forEach((BettingOperations op) -> System.out.print(op + " "));
		
		player.SetBettingAction(GetValidBettingOperationInput(operationsList, scanner));
		player.SetBetAmountFromInput(GetValidMoneyFromBettingOperation(player.GetBettingAction(), scanner, player));
		
		System.out.println("Player " + player.GetNickname() + " " + player.GetBettingAction() + " " + player.GetBetAmountFromInput());
		if (player.GetBettingAction() != BettingOperations.BET && player.GetBettingAction() != BettingOperations.RAISE) { System.out.println(); }
	}
	
	private int GetValidMoneyFromBettingOperation(BettingOperations op, Scanner scanner, Player p)
	{
		switch (op) {
			case FOLD:
				return 0;
			case CALL:
				return 0;
			case CHECK:
				return 0;
			case RAISE:
				return GetMoneyFromInput(scanner, p);
			case BET:
				return GetMoneyFromInput(scanner, p);
			case INVALID:
				System.out.println("This should not have happened...");
				break;
		}
		return 0;
	}
	
	private int GetMoneyFromInput(Scanner scanner, Player p)
	{
		int maximumBet = p.GetMoney() - (actualRoundBet - p.GetRoundBetCurrent());
		if (maximumBet < 0) {
			p.SetBettingAction(BettingOperations.CALL);
			maximumBet = 0;
		}
		int playerBet = maximumBet + 1;
		System.out.println("Current Player Bet: " + p.GetRoundBetCurrent());
		System.out.println("Current Table Bet: " + actualRoundBet);
		System.out.println("Maximum Player Bet: " + maximumBet);
		System.out.print("Value: ");
		while (playerBet > maximumBet || playerBet < 0) {
			while (!scanner.hasNextInt()) {
				System.out.println("Invalid input!");
				System.out.print("Value: ");
				scanner.next();
			}
			playerBet = scanner.nextInt();
			if (playerBet > maximumBet || playerBet < 0) {
				System.out.println("Invalid input!");
				System.out.print("Value: ");
			}
		}
		System.out.println("Player will pay " + playerBet
			+ " + " + (actualRoundBet - p.GetRoundBetCurrent())
			+ " = " + (playerBet + (actualRoundBet - p.GetRoundBetCurrent())));
		System.out.println();
		return playerBet;
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
		//TODO notify clients
		this.playersOnTable.remove(player);
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
	
	public int GetRoundBetCurrent()
	{
		return actualRoundBet;
	}
	
	public void SetRoundBetCurrent(int actualRoundBet)
	{
		this.actualRoundBet = actualRoundBet;
	}
	
	public int GetDealerIndex()
	{
		//TODO
		return this.dealerIndex;
	}

	public void SetDealerIndex(int dealerIndex)
	{
		this.dealerIndex = dealerIndex;
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

	public int GetHighestAmountToSubstractFromPot(int amount)
	{
		if (pot - amount < 0) {
			return amount;
		}
		return pot;
	}
	
	public boolean IsPreDef()
	{
		return isPreDef;
	}

	private void RemovePlayersWithZeroMoneyAndRecalculateDealerIndex()
	{
		CircularList<Player> tempPlayersOnTable = (CircularList<Player>)GetPlayersOnTable().clone();
		System.out.println("\nRound at its end: Removing 0-Money Players");
		Player oldDealer = this.GetDealer();
		//next Dealer = oldDealer + 1
		for (Player p : tempPlayersOnTable) {
			System.out.println(p.GetNickname() + " money is " + p.GetMoney());
			if (p.GetMoney() == 0 && p != oldDealer) {
				System.out.println(p.GetNickname() + " will be removed.");
				RemovePlayerFromTable(p);
			}
		}
		int nextDealerIndex = playersOnTable.indexOf(oldDealer) + 1;
		this.SetDealerIndex(nextDealerIndex);
		System.out.println("Current Dealer was " + oldDealer.GetNickname());
		System.out.println("New Dealer will be " + playersOnTable.get(nextDealerIndex).GetNickname());
		if (oldDealer.GetMoney() == 0) {
			System.out.println("Old dealer (" + oldDealer.GetNickname() + ") got removed bc 0 money.");
			RemovePlayerFromTable(oldDealer);
		}
	}
	
	public boolean IsPreBet()
	{
		return preBet;
	}
	
	public void SetPreBet(boolean preBet)
	{
		this.preBet = preBet;
	}
}

