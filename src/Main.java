
public class Main {
	public static void main(String[] args)
	{
		Table table = new Table();
		
		Player p1 = new Player("Tim");
		Player p2 = new Player("Niklas");
		Player p3 = new Player("Matthias");
		
		table.AddPlayerToTable(p1);
		table.AddPlayerToTable(p2);
		table.AddPlayerToTable(p3);
		
		table.InitRound();
		
	}
}
