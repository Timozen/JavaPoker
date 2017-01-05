package main.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javapoker.client.connection.ConnectionEventListener;
import javapoker.client.connection.SocketConnection;
import javapoker.client.connection.events.LoginAcceptedPlayerSetup;
import javapoker.client.connection.events.OpenTablesEvent;
import javapoker.client.connection.events.TableJoinEvent;
import javapoker.client.game.OpenTable;
import javapoker.client.game.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class MainMenuController extends ConnectionEventListener {
	
	@FXML
	public ListView listview;
	public Button button_refresh;
	public Button button_create;
	public Button button_join;
	public TextField txt_playercount;
	public Button button_createTable;
	public Pane pane_createTable;
	
	private SocketConnection socketConnection;
	private Player player;
	private ArrayList<OpenTable> openTables = new ArrayList<>();
	public static final ObservableList data = FXCollections.observableArrayList();
	
	public MainMenuController(SocketConnection socketConnection)
	{
		this.socketConnection = socketConnection;
	}
	
	@FXML
	public void initialize()
	{
		button_join.setDisable(true);
	}
	
	@Override
	public void OnOpenTables(OpenTablesEvent event)
	{
		System.out.println("Open tables");
		openTables = event.openTables;
		
		if (listview == null) {
			listview = new ListView(data);
		}
		
		if (openTables != null) {
			refreshListView();
		}
	}
	
	public void OnActionButtonJoin(ActionEvent actionEvent)
	{
		String item = (String) listview.getSelectionModel().getSelectedItem();
		if (item != null) {
			String tableId = item.split(" ")[0];
			
			joinTable(tableId);
		}
	}
	
	public void OnActionButtonRefresh(ActionEvent actionEvent)
	{
		socketConnection.SendMessage(new JSONObject().put("op", 1)
						     .put("type", "OPEN_TABLES_REFRESH")
						     .put("data", new JSONObject()));
	}
	
	private void refreshListView()
	{
		Platform.runLater(() -> {
			data.clear();
			openTables.forEach(table -> data.add(table.toString()));
			listview.setItems(data);
		});
		
	}
	
	public void OnMouseClickedListView(MouseEvent mouseEvent)
	{
		String item = (String) listview.getSelectionModel().getSelectedItem();
		
		if (item == null) {
			button_join.setDisable(true);
			return;
		} else {
			button_join.setDisable(false);
		}
		
		if (mouseEvent.getClickCount() == 2) {
			joinTable(item.split(" ")[0]);
		}
	}
	
	private void joinTable(String tableId)
	{
		socketConnection.SendMessage(new JSONObject().put("op", 1)
						     .put("type", "TABLE_JOIN_REQUEST")
						     .put("data", new JSONObject().put("tableId", tableId)));
	}
	
	public void OnActionButtonCreate(ActionEvent actionEvent)
	{
		pane_createTable.setDisable(!pane_createTable.isDisable());
		pane_createTable.setOpacity(1 - pane_createTable.getOpacity());
		button_createTable.setDisable(true);
	}
	
	public void OnActionButtonCreateTable(ActionEvent actionEvent)
	{
		int neededPlayers = Integer.parseInt(txt_playercount.getText());
		
		socketConnection.SendMessage(new JSONObject().put("op", 1)
							  .put("type", "CREATE_TABLE_REQUEST")
							  .put("data", new JSONObject().put("neededPlayers", neededPlayers)));
	}
	
	
	public void OnTextPlayerCountInput(KeyEvent keyEvent)
	{
		try {
			String input = txt_playercount.getText();
			if (input.matches("[0-9]*")) {
				int amount = Integer.parseInt(input);
				if (amount > 1 && amount < 13) {
					txt_playercount.setStyle("-fx-focus-color: green");
					button_createTable.setDisable(false);
				} else {
					txt_playercount.setStyle("-fx-focus-color: red");
					button_createTable.setDisable(true);
				}
			} else {
				txt_playercount.setStyle("-fx-focus-color: red");
				button_createTable.setDisable(true);
			}
		} catch (Exception ex) {
			txt_playercount.setStyle("-fx-focus-color: red");
			button_createTable.setDisable(true);
		}
	}
	
	@Override
	public void OnTableJoinEvent(TableJoinEvent event)
	{
		System.out.println("joined table!");
		//todo create new scene
		//pass all the needed data
		
		Platform.runLater(() -> {
			GameController gameController = new GameController(socketConnection, event.table, player);
			socketConnection.GetConnectionEventManager().AddListener(gameController);
			socketConnection.GetConnectionEventManager().RemoveListener(this); //unsuscribe ourselfs
			
			FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/Game.fxml"));
			loader.setController(gameController);
			try {
				Parent sceneMain = loader.load();
				Scene scene = new Scene(sceneMain);
				
				Stage primaryStage = (Stage) pane_createTable.getScene().getWindow();
				primaryStage.setTitle("Game");
				primaryStage.setScene(scene);
				primaryStage.show();
				primaryStage.setResizable(false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
	}
}
