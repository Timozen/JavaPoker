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

package javapoker.client.connection.utils;

import javapoker.client.connection.ConnectionEventManager;
import javapoker.client.connection.SocketConnection;
import javapoker.client.connection.events.*;
import org.json.JSONObject;

public class ConnectionEventBuilder {
	
	private ConnectionEventManager connectionEventManager;
	private SocketConnection socketConnection;
	
	public ConnectionEventBuilder(ConnectionEventManager connectionEventManager, SocketConnection socketConnection)
	{
		this.connectionEventManager = connectionEventManager;
		this.socketConnection = socketConnection;
	}
	
	public void CreateEvent(String msg)
	{
		JSONObject obj = new JSONObject(msg);
		JSONObject data = obj.getJSONObject("data");
		
		switch (obj.getString("type")) {
			case "LOGIN_REQUEST":
				connectionEventManager.handle(new LoginRequestEvent(socketConnection, data));
				break;
			case "LOGIN_RESULT":
				connectionEventManager.handle(new LoginResultEvent(socketConnection, data));
				break;
			case "OPEN_TABLES":
				connectionEventManager.handle((new OpenTablesEvent(socketConnection, data)));
				break;
			case "ON_TABLE_JOIN":
				connectionEventManager.handle((new TableJoinEvent(socketConnection, data)));
				break;
			case "ON_TABLE_LEAVE":
				connectionEventManager.handle((new TableLeaveEvent(socketConnection, data)));
				break;
			case "PLAYER_JOINS_TABLE":
				connectionEventManager.handle((new PlayerJoinsTableEvent(socketConnection, data)));
				break;
			case "PLAYER_LEAVES_TABLE":
				connectionEventManager.handle((new PlayerLeavesTableEvent(socketConnection, data)));
				break;
			case "PLAYER_ACTION_REQUEST":
				connectionEventManager.handle((new PlayerActionRequestEvent(socketConnection, data)));
				break;
			case "ROUND_UPDATE_START":
				connectionEventManager.handle((new RoundUpdateStartEvent(socketConnection, data)));
				break;
			case "ROUND_UPDATE_CARD_DRAW":
				connectionEventManager.handle((new RoundUpdateCardDrawEvent(socketConnection, data)));
				break;
			case "ROUND_UPDATE_ROUND":
				connectionEventManager.handle((new RoundUpdateRoundEvent(socketConnection, data)));
				break;
			case "ROUND_UPDATE_SHOWDOWN":
				connectionEventManager.handle((new RoundUpdateShowdownEvent(socketConnection, data)));
				break;
			case "ROUND_UPDATE_PLAYER":
				connectionEventManager.handle((new RoundUpdatePlayerEvent(socketConnection, data)));
				break;
			case "ROUND_UPDATE_CHOOSER_PLAYER":
				connectionEventManager.handle((new RoundUpdateChooserPlayer(socketConnection, data)));
				break;
			case "LOGIN_ACCEPTED_PLAYER_SETUP":
				connectionEventManager.handle((new LoginAcceptedPlayerSetup(socketConnection, data)));
				break;
			default:
				System.out.println("Unknown event type: " + obj.getString("type") + " not implemented or spelling mistake");
				break;
		}
	}
}
