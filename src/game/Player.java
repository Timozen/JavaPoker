package game;

import java.util.LinkedList;
import java.util.List;

class Player {
	
	private int money;
	private List<Card> cards;
	private String nickname;
	
	/**
	 * game.Player constructor with std value for money
	 */
	public Player(String nickname)
	{
		this.nickname = nickname;
		this.money = 5000;
		this.cards = new LinkedList<>();
	}
	
	/**
	 * game.Player constructor with adjustable money
	 *
	 * @param money Value of the money of the player
	 * @param nickname The nickname of the player
	 */
	public Player(String nickname, int money)
	{
		this.nickname = nickname;
		this.money = money;
		this.cards = new LinkedList<>();
	}
	
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
	
	/**
	 * RemoveMoney - Decrease the money int value
	 *
	 * @param money The amount the that will be removed
	 * @return the current value of money, or -1 if the player
	 * doesn't have enough money
	 */
	public int RemoveMoney(int money)
	{
		if (this.money - money < 0) {
			return -1;
		} else {
			this.money -= money;
			return this.money;
		}
	}
	
	/**
	 * SetCards - Sets the cards
	 * @param cards a list of cards the should be set
	 * @return 0 if everything is fine, -1 if the amount of cards is wrong
	 *
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
	 * @return cards value
	 */
	public List<Card> GetCards()
	{
		return this.cards;
	}
	
	/**
	 * AddCard - Add a card to the players hand
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
	 * GetNickName
	 * @return String of the nickname
	 */
	public String GetNickname()
	{
		return nickname;
	}
	
	/**
	 * SetNickName
	 * @param nickname a new nickname
	 */
	public void SetNickname(String nickname)
	{
		this.nickname = nickname;
	}
}
