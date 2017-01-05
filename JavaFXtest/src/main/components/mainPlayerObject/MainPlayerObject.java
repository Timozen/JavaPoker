package main.components.mainPlayerObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javapoker.client.connection.SocketConnection;
import javapoker.client.game.Player;
import javapoker.client.game.Table;
import main.components.Component;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

public class MainPlayerObject extends Component {
	
	private Node view;
	private MainPlayerObjectController controller;
	private Player player;
	private SocketConnection socketConnection;
	
	public MainPlayerObject(Player player, double x, double y, SocketConnection socketConnection, Table table)
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainPlayerObject.fxml"));
		fxmlLoader.setControllerFactory(param -> controller = new MainPlayerObjectController(socketConnection, table));
		try {
			view = fxmlLoader.load();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		super.getChildren().add(view);
		
		setLayoutX(x - 150);
		setLayoutY(y - 100);
		
		
		this.socketConnection = socketConnection;
		setupPlayer(player);
	}
	
	private void setupPlayer(Player player)
	{
		this.player = player;
		this.player.playerObject = this;
		SetNameLabel(player.nickname);
		SetMoneyLabel("" + player.money);
		SetBetAmountLabel("" + player.roundBetAll);
		SetStatusLabel("Pending");
		SetTurn(false);
		SetButtons(false);
	}
	
	@Override
	public void SetNameLabel(String name)
	{
		controller.name.setText(name);
	}
	
	@Override
	public void SetMoneyLabel(String money)
	{
		controller.money.setText(money);
	}
	
	@Override
	public void SetBetAmountLabel(String betAmount)
	{
		controller.betamount.setText(betAmount);
	}
	
	@Override
	public void SetStatusLabel(String state)
	{
		controller.state.setText(state);
	}
	
	@Override
	public void SetSmallBlind()
	{
		controller.SetSmallBlind();
	}
	
	@Override
	public void SetBigBlind()
	{
		controller.SetBigBlind();
	}
	
	@Override
	public void SetDealer()
	{
		controller.SetDealer();
	}
	
	@Override
	public void TurnChange(boolean isTurn)
	{
		if (!isTurn) {
			controller.turn.setFill(Color.RED);
			controller.pane_bet.setDisable(true);
		} else {
			controller.turn.setFill(Color.SPRINGGREEN);
			controller.pane_bet.setDisable(false);
		}
	}
	
	//todo find good var name
	public void SetButtons(boolean isStuff)
	{
		if (isStuff) {
			controller.button_increase.setText("Raise");
			controller.button_even.setText("Check");
		} else {
			controller.button_increase.setText("Bet");
			controller.button_even.setText("Call");
		}
		
		controller.isStuff = isStuff;
	}
	
	@Override
	public void SetCard1(String card)
	{
		try {
			File file = new File(URLDecoder.decode(getClass().getResource("../../cards/" + card + ".png").getPath(), "UTF-8"));
			javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
			controller.card1.setImage(image);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void SetCard2(String card)
	{
		try {
			File file = new File(URLDecoder.decode(getClass().getResource("../../cards/" + card + ".png").getPath(), "UTF-8"));
			javafx.scene.image.Image image = new javafx.scene.image.Image(file.toURI().toString());
			controller.card2.setImage(image);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
