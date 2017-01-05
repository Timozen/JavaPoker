package main.components.tableObject;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;

public class TableObject extends Pane {
	
	private Node view;
	private TableObjectController controller;
	
	public static final double AXIS_X = 300;
	public static final double AXIS_Y = 150;
	public ArrayList<String> cards;
	
	public TableObject(double x, double y)
	{
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("TableObject.fxml"));
		fxmlLoader.setControllerFactory(param -> controller = new TableObjectController());
		try {
			view = fxmlLoader.load();
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		getChildren().add(view);
		
		setLayoutX(x - AXIS_X);
		setLayoutY(y - AXIS_Y);
		
		cards = new ArrayList<>();
	}
	
	public void UpdatePotValue(String value)
	{
		controller.label_pot.setText(value);
	}
	
	public void UpdatePotValue(int value)
	{
		UpdatePotValue("" + value);
	}
	
	public void SetRoundName(String value)
	{
		controller.label_roundname.setText(value);
	}
	
	public void UpdateMinimumBet(String value)
	{
		controller.label_minimumbet.setText(value);
	}
	
	public void UpdateMinimumBet(int value)
	{
		UpdateMinimumBet(""+value);
	}
	
	public void AddCardToBoard(String card)
	{
		try {
			File file = new File(URLDecoder.decode(getClass().getResource("../../cards/" + card + ".png").getPath(), "UTF-8"));
			Image image = new Image(file.toURI().toString());
			
			System.out.println(file.toURI().toString());
			
			cards.add(card);
			switch (cards.size()) {
				case 1:
					controller.card1.setImage(image);
					break;
				case 2:
					controller.card2.setImage(image);
					break;
				case 3:
					controller.card3.setImage(image);
					break;
				case 4:
					controller.card4.setImage(image);
					break;
				case 5:
					controller.card5.setImage(image);
					break;
				default:
					System.err.println("That too many cards added to the board");
			}
			
			controller.card1.setImage(image);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
}
