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

package connection.server;

import connection.ConnectionEventManager;
import connection.events.*;
import connection.client.Client;
import game.Table;
import game.models.BettingOperations;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class ConnectionServer extends Server {
	
	private HashMap<String, Client> connectedClients;
	private ConnectionEventManager connectionEventManager;
	private HashMap<Integer, Table> tables = new HashMap<>();
	private int idCounter;
	
		
	public ConnectionServer(int port)
	{
		super(port);
		connectedClients = new HashMap<>();
		idCounter = 0;
	}
		
	@Override
	public void run()
	{
		boolean successfulStart = init();
		connectionEventManager = new ConnectionEventManager();
		connectionEventManager.AddListener(this);
		
		if (successfulStart) {
			System.out.println("The connection server has started and listens on port: " + GetPort());
		} else {
			System.out.println("The connection server could not start!");
			return;
		}
		
		try {
			while (true) {
				Socket newClientSocket = GetSocketListener().accept();
				Client client = new Client(newClientSocket, connectionEventManager);
				//connectedClients.put(id, client);
				client.start();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				GetSocketListener().close();
			} catch (IOException ex){
				ex.printStackTrace();
			} finally {
				System.out.println("The connection server could not be shut down!");
			}
		}
	}
	
	@Override
	public void OnClientConnectEvent(ClientConnectEvent event)
	{
		Client client = event.GetClient();
		
		System.out.println("A new client has connected");
		System.out.println("Send login request");
		
		client.LoginRequest();
	}
	
	@Override
	public void OnClientDisconnectEvent(ClientDisconnectEvent event)
	{
		System.out.println("A client has disconnected!");
		if (event.GetClient().IsLoggedIn()) {
			System.out.println("Player was logged in.");
			event.GetClient().GetPlayer().SetHasDisconnected(true);
		}
	}
	
	@Override
	public void OnLoginRequestAnswerEvent(LoginRequestAnswerEvent event)
	{
		System.out.println("A client has send its login information");
		System.out.println("The information is:");
		System.out.println("Username: " + event.username);
		System.out.println("Password: " + event.password);

		//TODO check if valid
		
		event.GetClient().SendMessage( new JSONObject().put("op", 1)
								.put("type", "LOGIN_RESULT")
								.put("data", new JSONObject()
										.put("valid", true)
										.put("playerId", event.username)
										.put("reason", "correct")
								)
					     );
		//setup player and add client to hashmap
		event.GetClient().SetUpAfterLogIn(event.username, true);
		connectedClients.put(event.username, event.GetClient());
	
		//TODO fill the first not full table
		tables.get(0).AddPlayerToTable(event.GetClient().GetPlayer());
	}
	
	@Override
	public void OnPlayerActionAnswerEvent(PlayerActionAnswerEvent event)
	{
		Table t = tables.get(event.tableId);
		t.receivedAnswer = true;
		event.GetClient().GetPlayer().SetBettingAction(event.action);
		event.GetClient().GetPlayer().SetBetAmount(event.betAmount);
	}

	@Override
	public void OnOpenTablesRefreshEvent(OpenTablesRefreshEvent event)
	{
		ArrayList<Integer> tableList = new ArrayList<>();
		for(HashMap.Entry<Integer, Table> entry : tables.entrySet()) {
			if (!entry.getValue().HasStarted()) {
				tableList.add(entry.getKey());
			}
		}
		event.GetClient().OpenTablesAnswer(new JSONArray(tableList));
	}

	@Override
	public void OnTableJoinRequestEvent(TableJoinRequestEvent event)
	{
		if (!tables.get(event.tableId).HasStarted()) {
			//let player join
			tables.get(event.tableId).AddPlayerToTable(event.GetClient().GetPlayer());
			//Table fires ON_TABLE_JOIN
			//Table fires PLAYER_JOINS_TABLE
		} else {
			event.GetClient().SendMessage(tables.get(event.tableId).GenerateJoinAnswer(false));
		}

	}

	public void CreateNewTable(int playerCount)
	{
		Table table = new Table(tables.size(), playerCount);
		tables.put(idCounter, table);
		idCounter++;

		Thread thread = new Thread(table);
		thread.start();

		//table.AddPlayerToTable("Vogel");
		//table.AddPlayerToTable("Grajetzki");
		//table.AddPlayerToTable("Neumann");
	}
	
}
