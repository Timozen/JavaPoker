package main.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javapoker.client.connection.ConnectionEventListener;
import javapoker.client.connection.SocketConnection;
import javapoker.client.connection.events.LoginRequestEvent;
import javapoker.client.connection.events.LoginResultEvent;
import javapoker.client.game.Player;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginController extends ConnectionEventListener {
	
	//Login
	public Pane pane_login;
	public Button button_login;
	public TextField txt_login_username;
	public PasswordField txt_login_password;
	public Label label_toRegister;
	
	//Register
	public Pane pane_register;
	public Button button_register;
	public TextField txt_register_username;
	public PasswordField txt_register_password1;
	public PasswordField txt_register_password2;
	public Label label_backToLogin;
	
	//Status
	public Pane pane_status;
	public Label label_status;
	public ProgressBar bar_status;
	
	private boolean isValidUsername = false;
	private boolean isLongEnough = false;
	
	
	private SocketConnection socketConnection;
	
	public LoginController()
	{
		
	}
	
	@FXML
	public void initialize()
	{
		
	}
	
	@Override
	public void OnLoginRequest(LoginRequestEvent event)
	{
		System.out.println("got login request");
	}
	
	@Override
	public void OnLoginResult(LoginResultEvent event)
	{
		if (event.validLogin) {
			
			pane_login.setDisable(true);
			pane_login.setOpacity(0);
			
			pane_register.setDisable(true);
			pane_register.setOpacity(0);
			
			pane_status.setDisable(false);
			pane_status.setOpacity(1);
			
			if (event.reason.equals("")) {
				Platform.runLater(() -> label_status.setText("Anmeldung erfolgreich!"));
			} else {
				Platform.runLater(() -> label_status.setText("Registrierung erfolgreich!"));
			}
			
			double barValue = 0.0;
			while(((int)barValue) != 1) {
				barValue += 0.05;
				bar_status.setProgress(barValue);
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			
			//load main menu scene
			Platform.runLater(() -> {
				MainMenuController mainMenuController = new MainMenuController(socketConnection);
				socketConnection.GetConnectionEventManager().AddListener(mainMenuController);
				socketConnection.GetConnectionEventManager().RemoveListener(this); //unsuscribe ourselfs
				
				FXMLLoader loader = new FXMLLoader(getClass().getResource("../scenes/MainMenu.fxml"));
				loader.setController(mainMenuController);
				try {
					Parent sceneMain = loader.load();
					Scene scene = new Scene(sceneMain);
					
					Stage primaryStage = (Stage) pane_login.getScene().getWindow();
					primaryStage.setTitle("MainMenu");
					primaryStage.setScene(scene);
					primaryStage.show();
					primaryStage.setResizable(false);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			});
		} else {
			if (event.reason.equals("username")) {
				txt_login_username.setStyle("-fx-focus-color: red");
				txt_login_username.setTooltip(new Tooltip("Unbekannter Benutzername!"));
				
			} else if (event.reason.equals("password")) {
				txt_login_password.setStyle("-fx-focus-color: red");
				txt_login_password.setTooltip(new Tooltip("Falsches Passwort!"));
			}
		}
	}
	
	public SocketConnection GetSocketConnection()
	{
		return socketConnection;
	}
	
	public void SetSocketConnection(SocketConnection socketConnection)
	{
		this.socketConnection = socketConnection;
	}
	
	public void OnClickLabelToRegister(MouseEvent mouseEvent)
	{
		pane_login.setDisable(true);
		pane_login.setOpacity(0);
		
		pane_register.setDisable(false);
		pane_register.setOpacity(1);
	}
	
	public void OnClickLabelBackToLogin(MouseEvent mouseEvent)
	{
		pane_register.setDisable(true);
		pane_register.setOpacity(0);
		
		pane_login.setDisable(false);
		pane_login.setOpacity(1);
	}
	
	public void OnActionButtonRegister(ActionEvent actionEvent)
	{
		String username = txt_register_username.getText();
		String password = hashPassword(txt_register_password1.getText());
		
		socketConnection.SendMessage(new JSONObject().put("op", 1)
						     .put("type", "REGISTER_REQUEST")
						     .put("data", new JSONObject().put("username", username)
							     .put("password", password)
						     )
		);
	}
	
	public void OnActionButtonLogin(ActionEvent actionEvent)
	{
		String username = txt_login_username.getText();
		String password = hashPassword(txt_login_password.getText());
		
		socketConnection.SendMessage(new JSONObject().put("op", 1)
						     .put("type", "LOGIN_REQUEST_ANSWER")
						     .put("data", new JSONObject().put("username", username)
							     .put("password", password)
						     )
		);
	}
	
	public void OnTxtPassword1Input(KeyEvent keyEvent)
	{
		String pw1 = txt_register_password1.getText();
		
		if (pw1.length() < 8) {
			txt_register_password1.setStyle("-fx-focus-color: red");
			isLongEnough = false;
		} else {
			txt_register_password1.setStyle("-fx-focus-color: green");
			isLongEnough = true;
		}
		
		checkPasswords();
	}
	
	public void OnTxtPassword2Input(KeyEvent keyEvent)
	{
		checkPasswords();
	}
	
	private void checkPasswords()
	{
		String pw1 = txt_register_password1.getText();
		String pw2 = txt_register_password2.getText();
		
		if (!pw1.equals("") && pw1.equals(pw2) && isValidUsername && isLongEnough) {
			button_register.setDisable(false);
			txt_register_password2.setStyle("-fx-focus-color: green");
			
		} else {
			button_register.setDisable(true);
			if (pw2.length() != 0) {
				txt_register_password2.setStyle("-fx-focus-color: red");
			}
		}
	}
	
	public void OnRegisterUsernameInput(KeyEvent keyEvent)
	{
		String username = txt_register_username.getText();
		
		if (username.length() >= 4 && username.length() <= 20) {
			isValidUsername = true;
			txt_register_username.setStyle("-fx-focus-color: green");
		} else {
			txt_register_username.setStyle("-fx-focus-color: red");
		}
		checkPasswords();
	}
	
	private String hashPassword(String password)
	{
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(password.getBytes());
			byte[] digest = messageDigest.digest();
			StringBuilder stringBuffer = new StringBuilder();
			
			for (byte b : digest) {
				stringBuffer.append(String.format("%02x", b & 0xef));
			}
			
			password = stringBuffer.toString();
			
			return password;
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		}
		
		return "";
	}
	
	public void OnLoginUsernameInput(KeyEvent keyEvent)
	{
		txt_login_username.setStyle(null);
		if (txt_login_username.getText().length() != 0 && txt_login_password.getText().length() != 0) {
			button_login.setDisable(false);
		} else {
			button_login.setDisable(true);
		}
	}
	
	public void OnLoginPasswordInput(KeyEvent keyEvent)
	{
		txt_login_password.setStyle(null);
		if (txt_login_username.getText().length() != 0 && txt_login_password.getText().length() != 0) {
			button_login.setDisable(false);
		} else {
			button_login.setDisable(true);
		}
	}
}
