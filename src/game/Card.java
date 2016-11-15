package game;

import handChecker.PokerCard;

public class Card implements PokerCard {
	
	private PokerCard.Color color;
	private PokerCard.Value value;
	
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
		String output = "";
		
		switch (getColor()) {
			case HEARTS:
				output += "♥";
				break;
			case DIAMONDS:
				output += "♦";
				break;
			case SPADES:
				output += "♠";
				break;
			case CLUBS:
				output += "♣";
				break;
		}
		output += getPrettyValue();
		return output;
	}
	
	private String getPrettyValue()
	{
		switch (getValue()) {
			case ASS: return "A";
			case TWO: return "2";
			case THREE: return "3";
			case FOUR: return "4";
			case FIVE: return "5";
			case SIX: return "6";
			case SEVEN: return "7";
			case EIGHT: return "8";
			case NINE: return "9";
			case TEN:  return "10";
			case JACK: return "J";
			case QUEEN: return "Q";
			case KING: return "K";
			default: return "";
		}
	}
}