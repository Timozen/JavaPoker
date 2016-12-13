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

package connection.utils;

import connection.ConnectionEventManager;
import connection.client.Client;
import connection.events.LoginRequestAnswerEvent;
import connection.events.PlayerActionAnswerEvent;
import connection.events.RegisterRequestEvent;
import connection.events.TableJoinRequestEvent;
import org.json.JSONObject;

public class ConnectionEventBuilder {
	
	private ConnectionEventManager connectionEventManager;
	private Client client;
	
	public ConnectionEventBuilder(ConnectionEventManager connectionEventManager, Client client)
	{
		this.connectionEventManager = connectionEventManager;
		this.client = client;
	}
	
	public void CreateEvent(String msg)
	{
		JSONObject obj = new JSONObject(msg);
		JSONObject data = obj.getJSONObject("data");
		switch (obj.getString("type")) {
			case "LOGIN_REQUEST_ANSWER":
				connectionEventManager.handle(new LoginRequestAnswerEvent(client, data));
				break;
			case "PLAYER_ACTION_ANSWER":
				connectionEventManager.handle(new PlayerActionAnswerEvent(client, data));
				break;
			case "TABLE_JOIN_REQUEST":
				connectionEventManager.handle(new TableJoinRequestEvent(client, data));
				break;
			case "REGISTER_REQUEST":
				connectionEventManager.handle(new RegisterRequestEvent(client, data));
				break;
			default:
				System.out.println("Unknown event type: " + obj.getString("type"));
				break;
		}
	}
}
