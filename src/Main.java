
public class Main {
	public static void main(String[] args)
	{
		Deck d = new Deck();
		d.CreateAndShuffle();
		
		for (int i = 0; i < 10; i++) {
			Card c = d.Draw();
			System.out.println(c.getColor() + " " + c.getValue());
		}
		
	}
}
