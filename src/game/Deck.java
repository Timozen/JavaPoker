package game;

import handChecker.PokerCard;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Deck {
	private Queue<Card> queue;
	private Random r;
	public Deck(Random r){
		this.r = r;
	}
	public Deck(){}
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
		LinkedList<Card> cards = new LinkedList<>();
		for(PokerCard.Color color : PokerCard.Color.values()){
			for(PokerCard.Value value : PokerCard.Value.values()){
				cards.add(new Card(color, value));
			}
		}
		
		shuffle(cards);
		
		//this doesnt work with our pseudo random
		//Collections.shuffle(cards);
		
		queue = cards;
	}
	
	/**
	 * shuffle - shuffles a list of cards
	 *
	 * @param cards contains a list of cards
	 *
	 * This method shuffles a list of cards with a very simple
	 * algorithm
	 */
	private void shuffle(List<Card> cards)
	{
		for (int i = cards.size() - 1; i > 0; i--) {
			int tmpPos = r.nextInt(i);
			Card tmpCard = cards.get(i);
			cards.set(i, cards.get(tmpPos));
			cards.set(tmpPos, tmpCard);
		}
	}
	
	public static Deck LoadPreDefinedDeck(String filePath){
		Deck deck = new Deck();
		File file = new File(filePath);
		List<Card> cards = new LinkedList<>();
		
		
		try {
			Scanner scanner = new Scanner(file);
			
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				
				String[] input = line.split(" ");
				Card card = new Card(PokerCard.Color.valueOf(input[0]), PokerCard.Value.valueOf(input[1]));
				cards.add(card);
			}
			scanner.close();
			deck.SetQueue((Queue<Card>) cards);
			
		} catch (IOException ex){
			ex.printStackTrace();
		}
				
		return deck;
	}
	
	public void SetQueue(Queue<Card> queue)
	{
		this.queue = queue;
	}
}
