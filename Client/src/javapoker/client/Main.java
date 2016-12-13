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
import javapoker.client.game.Player;
import javapoker.client.game.Table;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	public static void main(String[] args)
	{
		ConnectionEventManager connectionEventManager = new ConnectionEventManager();
		connectionEventManager.AddListener(new Listener());
		
		SocketConnection socketConnection = new SocketConnection("localhost", 46337, connectionEventManager);
		socketConnection.start();
	}
}


class Listener extends ConnectionEventListener {
	Player tempClientUntilTableIsReceived;
	Table table;
	ArrayList<OpenTable> openTables;

	Listener() {
		table = new Table();
		openTables = new ArrayList<>();
	}

	@Override
	public void OnLoginRequest(LoginRequestEvent event)
	{
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Received LoginRequest");

		System.out.print("Username: ");
		String userName = scanner.nextLine();
		
		System.out.print("Password: ");
		String password = scanner.nextLine();
		
		//hash the password
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(password.getBytes());
			byte[] digest = messageDigest.digest();
			StringBuilder stringBuffer = new StringBuilder();
			
			for(byte b : digest){
				stringBuffer.append(String.format("%02x", b & 0xef));
			}
			
			password = stringBuffer.toString();
			
			event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", "LOGIN_REQUEST_ANSWER")
								  .put("data", new JSONObject().put("username", userName)
									  		       .put("password", password)
								  )
			);
			
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
	
	@Override
	public void OnLoginResult(LoginResultEvent event)
	{
		System.out.println("Received LoginResult");
		System.out.println("Information is: " + event.validLogin);
		if (event.validLogin) {
			tempClientUntilTableIsReceived = new Player();
			tempClientUntilTableIsReceived.id = event.playerId;
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
		table.clientPlayer = tempClientUntilTableIsReceived;
		
		System.out.println("table id:" + table.tableId);
		System.out.println("with " + table.players.size() + " players");
	}
	
	@Override
	public void OnTableLeaveEvent(TableLeaveEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println("You got disconnected from table: " + event.reason);
		System.exit(1);
	}
		
	@Override
	public void OnPlayerJoinsTableEvent(PlayerJoinsTableEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		if (!event.player.id.equals(table.clientPlayer.id)) {
			System.out.println("Player with id " + event.player.id + " joined.");
			table.AddPlayer(event.player);
		}
	}
	
	@Override
	public void OnPlayerLeavesTableEvent(PlayerLeavesTableEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println("Player " + event.playerId + " left table because " + event.reason);
		table.RemovePlayer(event.playerId);
		System.out.println(table.players.size());
		if (table.players.size() == 1) {
			System.out.print("You were the last man standing. Good Job, time to say goodbye.");
			System.exit(420);
		}
	}
	
	@Override
	public void OnPlayerActionRequestEvent(PlayerActionRequestEvent event)
	{
		System.out.println("Your MaxBet is: " + event.maximumBetAmount);

		Scanner scanner = new Scanner(System.in);;
		String operation = null;
		int amount = 0;

		byte isCorrect = 0;
		while(isCorrect != 1) {
			System.out.print("Choose Action ");
			for(BettingOperations op : event.operations) {System.out.print(op.name() + " ");}
			operation = scanner.nextLine();

			if (!(operation.equals("FOLD") || operation.equals("CHECK") || operation.equals("CALL"))) {
				System.out.println("Input Betamount: ");
				amount = Integer.parseInt(scanner.nextLine());
				if (amount > event.maximumBetAmount || amount < 0) {
					System.out.println("Incorrect amount.");
					continue;
				}
				System.out.println("Correct Input: 0 || 1");
				if (Integer.parseInt(scanner.nextLine()) == 1) {
					isCorrect = 1;
				}
			} else {
				isCorrect = 1;
			}
		}

		event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								.put("type", "PLAYER_ACTION_ANSWER")
								.put("data", new JSONObject()
									.put("tableId", table.tableId)
									.put("action", operation)
									.put("betAmount", amount)
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
		table.boardCards = new ArrayList<>();
		//Später mit UI: Anpassung der Leute die das sind im GUI
	}
	
	@Override
	public void OnRoundUpdateCardDrawEvent(RoundUpdateCardDrawEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println("Player received " + event.card);
		table.clientPlayer.cards.add(event.card);
		//TODO with GUI: Show other players retreived one too
	}
	
	@Override
	public void OnRoundUpdateRoundEvent(RoundUpdateRoundEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println("---------------------------");
		System.out.println("ROUND: " + event.newTurn);
		System.out.println("Pot: " + event.pot);
		
	}

	@Override
	public void OnRoundUpdateShowdownPrePaymentEvent(RoundUpdateShowdownPrePaymentEvent event)
	{
		//Late
		//TODO will be implemented first in the server...
		//also see last commit, needs refactor
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		for(String[] s : event.playerData) {
			System.out.print(s[0] + " with ");
			System.out.print(s[1] + " and ");
			System.out.println(s[2]);
		}

	}


	@Override
	public void OnRoundUpdateShowdownPostPaymentEvent(RoundUpdateShowdownPostPaymentEvent event)
	{
		//Late
		//TODO will be implemented first in the server...
		//also see last commit, needs refactor
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		for(String[] s : event.winnerData) {
			System.out.print(s[0] + " wins ");
			System.out.print(s[1] + " and newAmount ");
			System.out.println(s[2]);
		}

	}

	@Override
	public void OnRoundUpdatePlayerEvent(RoundUpdatePlayerEvent event)
	{
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println(event.playerId + " paid " + event.playerBetAmount);
		System.out.println(event.playerId + " did " + event.action);

		//TODO BettingOperation
		table.SetPlayerMoneyAmount(event.playerId, event.playerMoney);
		table.SetPlayerTotalBetAmount(event.playerId, event.totalPlayerBetAmount);
		table.SetPlayerCurrentBetAmount(event.playerId, event.currentRoundBet);
		table.pot = event.tablePotValue;
		table.roundBet = event.currentRoundBet;

		System.out.println("New Pot: " + table.pot);
	}

	@Override
	public void OnRoundUpdateChooserPlayer(RoundUpdateChooserPlayer event) {
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		System.out.println("Pot Value: " + event.pot);
		System.out.println(table.GetPlayerById(event.playerId).nickname + " makes choice...");
	}

	@Override
	public void OnLoginAcceptedPlayerSetup(LoginAcceptedPlayerSetup event) {
		System.out.println("Triggered " + (new Object() {}.getClass().getEnclosingMethod().getName()));
		tempClientUntilTableIsReceived.id = event.playerId;
		tempClientUntilTableIsReceived.money = event.money;
		System.out.println("Welcome on our Server, " + event.playerId);
		System.out.println("Your money amount is " + event.money);
	}

	@Override
	public void OnRoundUpdateNewBoardCard(RoundUpdateNewBoardCard event) {
		System.out.println("New Boardcard received " + event.card);
		table.boardCards.add(event.card);
		System.out.print("Table Cards: ");
		for(String card : table.boardCards) {
			System.out.print(card + "   ");
		}
		System.out.println();
	}
}