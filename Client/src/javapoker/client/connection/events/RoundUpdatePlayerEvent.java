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
import org.json.JSONObject;

/**
 * Created by Tim on 04.12.2016.
 */
public class RoundUpdatePlayerEvent extends ConnectionEvent{
	public int playerBetAmount;
	public int totalPlayerBetAmount;
	public int currentRoundBet;
	public int tablePotValue;
	public String playerId;
	public int playerMoney;
	public RoundUpdatePlayerEvent(SocketConnection socketConnection, JSONObject data)
	{
		super(socketConnection, data);
	}
	
	@Override
	public void Build()
	{
		JSONObject data = GetData();
		playerBetAmount = data.getInt("betAmount");
		totalPlayerBetAmount = data.getInt("totalBetAmount");
		currentRoundBet = data.getInt("currentRoundBet");
		tablePotValue = data.getInt("pot");
		playerId = data.getString("playerId");
		playerMoney = data.getInt("money");
	}
}
