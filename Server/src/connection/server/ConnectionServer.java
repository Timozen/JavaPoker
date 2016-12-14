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
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
		Client client = event.GetClient();
		
		if (client.IsLoggedIn()) {
			System.out.println("Player was logged in.");
			client.GetPlayer().SetHasDisconnected(true);
			
			Table table = event.GetClient().GetPlayer().GetTable();
			if(table == null) {
				System.out.println("The player is not part of a table");
			} else {
				if(!table.HasStarted()) {
					System.out.println("The player was part of table, remove from table, because it" +
							   " didnt started yet");
					table.RemovePlayerFromTable(client.GetPlayer(), true, "Disconnected by client");
				}
			}
		}
	}
	
	@Override
	public void OnLoginRequestAnswerEvent(LoginRequestAnswerEvent event)
	{
		boolean valid = false;
		String reason = "";
		
		System.out.println("A client has send its login information");
		
		try {
			String fileString = readFile("src/users.json", StandardCharsets.UTF_8);
			JSONArray obj = new JSONArray(fileString);
			JSONObject userObj = obj.getJSONObject(0);
			boolean found = false;
			for(int i = 0; i < obj.length(); i++) {
				userObj = obj.getJSONObject(i);
				
				if (userObj.getString("username").equals(event.username)) {
					found = true;
					break;
				}
			}
			if(found) {
				if(userObj.getString("passwordHash").equals(event.password)) {
					valid = true;
				} else {
					reason = "Wrong password";
				}
				
			} else {
				reason = "Wrong username";
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		event.GetClient().SendMessage( new JSONObject().put("op", 1)
								.put("type", "LOGIN_RESULT")
								.put("data", new JSONObject()
										.put("valid", valid)
										.put("playerId", event.username)
										.put("reason", reason)
								)
					     );
		if(valid) {
			System.out.println("Log in was successfull");
			//setup player and add client to hashmap
			event.GetClient().SetUpAfterLogIn(event.username, true);
			connectedClients.put(event.username, event.GetClient());
			
			//TODO fill the first not full table
			tables.get(0).AddPlayerToTable(event.GetClient().GetPlayer());
		} else {
			System.out.println("Log in was unsuccessfull");
			//todo ggf neues login request senden?
		}
	}
	
	@Override
	public void OnRegisterRequestEvent(RegisterRequestEvent event)
	{
		try {
			String fileString = readFile("src/users.json", StandardCharsets.UTF_8);
			JSONArray array = new JSONArray(fileString);
			
			//TODO check for username already in use!!!
			
			JSONObject newUser = new JSONObject().put("username", event.username)
								.put("passwordHash", event.password)
								.put("loggedIn", false);
			array.put(newUser);
			
			try(  PrintWriter out = new PrintWriter("src/users.json")  ){
				out.println(array.toString(1));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			event.GetClient().SendMessage( new JSONObject().put("op", 1)
							       .put("type", "LOGIN_RESULT")
							       .put("data", new JSONObject()
								       .put("valid", true)
								       .put("playerId", event.username)
								       .put("reason", "Register successfull")
							       )
			);
			
			
			System.out.println("Register was successfull");
			//setup player and add client to hashmap
			event.GetClient().SetUpAfterLogIn(event.username, true);
			connectedClients.put(event.username, event.GetClient());
			
			//TODO fill the first not full table
			tables.get(0).AddPlayerToTable(event.GetClient().GetPlayer());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
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
	
	@Override
	public void OnCreateTableRequestEvent(CreateTableRequestEvent event)
	{
		Table table = CreateNewTable(event.neededPlayers);
		table.AddPlayerToTable(event.GetClient().GetPlayer());
	}
	
	public Table CreateNewTable(int playerCount)
	{
		Table table = new Table(tables.size(), playerCount);
		tables.put(idCounter, table);
		idCounter++;

		Thread thread = new Thread(table);
		thread.start();
	
		return table;
	}
	
	
	private String readFile(String path, Charset encoding)throws IOException
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
