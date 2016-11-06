import game.Table;

import java.util.Scanner;

public class Main {
	public static void main(String[] args)
	{
		Scanner scanner = new Scanner(System.in);
		System.out.print("Seed:" );
		
		int seed = scanner.nextInt();
		Table table;
		if(seed == 0){
			table = new Table();
		}else{
			table = new Table(seed);
		}
		
		

		table.AddPlayerToTable("Tim");
		table.AddPlayerToTable("Niklas");
		table.AddPlayerToTable("Matthias");
		//table.AddPlayerToTable("Fabian");
		//table.AddPlayerToTable("Jan");
		
		table.StartGame();
	}
}
