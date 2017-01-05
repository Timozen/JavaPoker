package main.components.tableObject;

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

public class TableObjectController implements Initializable {
	
	public ImageView card1;
	public ImageView card2;
	public ImageView card3;
	public ImageView card4;
	public ImageView card5;
	public Label label_pot;
	
	private final double IMAGE_WIDTH = 40;
	private final double IMAGE_HEIGHT = 50;
	public Label label_roundname;
	public Label label_minimumbet;
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		File file = new File(getClass().getResource("../../cards/cardBack_green1.png").getPath());
		Image image = new Image(file.toURI().toString());
		card1.setImage(image);
		card2.setImage(image);
		card3.setImage(image);
		card4.setImage(image);
		card5.setImage(image);
		
		label_pot.setText("0");
	}
	
	public void OnMouseEntered1(MouseEvent mouseEvent)
	{
		mouseOverStart(card1);
	}
	
	public void OnMouseEntered2(MouseEvent mouseEvent)
	{
		mouseOverStart(card2);
	}
	
	public void OnMouseEntered3(MouseEvent mouseEvent)
	{
		mouseOverStart(card3);
	}
	
	public void OnMouseEntered4(MouseEvent mouseEvent)
	{
		mouseOverStart(card4);
	}
	
	public void OnMouseEntered5(MouseEvent mouseEvent)
	{
		mouseOverStart(card5);
	}
	
	public void OnMouseExited1(MouseEvent mouseEvent)
	{
		mouseOverEnd(card1);
	}
	
	public void OnMouseExited2(MouseEvent mouseEvent)
	{
		mouseOverEnd(card2);
	}
	
	public void OnMouseExited3(MouseEvent mouseEvent)
	{
		mouseOverEnd(card3);
	}
	
	public void OnMouseExited4(MouseEvent mouseEvent)
	{
		mouseOverEnd(card4);
	}
	
	public void OnMouseExited5(MouseEvent mouseEvent)
	{
		mouseOverEnd(card5);
	}
	
	private void mouseOverStart(ImageView card)
	{
		card.setFitWidth(IMAGE_WIDTH * 4);
		card.setFitHeight(IMAGE_HEIGHT * 4);
		
		card.setLayoutX(card.getLayoutX() + IMAGE_WIDTH / 2 - IMAGE_WIDTH);
		card.setLayoutY(card.getLayoutY() + IMAGE_HEIGHT / 2 - IMAGE_HEIGHT);
	}
	
	private void mouseOverEnd(ImageView card)
	{
		card.setFitWidth(IMAGE_WIDTH);
		card.setFitHeight(IMAGE_HEIGHT);
		
		card.setLayoutX(card.getLayoutX() - IMAGE_WIDTH / 2 + IMAGE_WIDTH);
		card.setLayoutY(card.getLayoutY() - IMAGE_HEIGHT / 2 + IMAGE_HEIGHT);
	}
}