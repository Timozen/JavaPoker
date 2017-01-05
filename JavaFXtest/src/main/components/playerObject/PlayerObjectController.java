package main.components.playerObject;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class PlayerObjectController implements Initializable {
	
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
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		File file = new File(getClass().getResource("../../cards/cardBack_blue3.png").getPath());
		Image image = new Image(file.toURI().toString());
		card1.setImage(image);
		card2.setImage(image);
	}
	
	public void SetSmallBlind()
	{
		dealer.setDisable(true);
		smallblind.setDisable(false);
		bigblind.setDisable(true);
	}
	
	public void SetBigBlind()
	{
		dealer.setDisable(true);
		smallblind.setDisable(true);
		bigblind.setDisable(false);
	}
	
	public void SetDealer()
	{
		dealer.setDisable(false);
		smallblind.setDisable(true);
		bigblind.setDisable(true);
	}
	
	private final double IMAGE_WIDTH = 40;
	private final double IMAGE_HEIGHT = 50;
	
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
}