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

package game;

import connection.client.Client;
import game.models.BettingOperations;
import game.models.PlayerState;
import handChecker.PokerCard;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Player {
	
	private int money;
	private List<Card> cards;
	private String nickname;
	private String id;
	private PlayerState playerState;                //Gives information about what Player did (Fold / AllIn / Normal)
    	private boolean hasDisconnected;
	
	//Field for the sum of all bet of all Setzrunden
	private int roundBetAll;
	//For the current Setzrunde
	private int roundBetCurrent;
	
	//Needed for input or communication
	private int betAmountFromInput;                                        //zu setzender Betrag
	private BettingOperations bettingAction;
	
	private int totalWinAmount;
	private boolean isElective;
	private boolean calledHighestBet;
	
	private List<PokerCard> cardsWithTable;
	private int winnerNumber;
	
	private Client connectionClient;
	private Table table;
	
	/**
	 * game.Player constructor with std value for money
	 */
	public Player(String nickname)
	{
		this.nickname = nickname;
		this.id = nickname;
		this.money = 5000;
		this.cards = new LinkedList<>();
		this.roundBetAll = 0;
		this.betAmountFromInput = 0;
		this.totalWinAmount = 0;
		this.isElective = true;
	}
	
	/**
	 * game.Player constructor with adjustable money
	 *
	 * @param money    Value of the money of the player
	 * @param nickname The nickname of the player
	 */
	public Player(String nickname, int money)
	{
		this(nickname);
		this.nickname = nickname;
		this.money = money;
		this.cards = new LinkedList<>();
	}
	
	/**
	 * RemoveMoney - Decrease the money int value
	 *
	 * @param money The amount the that will be removed
	 * @return the current value of money, or -1 if the player
	 * doesn't have enough money
	 */
	public int DecreaseMoney(int money)
	{
		if (this.money - money < 0) {
			return -1;
		} else {
			this.money -= money;
			return this.money;
		}
	}
	
	/**
	 * IncreaseMoney - Increase the money about money
	 *
	 * @param money Value the amount should be increased about
	 */
	public void IncreaseMoney(int money)
	{
		this.money += money;
	}
	
	/**
	 * SetCards - Sets the cards
	 *
	 * @param cards a list of cards the should be set
	 * @return 0 if everything is fine, -1 if the amount of cards is wrong
	 * <p>
	 * Sets the cards for the player if both cards are handed for example
	 */
	public int SetCards(List<Card> cards)
	{
		if (cards.size() == 2) {
			this.cards = cards;
			return 0;
		} else {
			return -1;
		}
	}
	
	/**
	 * GetCards - Returns both cards
	 *
	 * @return cards value
	 */
	public List<Card> GetCards()
	{
		return this.cards;
	}
	
	/**
	 * AddCard - Add a card to the players hand
	 *
	 * @param card the card to be added
	 * @return 0 if everything is ok, -1 if amount of cards is wrong
	 * Adds a card to the hand, only if there are less than 2 cards
	 */
	public int AddCard(Card card)
	{
		if (cards.size() >= 2) {
			return -1;
		} else {
			cards.add(card);
			return 0;
		}
	}
	
	/**
	 * Increase Round Bet
	 *
	 * @param roundBet Value for increasing
	 *                 Increases Round Bet about roundBetAll
	 */
	public void IncreaseRoundBetAll(int roundBet)
	{
		this.roundBetAll += roundBet;
	}
	
	public int DecreaseRoundBetAll(int roundBet)
	{
		int oldBet = this.roundBetAll;
		if ((this.roundBetAll - roundBet) < 0) {
			this.roundBetAll = 0;
			return oldBet;
		}
		this.roundBetAll -= roundBet;
		return roundBet;
	}
	public void ResetPlayerExceptMoney()
	{
		cards = new LinkedList<>();
		cardsWithTable = new LinkedList<>();
		roundBetCurrent = 0;
		roundBetAll = 0;
	}
	
	public JSONObject ToJSON()
	{
		JSONObject obj = new JSONObject();
		
		obj.put("nickname", nickname);
		obj.put("playerState", playerState);
		obj.put("roundBetAll", roundBetAll);
		obj.put("roundBetCurrent", roundBetCurrent);
		obj.put("lastBettingOption", bettingAction);
		obj.put("money", money);
		obj.put("id", id);

		return obj;
	}
	
	
	//region Getter and Setter
	
	//region Client
	public Client GetConnectionClient()
	{
		return connectionClient;
	}
	
	public void SetConnectionClient(Client connectionClient)
	{
		this.connectionClient = connectionClient;
	}
	//endregion
	
	
	//region WinnerNumber
	public void SetWinnerNumber(int winnerNumber)
	{
		this.winnerNumber = winnerNumber;
	}
	
	public int GetWinnerNumber()
	{
		return winnerNumber;
	}

	public void IncreaseWinnerNumber(int amount) { winnerNumber += amount; }

	public void DecreaseWinnerNumber(int amount) { winnerNumber -= amount; }
	//endregion
	
	//region CardWithTable
	public void SetCardsWithTable(Table table)
	{
		cardsWithTable = new ArrayList<>();
		
		this.cardsWithTable.addAll(cards);
		cardsWithTable.addAll(table.GetBoardCardList());
	}
	
	public List<PokerCard> GetCardsWithTable()
	{
		return cardsWithTable;
	}
	//endregion
	
	//region BetAmountFromInput
	public void SetBetAmountFromInput(int betAmount)
	{
		this.betAmountFromInput = betAmount;
	}
	
	public int GetBetAmountFromInput()
	{
		return betAmountFromInput;
	}
	//endregion
	
	//region RoundBetCurrent
	public int GetRoundBetCurrent()
	{
		return roundBetCurrent;
	}
	
	public void SetRoundBetCurrent(int actualRoundBet)
	{
		this.roundBetCurrent = actualRoundBet;
	}
	//endregion
	
	//region BettingAction
	public BettingOperations GetBettingAction()
	{
		return bettingAction;
	}
	
	public void SetBettingAction(BettingOperations action)
	{
		this.bettingAction = action;
	}
	//endregion
	
	//region IsCalledHighestBet
	public void SetIsCalledHighestBet(boolean calledHighestBet)
	{
		this.calledHighestBet = calledHighestBet;
	}
	
	public boolean GetIsCalledHighestBet()
	{
		return calledHighestBet;
	}
	//endregion
	
	//region PlayerState
	/**
	 * GetPlayerState
	 *
	 * @return State of the player in actual Round
	 */
	public PlayerState GetPlayerState()
	{
		return this.playerState;
	}
	
	/**
	 * SetPlayerState
	 *
	 * @param playerState the new state for the player
	 */
	public void SetPlayerState(PlayerState playerState)
	{
		this.playerState = playerState;
		if (playerState == PlayerState.ALLIN || playerState == PlayerState.FOLD) {
			isElective = false;
		}
	}
	//endregion
	
	//region RoundBetAll
	/**
	 * SetRoundBetAll
	 *
	 * @param roundBet Value for Round Bet
	 */
	public void SetRoundBetAll(int roundBet)
	{
		this.roundBetAll = roundBet;
	}
	
	/**
	 * GetRoundBetAll
	 *
	 * @return roundBetAll returns roundBetAll for Player
	 */
	public int GetRoundBetAll()
	{
		return roundBetAll;
	}
	//endregion
	
	//region Money
	/**
	 * GetMoney - Returns the current value of money
	 *
	 * @return int value of money
	 * <p>
	 * Return the current value of money the player has
	 */
	public int GetMoney()
	{
		return money;
	}
	
	/**
	 * SetMoney - Sets the money to a specific value
	 *
	 * @param money Value that the money should have
	 *              <p>
	 *              Sets the money for the player, can probably be removed
	 *              TODO: Possibly remove, because unnecessary
	 */
	public void SetMoney(int money)
	{
		this.money = money;
	}
	//endregion
	
	//region Nickname
	/**
	 * GetNickName
	 *
	 * @return String of the nickname
	 */
	public String GetNickname()
	{
		return nickname;
	}
	
	/**
	 * SetNickName
	 *
	 * @param nickname a new nickname
	 */
	public void SetNickname(String nickname)
	{
		this.nickname = nickname;
	}
	//endregion
	
	//region Elective
	public boolean IsElective()
	{
		return isElective;
	}
	//endregion

	public void SetBetAmount(int amount) {
		betAmountFromInput = amount;
	}

	public int GetBetAmount() {
		return betAmountFromInput;
	}

	public int GetTotalWinAmount() { return totalWinAmount;}

	public void IncreaseTotalWinAmount(int totalWinAmount) {
		this.totalWinAmount += totalWinAmount;
	}

	public void SetTotalWinAmount(int totalWinAmount) {
		this.totalWinAmount = totalWinAmount;
	}

	public void SetHasDisconnected(boolean hasDisconnected)
    	{
		this.hasDisconnected = hasDisconnected;
    	}

    	public boolean GetHasDisconnected()
    	{
        	return hasDisconnected;
    	}
	
	public Table GetTable()
	{
		return table;
	}
	
	public void SetTable(Table table)
	{
		this.table = table;
	}
	
	//endregion
}