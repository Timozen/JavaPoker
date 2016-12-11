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
	public String dealerId;
	public String smallBlindId;
	public String bigBlindId;
	public int bigBlindValue;
	public int smallBlindValue;
	public int pot;
	public int tableId;
	public int roundBet;
	public Player clientPlayer;
	
	public Table(){
		players = new ArrayList<>();
		clientPlayer = new Player();
	}


	public static Table Build(JSONObject obj)
	{
		Table table = new Table();
		table.neededPlayers = obj.getInt("neededPlayers");
		table.tableId = obj.getInt("tableId");

		/*Never join existing table... bc easier

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
		*/
		return table;
	}

	public void AddPlayer(Player p)
	{
		this.players.add(p);
	}

	public void RemovePlayer(String playerId)
	{
		Player p = GetPlayerById(playerId);
		players.remove(p);
	}

	public Player GetPlayerById(String playerId)
	{
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).id.equals(playerId)) {
				return players.get(i);
			}
		}
		return null;
	}

	public int SetPlayerMoneyAmount(String playerId, int amount)
	{
		Player p = GetPlayerById(playerId);
		if (p != null) {
			p.money = amount;
			return 1;
		}
		return -1;
	}

	//TODO
	public int SetPlayerState(String playerId, int playingState)
	{
		Player p = GetPlayerById(playerId);
		if (p != null) {

		}
		return -1;
	}

	public int SetPlayerTotalBetAmount(String playerId, int totalBetAmount) {
		Player p = GetPlayerById(playerId);
		if (p != null) {
			p.roundBetAll = totalBetAmount;
			return 1;
		}
		return -1;
	}

	public int SetPlayerCurrentBetAmount(String playerId, int currentBetAmount) {
		Player p = GetPlayerById(playerId);
		if (p != null) {
			p.roundBetCurrent = currentBetAmount;
			return 1;
		}
		return -1;
	}

}
