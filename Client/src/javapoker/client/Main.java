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

package javapoker.client;

import javapoker.client.connection.ConnectionEventListener;
import javapoker.client.connection.ConnectionEventManager;
import javapoker.client.connection.SocketConnection;
import javapoker.client.connection.events.*;
import org.json.JSONObject;

public class Main {
	
	public static void main(String[] args)
	{
		ConnectionEventManager connectionEventManager = new ConnectionEventManager();
		connectionEventManager.AddListener(new Listener());
		
		SocketConnection socketConnection = new SocketConnection("localhost", 9090, connectionEventManager);
		socketConnection.start();
	}
}


class Listener extends ConnectionEventListener {
		
	@Override
	public void OnLoginRequest(LoginRequestEvent event)
	{
		System.out.println("Received LoginRequest");
		event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", "LOGIN_REQUEST_ANSWER")
								  .put("data", new JSONObject().put("username", "Test")
											       .put("password", "1234")
								  )
						 );
	}
	
	@Override
	public void OnLoginResult(LoginResultEvent event)
	{
		System.out.println("Received LoginResult");
		System.out.println("Information is: " + event.validLogin);
		super.OnLoginResult(event);
	}
	
	@Override
	public void OnOpenTables(OpenTablesEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
	}
	
	@Override
	public void OnTableJoinEvent(TableJoinEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
	}
	
	@Override
	public void OnTableLeaveEvent(TableLeaveEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
	}
		
	@Override
	public void OnPlayerJoinsTableEvent(PlayerJoinsTableEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnPlayerLeavesTableEvent(PlayerLeavesTableEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnPlayerActionRequestEvent(PlayerActionRequestEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdateStartEvent(RoundUpdateStartEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdateCardDrawEvent(RoundUpdateCardDrawEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdateRoundEvent(RoundUpdateRoundEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdateShowdownEvent(RoundUpdateShowdownEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdatePlayerEvent(RoundUpdatePlayerEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
}