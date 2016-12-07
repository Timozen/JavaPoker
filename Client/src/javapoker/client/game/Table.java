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

package javapoker.client.game;

import org.json.JSONObject;

import java.util.ArrayList;

public class Table {
	
	public ArrayList<Player> players = new ArrayList<>();
	public int neededPlayers;
	public Player dealer;
	public Player smallBlind;
	public Player bigBlind;
	public int bigBlindValue;
	public int smallBlindValue;
	public int pot;
	public int id;
	
	public Table(){}
	
	
	public static Table Build(JSONObject obj)
	{
		Table table = new Table();
		table.neededPlayers = obj.getInt("neededPlayers");
		table.id = obj.getInt("id");
		table.bigBlindValue = obj.getInt("bigBlindValue");
		table.smallBlindValue = obj.getInt("smallBlindValue");
		table.pot = obj.getInt("pot");

		if(obj.has("smallBlind")){
			table.smallBlind = Player.Build(obj.getJSONObject("smallBlind"));
		}
		if(obj.has("bigBlind")){
			table.bigBlind = Player.Build(obj.getJSONObject("bigBlind"));
		}
		if(obj.has("dealer")){
			table.dealer = Player.Build(obj.getJSONObject("dealer"));
		}
		
		return table;
	}
}
