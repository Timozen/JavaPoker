package main.components.playerObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javapoker.client.game.Player;
import main.components.Component;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

public class PlayerObject extends Component {
	
	private Node view;
	private PlayerObjectController controller;
	private Player player;
	
	public PlayerObject(Player player, double x, double y)
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PlayerObject.fxml"));
		fxmlLoader.setControllerFactory(param -> controller = new PlayerObjectController());
		try {
			view = fxmlLoader.load();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		getChildren().add(view);
		
		setLayoutX(x - 75);
		setLayoutY(y - 75);
		
		setupPlayer(player);
	}
	
	public void setupPlayer(Player player)
	{
		this.player = player;
		
		if (player == null) {
			SetNameLabel("Not connected");
			SetMoneyLabel("");
			SetBetAmountLabel("");
			SetStatusLabel("");
			SetTurn(false);
			setDisable(true);
			return;
		}
		
		player.playerObject = this;
		
		SetNameLabel(player.nickname);
		SetMoneyLabel("" + player.money);
		SetBetAmountLabel("" + player.roundBetAll);
		SetStatusLabel("Pending");
		SetTurn(false);
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
		} else {
			controller.turn.setFill(Color.SPRINGGREEN);
		}
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
