package game;

import game.Card;
import handChecker.PokerCard;

import java.util.*;

public class Deck {
	private Queue<Card> queue;
	
	/**
	 * Draw - Draws a card
	 *
	 * @return Pokercard
	 */
	public Card Draw()
	{
		return queue.poll();
	}
	
	/**
	 * CreateAndShuffle - Creates a new shuffled game.Deck
	 * <p>
	 * This method creates a new deck with 52 shuffled cards.
	 */
	public void CreateAndShuffle()
	{
		/**
		game.Card cards[] = new game.Card[52];
		int tempColorRange = 13;
		
		for (int i = 0; i < PokerCard.Color.values().length; i++) {
			for (int j = 0; j < PokerCard.Value.values().length; j++) {
				int tmpPos = i * tempColorRange + j;
				cards[tmpPos] = new game.Card(PokerCard.Color.values()[i],
							 PokerCard.Value.values()[j]);
			}
		}
		 **/
		
		LinkedList<Card> cards = new LinkedList<>();
		for(PokerCard.Color color : PokerCard.Color.values()){
			for(PokerCard.Value value : PokerCard.Value.values()){
				cards.add(new Card(color, value));
			}
		}
		
		Collections.shuffle(cards);
		
		queue = cards;
	}
	
	/**
	 * shuffle - shuffles a card array
	 *
	 * @param cards contains a array of cards
	 *
	 * This method shuffles a card array with a very simple
	 * algorithm
	 */
	private void shuffle(Card[] cards)
	{
		Random r = new Random();
		for (int i = cards.length - 1; i > 0; i--) {
			int tmpPos = r.nextInt(i);
			
			Card tmpCard = cards[i];
			cards[i] = cards[tmpPos];
			cards[tmpPos] = tmpCard;
		}
	}
	
	/**
	 * Only for Debug!!!
	 *
	 * @param cards a array of cards
	 */
	private void print(List<Card> cards)
	{
		System.out.println("########################");
		for (Card card : cards) {
			System.out.println(card.getColor() + " " + card.getValue());
		}
		System.out.println("########################");
	}
}
