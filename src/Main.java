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
		//Implementierung:
		// Table.GetPlayerAction() <= da muss die Aktion gewählt werden.
		//							  mit für den Player aufgerufen werden muss SetBetAmount()

	}
}
