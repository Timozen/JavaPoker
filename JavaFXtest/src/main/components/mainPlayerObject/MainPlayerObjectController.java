package main.components.mainPlayerObject;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javapoker.client.connection.SocketConnection;
import javapoker.client.game.BettingOperations;
import javapoker.client.game.Table;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPlayerObjectController implements Initializable {
	
	//FXML
	public Label name;
	public StackPane dealer;
	public StackPane smallblind;
	public StackPane bigblind;
	public Label money;
	public Label betamount;
	public Label state;
	public ImageView card1;
	public ImageView card2;
	public Circle turn;
	public Button button_fold;
	public Button button_increase;
	public Button button_even;
	public Slider slider_money;
	public Label label_slider_min;
	public Label label_slider_max;
	public Label label_slider_current;
	public Pane pane_bet;
	
	//other stuff
	private SocketConnection socketConnection;
	private Table table;
	private boolean isAllin;
	
	private static final double IMAGE_WIDTH = 40;
	private static final double IMAGE_HEIGHT = 50;
	
	//todo find goog var name
	public boolean isStuff = true;
	
	public MainPlayerObjectController(SocketConnection socketConnection, Table table)
	{
		this.socketConnection = socketConnection;
		this.table = table;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		SetSliderMin(0);
		SetSliderMax(100);
		slider_money.valueProperty().addListener((observable, oldValue, newValue) -> {
			
			slider_money.setValue(newValue.intValue());
			label_slider_current.setText("" + newValue.intValue());
			
			if (newValue.equals(slider_money.getMax())) {
				button_increase.setText("All-In");
				isAllin = true;
			} else {
				isAllin = false;
			}
			
		});
		
		File file = new File(getClass().getResource("../../cards/cardBack_blue3.png").getPath());
		Image image = new Image(file.toURI().toString());
		card1.setImage(image);
		card2.setImage(image);
		
	}
	
	void SetSliderMax(double value)
	{
		slider_money.setMax(value);
		label_slider_max.setText("" + Math.round(value));
	}
	
	void SetSliderMin(double value)
	{
		slider_money.setMin(value);
		label_slider_min.setText("" + Math.round(value));
	}
	
	void SetSmallBlind()
	{
		dealer.setDisable(true);
		smallblind.setDisable(false);
		bigblind.setDisable(true);
	}
	
	void SetBigBlind()
	{
		dealer.setDisable(true);
		smallblind.setDisable(true);
		bigblind.setDisable(false);
	}
	
	void SetDealer()
	{
		dealer.setDisable(false);
		smallblind.setDisable(true);
		bigblind.setDisable(true);
	}
	
	public void MouseHoverStarted1(MouseEvent mouseEvent)
	{
		mouseOverStart(card1);
	}
	
	public void MouseHoverEnded1(MouseEvent mouseEvent)
	{
		mouseOverEnd(card1);
	}
	
	public void MouseHoverStarted2(MouseEvent mouseEvent)
	{
		mouseOverStart(card2);
	}
	
	public void MouseHoverEnded2(MouseEvent mouseEvent)
	{
		mouseOverEnd(card2);
	}
	
	private void mouseOverStart(ImageView card)
	{
		card.setFitWidth(IMAGE_WIDTH * 4);
		card.setFitHeight(IMAGE_HEIGHT * 4);
		
		card.setLayoutX(card.getLayoutX() + IMAGE_WIDTH / 2 - IMAGE_WIDTH);
		card.setLayoutY(card.getLayoutY() + IMAGE_HEIGHT / 2 - IMAGE_HEIGHT);
		
		card.toFront();
	}
	
	private void mouseOverEnd(ImageView card)
	{
		card.setFitWidth(IMAGE_WIDTH);
		card.setFitHeight(IMAGE_HEIGHT);
		
		card.setLayoutX(card.getLayoutX() - IMAGE_WIDTH / 2 + IMAGE_WIDTH);
		card.setLayoutY(card.getLayoutY() - IMAGE_HEIGHT / 2 + IMAGE_HEIGHT);
		
		card.toBack();
	}
	
	public void OnButtonFold(ActionEvent actionEvent)
	{
		JSONObject obj = new JSONObject();
		obj.put("op", 1)
			.put("type", "PLAYER_ACTION_ANSWER")
			.put("data", new JSONObject()
				.put("tableId", table.tableId)
				.put("action", BettingOperations.FOLD)
				.put("betAmount", 0)
				.put("isAllIn", false)
			);
		
		System.out.println(obj.toString());
		socketConnection.SendMessage(obj);
	}
	
	public void OnButtonIncrease(ActionEvent actionEvent)
	{
		
		JSONObject obj = new JSONObject().put("op", 1)
			.put("type", "PLAYER_ACTION_ANSWER")
			.put("data", new JSONObject()
				.put("tableId", table.tableId)
				.put("action", isStuff ? BettingOperations.RAISE : BettingOperations.BET)
				.put("betAmount", Math.round(slider_money.getValue()))
				.put("isAllIn", isAllin)
			);
		
		System.out.println(obj.toString());
		socketConnection.SendMessage(obj);
		
	}
	
	public void OnButtonEven(ActionEvent actionEvent)
	{
		JSONObject obj = new JSONObject().put("op", 1)
			.put("type", "PLAYER_ACTION_ANSWER")
			.put("data", new JSONObject()
				.put("tableId", table.tableId)
				.put("action", isStuff ? BettingOperations.CHECK : BettingOperations.CALL)
				.put("betAmount", 0)
				.put("isAllIn", false)
			);
		
		System.out.println(obj.toString());
		socketConnection.SendMessage(obj);
	}
		
}