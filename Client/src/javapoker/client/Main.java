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
import javapoker.client.game.BettingOperations;
import javapoker.client.game.OpenTable;
import javapoker.client.game.Table;
import org.json.JSONObject;

import java.util.ArrayList;

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
	Table table;
	ArrayList<OpenTable> openTables;

	Listener() {
		table = new Table();
		openTables = new ArrayList<>();
	}

	@Override
	public void OnLoginRequest(LoginRequestEvent event)
	{
		System.out.println("Received LoginRequest");
		event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", "LOGIN_REQUEST_ANSWER")
								  .put("data", new JSONObject().put("username", "Test")
											       .put("password", "1234")
								  )
				//TODO implement pwd and uname set by client
						 );
	}
	
	@Override
	public void OnLoginResult(LoginResultEvent event)
	{
		System.out.println("Received LoginResult");
		System.out.println("Information is: " + event.validLogin);
		if (event.validLogin) {
			table.playerId = event.playerId;
		}
	}
	
	@Override
	public void OnOpenTables(OpenTablesEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		this.openTables = event.openTables;
	}
	
	@Override
	public void OnTableJoinEvent(TableJoinEvent event)
	{
		System.out.println("Received TableJoin");
		if (event.table == null) {
			System.out.println("Error - Cannot connect to table");
			return;
		}
		table = event.table;
		
		System.out.println("table id:" + table.tableId);
		System.out.println("with " + table.players.size() + " players");
	}
	
	@Override
	public void OnTableLeaveEvent(TableLeaveEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println("Reason: " + event.reason);
	}
		
	@Override
	public void OnPlayerJoinsTableEvent(PlayerJoinsTableEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		if (!event.player.id.equals(table.playerId)) {
			table.AddPlayer(event.player);
		}
	}
	
	@Override
	public void OnPlayerLeavesTableEvent(PlayerLeavesTableEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		table.RemovePlayer(event.playerId);
	}
	
	@Override
	public void OnPlayerActionRequestEvent(PlayerActionRequestEvent event)
	{
		//TODO Eingabe
		event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								.put("type", "PLAYER_ACTION_ANSWER")
								.put("data", new JSONObject()
									.put("tableId", table.tableId)
									.put("action", BettingOperations.FOLD)
									.put("betAmount", 0)
									.put("isAllIn", false)
								));
	}
	
	@Override
	public void OnRoundUpdateStartEvent(RoundUpdateStartEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		table.dealerId = event.dealerId;
		table.smallBlindId = event.smallBlindId;
		table.bigBlindId = event.bigBlindId;
	}
	
	@Override
	public void OnRoundUpdateCardDrawEvent(RoundUpdateCardDrawEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		table.GetPlayerById(table.playerId).cards.add(event.card);
	}
	
	@Override
	public void OnRoundUpdateRoundEvent(RoundUpdateRoundEvent event)
	{
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdateShowdownEvent(RoundUpdateShowdownEvent event)
	{
		//Late
		//TODO will be implemented first in the server...
		//also see last commit, needs refactor
		System.out.println("Not implemented " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		
	}
	
	@Override
	public void OnRoundUpdatePlayerEvent(RoundUpdatePlayerEvent event)
	{	//TODO new Datatype
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println(event.playerId + " paid " + event.playerBetAmount);

		//TODO Print betting Operation & if fold then set state fold
		//TODO Set player GameState
		System.out.println(event.playerId + " put " + event.playerBetAmount);
		table.SetPlayerMoneyAmount(event.playerId, event.playerMoney);
		table.SetPlayerTotalBetAmount(event.playerId, event.totalPlayerBetAmount);


		table.roundBet = event.currentRoundBet;
		table.pot = event.tablePotValue;

	}
}