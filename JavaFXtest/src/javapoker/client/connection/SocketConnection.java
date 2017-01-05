/*
 * JavaPoker - Online Poker Game Copyright (C) 2016 Tim Büchner, Matthias Döpmann
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */

package javapoker.client.connection;

import javafx.application.Platform;
import javapoker.client.connection.utils.ConnectionEventBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnection extends Thread {
	
	private String adr;
	private int port;
	private Socket socket;
	
	private BufferedReader input;
	private PrintWriter output;
	
	private ConnectionEventManager connectionEventManager;
	private ConnectionEventBuilder connectionEventBuilder;
	
	public SocketConnection(String adr, int port, ConnectionEventManager connectionEventManager)
	{
		this.adr = adr;
		this.port = port;
		this.connectionEventManager = connectionEventManager;
		this.connectionEventBuilder = new ConnectionEventBuilder(this.connectionEventManager, this);
	}
	
	@Override
	public void run()
	{
		boolean connected = false;
		try {
			while(!connected) {
				try {
					socket = new Socket(adr, port);
					connected = true;
				} catch (java.net.ConnectException ex) {
					System.out.println("Server is not available - Automatic retry in 5 seconds");
					connected = false;
					try {
						sleep(5000);
					} catch (InterruptedException ex2) {
						ex2.printStackTrace();
					}
				}
			}
			
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}
		
		try {
			while (true) {
				String msg = input.readLine();
				if (msg == null) {
					break;
				}
				Platform.runLater(() -> connectionEventBuilder.CreateEvent(msg));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void SendMessage(String msg)
	{
		output.println(msg);
	}
	
	public void SendMessage(JSONObject obj)
	{
		SendMessage(obj.toString());
	}
	
	public ConnectionEventManager GetConnectionEventManager()
	{
		return connectionEventManager;
	}
}
