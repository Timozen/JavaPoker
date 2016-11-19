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
