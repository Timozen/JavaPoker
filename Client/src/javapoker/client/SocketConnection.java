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

package javapoker.client;

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
	
	public SocketConnection(String adr, int port)
	{
		this.adr = adr;
		this.port = port;
	}
	
	@Override
	public synchronized void start()
	{
		try {
			socket = new Socket(adr, port);
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			
			run();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		try {
			while (true) {
				String msg = input.readLine();
				if (msg == null) {
					break;
				}
				
				System.out.println(msg);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public void sendMessage(String msg)
	{
		output.println(msg);
	}
}
