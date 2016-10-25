import game.Table;

public class Main {
	public static void main(String[] args)
	{
		Table table = new Table();

		table.AddPlayerToTable("Tim");
		table.AddPlayerToTable("Niklas");
		table.AddPlayerToTable("Matthias");
		table.AddPlayerToTable("Fabian");
		table.AddPlayerToTable("Jan");
		
		table.StartGame();
	}
}
