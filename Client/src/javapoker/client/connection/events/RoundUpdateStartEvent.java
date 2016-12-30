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
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Tim on 04.12.2016.
 */
public class RoundUpdateStartEvent extends ConnectionEvent{
	public String dealerId;
	public String smallBlindId;
	public String bigBlindId;
	public ArrayList<String[]> players;

	public RoundUpdateStartEvent(SocketConnection socketConnection, JSONObject data)
	{
		super(socketConnection, data);
	}
	
	@Override
	public void Build()
	{
		this.dealerId = GetData().getString("dealerId");
		this.smallBlindId = GetData().getString("smallBlind");
		this.bigBlindId = GetData().getString("bigBlind");

		JSONArray information = GetData().getJSONArray("information");
		players = new ArrayList<>();
		for(int i = 0; i < information.length(); i++) {
			players.add(new String[]{
					information.getJSONObject(i).getString("playerId"),
					Integer.toString(information.getJSONObject(i).getInt("money")),
					//Definitely total, bc round start
					Integer.toString(information.getJSONObject(i).getInt("roundBet"))
			});
		}
	}
}
