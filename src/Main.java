import game.Table;

import java.util.Scanner;

public class Main {
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		Table table = null;
		
		System.out.print("Load pre-def(Y/N): ");
		
		String input = scanner.next();
		
		if (input.toUpperCase().contains("Y")) {
			table = new Table("content/decks/1.txt");
			
		} else if (input.toUpperCase().contains("N")) {
			System.out.print("Seed:");
			
			int seed = scanner.nextInt();
			
			if (seed == 0) {
				table = new Table();
			} else {
				table = new Table(seed);
			}
		} else {
			System.out.println("Is is so hard to write?!?");
			System.exit(2);
		}
		
		table.AddPlayerToTable("Tim");
		table.AddPlayerToTable("Niklas");
		//table.AddPlayerToTable("Matthias");
		//table.AddPlayerToTable("Jan");
		
		table.StartGame();
	}
}
