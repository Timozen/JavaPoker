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

package connection.client;

import connection.ConnectionEventManager;
import connection.events.ClientConnectEvent;
import connection.events.ClientDisconnectEvent;
import connection.utils.ConnectionEventBuilder;
import game.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
	
	private BufferedReader input;
	private PrintWriter output;
	private Socket socket;
	private ConnectionEventManager connectionEventManager;
	private ConnectionEventBuilder connectionEventBuilder;
	
	private String username;
	private boolean loggedIn;
	private Player player;

	public Client(Socket socket, ConnectionEventManager connectionEventManager)
	{
		this.socket = socket;
		this.connectionEventManager = connectionEventManager;
		this.connectionEventBuilder = new ConnectionEventBuilder(connectionEventManager, this);
		this.loggedIn = false;
	}
	
	private boolean init()
	{
		try {
			input  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public void run()
	{
		if (init()) {
			connectionEventManager.handle(new ClientConnectEvent(this, null));
			//TODO log-in
		} else {
			System.out.println("Client could not be initiated!");
			return;
		}
		
		try {
			//SendMessage("Welcome!");
			while(true){
				String msg = input.readLine();
				if(msg == null){
					break;
				}
				connectionEventBuilder.CreateEvent(msg);
			}
		} catch (IOException ex){
			//ex.printStackTrace();
		} finally {
			try {
				socket.close();
				connectionEventManager.handle(new ClientDisconnectEvent(this, null));
				//TODO Fire disconnect event
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
	
	public void SendMessage(String msg)
	{
		output.println(msg);
	}
	
	public Client SendMessage(JSONObject obj)
	{
		output.println(obj.toString());
		return this;
	}
		
	public void LoginRequest()
	{
		SendMessage(new JSONObject().put("op", 1).put("type", "LOGIN_REQUEST").put("data", new JSONObject()));
	}
	
	public void SetUpAfterLogIn(String username, boolean loggedIn)
	{
		this.username = username;
		this.loggedIn = loggedIn;
		this.player = new Player(username);
		this.player.SetConnectionClient(this);

		this.SendMessage(new JSONObject()
				.put("op", "1")
				.put("type", "LOGIN_ACCEPTED_PLAYER_SETUP")
				.put("data", new JSONObject()
					.put("money", this.player.GetMoney())
					.put("playerId", this.GetPlayerId())
				)
		);
	}

	public void OpenTablesAnswer(JSONArray tables)
	{
		SendMessage(new JSONObject()
		.put("op", 1)
		.put("data", new JSONObject().put("tables", tables))
		.put("type", "OPEN_TABLES")
		);
	}
	//region Getter and Setter
	
	
	public Player GetPlayer()
	{
		return player;
	}
	
	public String GetPlayerId()
	{
		return username;
	}

	public String GetUsername()
	{
		return username;
	}

	public void SetUsername(String username)
	{
		this.username = username;
	}
	
	public boolean IsLoggedIn()
	{
		return loggedIn;
	}
	
	public void SetLoggedIn(boolean loggedIn)
	{
		this.loggedIn = loggedIn;
	}
	
	//endregion
	
	
}
