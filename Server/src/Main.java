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

import connection.server.ConnectionServer;

public class Main {
	public static void main(String[] args)
	{
		ConnectionServer connectionServer = new ConnectionServer(9090);
		connectionServer.start();
		
		
		/**
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
		table.AddPlayerToTable("Jan");
		
		table.StartGame();
		 
	 	**/
	}
}
