package main.controllers;

import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javapoker.client.connection.ConnectionEventListener;
import javapoker.client.connection.SocketConnection;
import javapoker.client.connection.events.*;
import javapoker.client.game.Player;
import javapoker.client.game.Table;
import main.components.Component;
import main.components.mainPlayerObject.MainPlayerObject;
import main.components.playerObject.PlayerObject;
import main.components.tableObject.TableObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class GameController extends ConnectionEventListener implements Initializable {
	
	public Pane mainpane;
	private ArrayList<Component> playerObjects = new ArrayList<>();
	private Table table;
	
	private static final double WINDOW_WIDTH = 1280;
	private static final double WINDOW_HEIGHT = 720;
	private SocketConnection socketConnection;
	private int connectedPlayers;
	private boolean hasStarted = false;
	
	private ArrayList<Integer> freePlaces;
	private int mainPlayerPlace = 0;
	private TableObject tableObject;
	
	private Player lastPlayer;
	
	public GameController(SocketConnection socketConnection, Table table, Player player)
	{
		if (socketConnection != null && table != null) {
			this.socketConnection = socketConnection;
			this.table = table;
			this.table.clientPlayer = player;
			connectedPlayers = table.players.size();
			
			if (connectedPlayers == table.neededPlayers) {
				hasStarted = true;
			}
		} else {
			this.table = new Table();
			this.table.tableId = 1;
			this.table.neededPlayers = 5;
			this.table.players.add(new Player());
			this.table.players.add(new Player());
			this.table.players.add(new Player());
			connectedPlayers = 3;
		}
		
		freePlaces = new ArrayList<>();
		
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		mainpane.getChildren().clear();
		
		double centerX = WINDOW_WIDTH / 2;
		double centerY = WINDOW_HEIGHT / 2 - 20;
		
		tableObject = new TableObject(centerX, centerY);
		mainpane.getChildren().add(tableObject);
		
		
		double angle = 90;
		double addAngle = 360 / table.neededPlayers;
		double dist = 120;
		
		double startAngle = 90 + addAngle * (table.neededPlayers - table.players.size() + 1);
		
		for (int i = 0; i < table.neededPlayers; i++) {
			double angleRad = startAngle * Math.PI / 180;
			double newX = centerX + (TableObject.AXIS_X + dist) * Math.cos(angleRad);
			double newY = centerY + (TableObject.AXIS_Y + dist) * Math.sin(angleRad);
			
			Component temp;
			if (i < table.players.size() - 1) {
				temp = new PlayerObject(table.players.get(i), newX, newY);
			} else if (i == table.players.size() - 1) {
				temp = new MainPlayerObject(table.players.get(i), newX, newY, null, table);
				mainPlayerPlace = i;
				table.clientPlayer = table.players.get(i);
			} else {
				temp = new PlayerObject(null, newX, newY);
				temp.setDisable(true);
				freePlaces.add(i);
			}
			
			playerObjects.add(temp);
			mainpane.getChildren().add(temp);
			
			startAngle += addAngle;
		}
	}
	
	@Override
	public void OnTableLeaveEvent(TableLeaveEvent event)
	{
		System.exit(240);
	}
	
	@Override
	public void OnPlayerJoinsTableEvent(PlayerJoinsTableEvent event)
	{
		//get free position
		System.out.println(Arrays.toString(freePlaces.toArray()));
		int freePosition;
		Optional<Integer> op = freePlaces.stream().filter(value -> value > mainPlayerPlace).sorted().findFirst();
		if (op.isPresent()) {
			freePosition = op.get();
		} else {
			freePosition = freePlaces.stream().filter(value -> value < mainPlayerPlace).sorted().findFirst().get();
		}
		
		PlayerObject playerObject = (PlayerObject) playerObjects.get(freePosition);
		playerObject.setupPlayer(event.player);
		playerObject.setDisable(false);
		connectedPlayers++;
		
		table.AddPlayer(event.player);
		
		if (connectedPlayers == table.neededPlayers) {
			hasStarted = true;
		}
		
		freePlaces.remove(0);
	}
	
	@Override
	public void OnPlayerLeavesTableEvent(PlayerLeavesTableEvent event)
	{
		if (hasStarted) {
			System.out.println("player left table during game");
			//todo entfernen während das spiel läuft
		} else {
			System.out.println("player left table durng waiting time");
			
			//todo make the visual order correct!
			
			final int[] i = {0};
			table.players.forEach(p -> {
				if (p.nickname.equals(event.playerId)) {
					i[0] = table.players.indexOf(p);
				}
			});
			
			((PlayerObject) playerObjects.get(i[0])).setupPlayer(null);
			connectedPlayers--;
			
			freePlaces.add(i[0]);
			
			table.RemovePlayer(event.playerId);
			
			System.out.println("done with place " + i[0]);
		}
	}
	
	@Override
	public void OnPlayerActionRequestEvent(PlayerActionRequestEvent event)
	{
		//// TODO: 29.12.2016 yep...
		super.OnPlayerActionRequestEvent(event);
	}
	
	@Override
	public void OnRoundUpdateStartEvent(RoundUpdateStartEvent event)
	{
		
		//// TODO: 29.12.2016 Reset everything!!! 
		
		table.dealerId = event.dealerId;
		table.smallBlindId = event.smallBlindId;
		table.bigBlindId = event.bigBlindId;
		table.boardCards = new ArrayList<>();
		
		table.GetPlayerById(event.dealerId).playerObject.SetDealer();
		table.GetPlayerById(event.smallBlindId).playerObject.SetSmallBlind();
		table.GetPlayerById(event.bigBlindId).playerObject.SetBigBlind();
	}
	
	@Override
	public void OnRoundUpdateCardDrawEvent(RoundUpdateCardDrawEvent event)
	{
		ArrayList<String> cards = table.clientPlayer.cards;
		cards.add(event.card);
		if (cards.size() == 1) {
			table.GetPlayerById(table.clientPlayer.id).playerObject.SetCard1(event.card);
		} else if (cards.size() == 2) {
			table.GetPlayerById(table.clientPlayer.id).playerObject.SetCard2(event.card);
		} else {
			System.err.println("Player draws to many cards");
		}
		
	}
	
	@Override
	public void OnRoundUpdateRoundEvent(RoundUpdateRoundEvent event)
	{
		tableObject.SetRoundName(event.newTurn);
		tableObject.UpdatePotValue(event.pot);
	}
	
	@Override
	public void OnRoundUpdateShowdownPrePaymentEvent(RoundUpdateShowdownPrePaymentEvent event)
	{
		//// TODO: 29.12.2016 should be implemented soon
		System.err.println("not implemented yet");
	}
	
	@Override
	public void OnRoundUpdateShowdownPostPaymentEvent(RoundUpdateShowdownPostPaymentEvent event)
	{
		//// TODO: 29.12.2016 should be implemented soon
		System.err.println("not implemented yet");
	}
	
	@Override
	public void OnRoundUpdatePlayerEvent(RoundUpdatePlayerEvent event)
	{
		Player player = table.GetPlayerById(event.playerId);
		player.playerObject.SetStatusLabel(event.action);
		player.playerObject.SetBetAmountLabel(event.totalPlayerBetAmount + "(" + event.playerBetAmount + ")");
		
		table.SetPlayerMoneyAmount(event.playerId, event.playerMoney);
		table.SetPlayerTotalBetAmount(event.playerId, event.totalPlayerBetAmount);
		table.SetPlayerCurrentBetAmount(event.playerId, event.currentRoundBet);
		table.pot = event.tablePotValue;
		table.roundBet = event.currentRoundBet;
		
		tableObject.UpdatePotValue(event.tablePotValue);
		tableObject.UpdateMinimumBet(event.currentRoundBet);
	}
	
	@Override
	public void OnRoundUpdateChooserPlayer(RoundUpdateChooserPlayer event)
	{
		Player currentPlayer = table.GetPlayerById(event.playerId);
		if (lastPlayer != null) {
			lastPlayer.playerObject.SetTurn(false);
		}
		currentPlayer.playerObject.SetTurn(true);
		lastPlayer = currentPlayer;
	}
	
	@Override
	public void OnRoundUpdateNewBoardCard(RoundUpdateNewBoardCard event)
	{
		table.boardCards.add(event.card);
		tableObject.AddCardToBoard(event.card);
	}
	
	
}