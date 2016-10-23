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
	
	public String toString(){
		return getColor() + " " + getValue();
	}
}