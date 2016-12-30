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

package javapoker.client.connection.events;

import javapoker.client.connection.SocketConnection;
import javapoker.client.game.Player;
import javapoker.client.game.Table;
import javapoker.client.pokerui.PokerGUIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TableJoinEvent extends ConnectionEvent{

	public Table table;

	public TableJoinEvent(SocketConnection socketConnection, JSONObject data)
	{
		super(socketConnection, data);
	}

	@Override
	public void Build()
	{
		if(GetData().getBoolean("success")) {
			table = new Table();
			table.tableId = GetData().getInt("tableId");
			table.neededPlayers = GetData().getInt("neededplayercount");
			table.b = new PokerGUIBuilder(table.neededPlayers);

			JSONArray players = GetData().getJSONArray("players");

			for(int i = 0; i < players.length(); i++){
				table.AddPlayer(Player.Build(players.getJSONObject(i)));
			}
		} else {
			table = null;
		}
	}
}
