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
import javapoker.client.game.OpenTable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Tim on 04.12.2016.
 */
public class OpenTablesEvent extends ConnectionEvent {
	public ArrayList<OpenTable> openTables;
	public OpenTablesEvent(SocketConnection socketConnection, JSONObject data)
	{
		super(socketConnection, data);
	}
	
	@Override
	public void Build()
	{
		JSONArray openTables = GetData().getJSONArray("tables");
		this.openTables = new ArrayList<>();
		
		for(int i = 0; i < openTables.length(); i++) {
			JSONObject itTable = openTables.getJSONObject(i);
			String tableId = itTable.getString("tableId");
			int currentPlayers = itTable.getInt("currentPlayers");
			int neededPlayers = itTable.getInt("neededPlayers");
			this.openTables.add(new OpenTable(
							tableId,
							currentPlayers,
							neededPlayers
					)
			);
		}
	}
}
