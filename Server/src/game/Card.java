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

public class Card implements PokerCard {
	
	private final PokerCard.Color color;
	private final PokerCard.Value value;
	
	Card(PokerCard.Color color, PokerCard.Value value)
	{
		this.color = color;
		this.value = value;
	}
	
	@Override
	public Color getColor()
	{
		return this.color;
	}
	
	@Override
	public Value getValue()
	{
		return this.value;
	}
	
	public String toString()
	{
		return toPrettyString();
		//return getColor() + " " + getValue();
	}
	
	public String toPrettyString()
	{
		return getPrettyColor() + getPrettyValue();
	}
	
	private String getPrettyColor()
	{
		switch (getColor()) {
			case HEARTS:
				return "♥";
			case DIAMONDS:
				return "♦";
			case SPADES:
				return "♠";
			case CLUBS:
				return "♣";
			default:
				return "INVALID!!!";
		}
	}
	
	private String getPrettyValue()
	{
		switch (getValue()) {
			case ASS:
				return "A";
			case TWO:
				return "2";
			case THREE:
				return "3";
			case FOUR:
				return "4";
			case FIVE:
				return "5";
			case SIX:
				return "6";
			case SEVEN:
				return "7";
			case EIGHT:
				return "8";
			case NINE:
				return "9";
			case TEN:
				return "10";
			case JACK:
				return "J";
			case QUEEN:
				return "Q";
			case KING:
				return "K";
			default:
				return "";
		}
	}
}