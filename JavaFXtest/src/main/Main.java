package main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javapoker.client.connection.ConnectionEventManager;
import javapoker.client.connection.SocketConnection;
import main.controllers.GameController;
import main.controllers.LoginController;

public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		///*
		ConnectionEventManager connectionEventManager = new ConnectionEventManager();
		LoginController controller = new LoginController();
		connectionEventManager.AddListener(controller);
		
		SocketConnection socketConnection = new SocketConnection("localhost", 46337, connectionEventManager);
		socketConnection.setDaemon(true);
		socketConnection.start();
		
		controller.SetSocketConnection(socketConnection);
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/Login.fxml"));
		loader.setController(controller);
		
		//Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
		
		Parent sceneMain = loader.load();

		Scene scene = new Scene(sceneMain);
		
		primaryStage.setTitle("Anmelden");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		//primaryStage.setAlwaysOnTop(true);
		//*/
		/*
		GameController gameController = new GameController(null, null, null);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("scenes/Game.fxml"));
		loader.setController(gameController);
		
		Parent sceneMain = loader.load();
		Scene scene = new Scene(sceneMain);
		primaryStage.setTitle("Spiel");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setResizable(false);
		*/
		
	}
	
	public static void main(String[] args)
	{
		launch(args);
	}
}
