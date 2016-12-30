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
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Main {
	public static void main(String[] args)
	{
		
		System.out.printf( "       _                  _____      _             \n" +
				   "      | |                |  __ \\    | |            \n" +
				   "      | | __ ___   ____ _| |__) |__ | | _____ _ __ \n" +
				   "  _   | |/ _` \\ \\ / / _` |  ___/ _ \\| |/ / _ \\ '__|\n" +
				   " | |__| | (_| |\\ V / (_| | |  | (_) |   <  __/ |   \n" +
				   "  \\____/ \\__,_| \\_/ \\__,_|_|   \\___/|_|\\_\\___|_|   \n" +
				   "                                                   \n");
		
		
		ConnectionEventManager connectionEventManager = new ConnectionEventManager();
		connectionEventManager.AddListener(new Listener());
		
		SocketConnection socketConnection = new SocketConnection("localhost", 46337, connectionEventManager);
		socketConnection.start();
	}
}


class Listener extends ConnectionEventListener {
	private Player tempClientUntilTableIsReceived;
	private Table table;
	private ArrayList<OpenTable> openTables;
	private Scanner scanner;
	
	private final String SEPERATOR = "######################################################################";
	
	private ResourceBundle messages;
	
	Listener()
	{
		table = new Table();
		openTables = new ArrayList<>();
		scanner = new Scanner(System.in);
		
		Locale local_de_DE = new Locale("de", "DE");
		Locale local_en_US = new Locale("en", "US");
		
		messages = ResourceBundle.getBundle("Bundle", local_en_US);
	}
	
	private void printHeadLine(String headLine)
	{
		StringBuilder stringBuilder = new StringBuilder();
		
		int neededHashs = (SEPERATOR.length() - headLine.length())/2 - 1;
		
		for(int i = 0; i < neededHashs; i++)
			stringBuilder.append("#");
		
		stringBuilder.append(" " + headLine + " ");
		
		for(int i = 0; i < neededHashs; i++)
			stringBuilder.append("#");
		
		System.out.println("\n" + stringBuilder.toString() + "\n");
	}
	
	@Override
	public void OnLoginRequest(LoginRequestEvent event)
	{
		printHeadLine(messages.getString("LOGIN_headline"));
				
		String type = "";
		String typePhrase = "";
		while(!type.equals("R") && !type.equals("L")) {
			System.out.print(messages.getString("LOGIN_question"));
			type = scanner.nextLine().toUpperCase();
			if (type.equals("R")) {
				typePhrase = "REGISTER_REQUEST";
			} else if (type.equals("L")) {
				typePhrase = "LOGIN_REQUEST_ANSWER";
			} else {
				System.out.println(messages.getString("LOGIN_invalid"));
			}
		}
		
		System.out.print(messages.getString("LOGIN_user"));
		String userName = scanner.nextLine();
		
		System.out.print(messages.getString("LOGIN_password"));
		String password = scanner.nextLine();
		
		//hash the password
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(password.getBytes());
			byte[] digest = messageDigest.digest();
			StringBuilder stringBuffer = new StringBuilder();
			
			for (byte b : digest) {
				stringBuffer.append(String.format("%02x", b & 0xef));
			}
			
			password = stringBuffer.toString();
			
			event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", typePhrase)
								  .put("data", new JSONObject().put("username", userName)
									  .put("password", password)
								  )
			);
			
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void OnLoginResult(LoginResultEvent event)
	{
		printHeadLine(messages.getString("LOGINRESULT_headline"));
		
		if (event.validLogin) {
			System.out.println(messages.getString("LOGINRESULT_success"));
			tempClientUntilTableIsReceived = new Player();
			tempClientUntilTableIsReceived.id = event.playerId;
		} else {
			System.out.println(messages.getString("LOGINRESULT_fail"));
			System.out.println(messages.getString("LOGINRESULT_reason_"+event.reason));
		}
	}
	
	@Override
	public void OnLoginAcceptedPlayerSetup(LoginAcceptedPlayerSetup event)
	{
		printHeadLine(messages.getString("LOGINACCEPT_headline"));
		
		tempClientUntilTableIsReceived.id = event.playerId;
		tempClientUntilTableIsReceived.money = event.money;
		System.out.println(messages.getString("LOGINACCEPT_msg_server") + " " + event.playerId);
		System.out.println(messages.getString("LOGINACCEPT_msg_money") + " " + event.money);
	}
	
	@Override
	public void OnOpenTables(OpenTablesEvent event)
	{
		printHeadLine(messages.getString("OPENTABLES_headline"));
		this.openTables = event.openTables;
		
		String leftAlignFormat = "| %-15s | %-4d | %-4d |%n";
				
		if (openTables.size() != 0) {
			System.out.format("+-----------------+------+------+%n");
			System.out.format( "| "  + messages.getString("OPENTABLES_table") + "        "
					   +"| CUR  "
					   +"| MAX  |%n");
			System.out.format("+-----------------+------+------+%n");
			
			for (OpenTable table : openTables) {
				System.out.format(leftAlignFormat, table.tableId , table.currentPlayers, table.neededPlayers);
			}
			System.out.format("+-----------------+------+------+%n%n");
		} else {
			System.out.println(messages.getString("OPENTABLES_notables"));
		}
		
		
		System.out.print(messages.getString("OPENTABLES_question"));
		
		String input = scanner.nextLine().toUpperCase();
		
		if (input.equals("J")) {
			System.out.print(messages.getString("OPENTABLES_join"));
			int tableId = scanner.nextInt();
			scanner.nextLine();
			event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", "TABLE_JOIN_REQUEST")
								  .put("data", new JSONObject().put("tableId", tableId)));
		} else if (input.equals("C")) {
			System.out.print(messages.getString("OPENTABLES_create"));
			int neededPlayers = scanner.nextInt();
			
			event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", "CREATE_TABLE_REQUEST")
								  .put("data", new JSONObject().put("neededPlayers", neededPlayers)));
		} else if (input.equals("R")) {
			event.GetConnection().SendMessage(new JSONObject().put("op", 1)
								  .put("type", "OPEN_TABLES_REFRESH")
								  .put("data", new JSONObject()));
		}
		
	}
	
	@Override
	public void OnTableJoinEvent(TableJoinEvent event)
	{
		printHeadLine(messages.getString("TABLEJOIN_headline"));
		
		if (event.table == null) {
			System.out.println(messages.getString("TABLEJOIN_error"));
			return;
		}
		table = event.table;

		table.clientPlayer = tempClientUntilTableIsReceived;
		
		System.out.print(messages.getString("TABLEJOIN_table") + " " + table.tableId + " ");
		System.out.println(messages.getString("TABLEJOIN_with")+ " " + table.players.size() + " "
				   + messages.getString("TABLEJOIN_player"));
	}
	
	@Override
	public void OnTableLeaveEvent(TableLeaveEvent event)
	{
		printHeadLine(messages.getString("TABLELEAVE_headline"));
		System.out.println(messages.getString("TABLELEAVE_msg") + event.reason);
		System.exit(1);
	}
	
	@Override
	public void OnPlayerJoinsTableEvent(PlayerJoinsTableEvent event)
	{
		printHeadLine(messages.getString("PLAYERJOINSTABLE_headline"));
		
		if (!event.player.id.equals(table.clientPlayer.id)) {
			System.out.println(event.player.id + messages.getString("PLAYERJOINSTABLE_msg"));
			table.AddPlayer(event.player);
		}
	}
	
	@Override
	public void OnPlayerLeavesTableEvent(PlayerLeavesTableEvent event)
	{
		printHeadLine(messages.getString("PLAYERLEAVESTABLE_headline"));
		
		System.out.println(event.playerId + messages.getString("PLAYERLEAVESTABLE_msg_leave"));
		table.RemovePlayer(event.playerId);
		System.out.println(table.players.size());
		
		if (table.players.size() == 1) {
			System.out.print(messages.getString("PLAYERLEAVESTABLE_msg_win"));
			System.exit(420);
		}
	}
	
	@Override
	public void OnPlayerActionRequestEvent(PlayerActionRequestEvent event)
	{
		///*
		// printHeadLine(messages.getString("PLAYERACTION_headline"));
		System.out.println(messages.getString("PLAYERACTION_maxbet") + event.maximumBetAmount);

		String operation = "";
		int amount = 0;

		byte isCorrect = 0;
		while (isCorrect != 1) {

			System.out.print(messages.getString("PLAYERACTION_options"));

			for (BettingOperations op : event.operations) {
				System.out.print(op.name() + " ");
			}

			System.out.print("\n" + messages.getString("PLAYERACTION_choose") + " ");
			operation = scanner.nextLine().toUpperCase();

			if (!(operation.equals("FOLD") || operation.equals("CHECK") || operation.equals("CALL"))) {
				System.out.println(messages.getString("PLAYERACTION_betamount"));
				amount = Integer.parseInt(scanner.nextLine());
				if (amount > event.maximumBetAmount || amount < 0) {
					System.out.println(messages.getString("PLAYERACTION_invalid"));
					continue;
				}
				System.out.println(messages.getString("PLAYERACTION_correct"));
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
	  	//*/
		//table.b.SetPlayerChoice(true);
	}
	
	@Override
	public void OnRoundUpdateStartEvent(RoundUpdateStartEvent event)
	{
		printHeadLine(messages.getString("ROUNDUPDATESTART_headline"));
		
		table.dealerId = event.dealerId;
		table.b.SetDealer(table.dealerId);

		table.smallBlindId = event.smallBlindId;
		table.b.SetSmallBlind(table.smallBlindId);

		table.bigBlindId = event.bigBlindId;
		table.b.SetBigBlind(table.bigBlindId);

		table.b.ResetBoardCards();
		table.b.ResetPlayerCards();

		for (String[] s : event.players) {
			table.b.GetPlayerByName(s[0]).SetMoney(Integer.parseInt(s[1]));
			table.b.GetPlayerByName(s[0]).SetRoundBet(Integer.parseInt(s[2]));
			table.b.GetPlayerByName(s[0]).SetCurrentBet(Integer.parseInt(s[2]));
		}

		table.boardCards = new ArrayList<>();
	}
	
	@Override
	public void OnRoundUpdateCardDrawEvent(RoundUpdateCardDrawEvent event)
	{
		printHeadLine(messages.getString("ROUNDUPDATECARD_headline"));
		
		System.out.println(messages.getString("ROUNDUPDATECARD_msg") + " " + event.card);
		table.clientPlayer.cards.add(event.card);
		table.b.GetPlayerByName(table.clientPlayer.id).AddCard(event.card);
		table.b.AddDummyCards(table.clientPlayer.id);
		//TODO with GUI: Show other players retreived one too
	}
	
	@Override
	public void OnRoundUpdateRoundEvent(RoundUpdateRoundEvent event)
	{
		printHeadLine(messages.getString("ROUNDUPDATEROUND_headline"));
		System.out.println(messages.getString("ROUNDUPDATEROUND_round") + " " + event.newTurn);
		System.out.println(messages.getString("ROUNDUPDATEROUND_pot") + " " + event.pot);
	}
	
	@Override
	public void OnRoundUpdateShowdownPrePaymentEvent(RoundUpdateShowdownPrePaymentEvent event)
	{
		//Late
		//TODO will be implemented first in the server...
		//also see last commit, needs refactor
		printHeadLine(messages.getString("ROUNDUPDATESHOWDOWN1_headline"));
		
		for (String[] s : event.playerData) {
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
		printHeadLine(messages.getString("ROUNDUPDATESHOWDOWN2_headline"));
		
		for (String[] s : event.winnerData) {
			System.out.print(s[0] + " wins ");
			System.out.print(s[1] + " and newAmount ");
			System.out.println(s[2]);
		}
		
	}
	
	@Override
	public void OnRoundUpdatePlayerEvent(RoundUpdatePlayerEvent event)
	{
		printHeadLine(messages.getString("ROUNDUPDATEPLAYER_headline"));
		System.out.println(event.playerId + " " + messages.getString("ROUNDUPDATEPLAYER_action") + " " + event.action);
		System.out.println(event.playerId + " " + messages.getString("ROUNDUPDATEPLAYER_paid") + " "+ event.playerBetAmount);
		
		//TODO BettingOperation
		table.SetPlayerMoneyAmount(event.playerId, event.playerMoney);
		table.SetPlayerTotalBetAmount(event.playerId, event.totalPlayerBetAmount);
		table.SetPlayerCurrentBetAmount(event.playerId, event.currentRoundBet);


		table.pot = event.tablePotValue;
		table.b.SetTablePot(table.pot);

		table.roundBet = event.currentRoundBet;
		//Whooops missing in GUI, comes l8r
		
		System.out.println(messages.getString("ROUNDUPDATEPLAYER_pot") + " " + table.pot);
	}
	
	@Override
	public void OnRoundUpdateChooserPlayer(RoundUpdateChooserPlayer event)
	{
		printHeadLine(messages.getString("ROUNDUPDATECHOOSER_headline"));
		System.out.println(messages.getString("ROUNDUPDATECHOOSER_pot") + " " + event.pot);
		System.out.println(table.GetPlayerById(event.playerId).nickname + " " + messages.getString("ROUNDUPDATECHOOSER_msg"));
		table.b.SetPerformingPlayer(event.playerId);
	}
		
	@Override
	public void OnRoundUpdateNewBoardCard(RoundUpdateNewBoardCard event)
	{
		printHeadLine(messages.getString("ROUNDUPDATEBOARDCARD_headline"));
		
		System.out.println(messages.getString("ROUNDUPDATEBOARDCARD_card") + " " + event.card);
		table.boardCards.add(event.card);
		System.out.print(messages.getString("ROUNDUPDATEBOARDCARD_allcards"));
		for (String card : table.boardCards) {
			System.out.print(card + "   ");
		}
		System.out.println();
	}
}