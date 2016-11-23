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

package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
	
	private BufferedReader input;
	private PrintWriter output;
	private Socket socket;
	
	public int id;
	
	public Client(Socket socket, int id)
	{
		this.socket = socket;
		this.id = id;
	}
	
	private boolean init()
	{
		try {
			input  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintWriter(socket.getOutputStream(), true);
			
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	@Override
	public synchronized void start()
	{
		if (init()) {
			
			//TODO log-in
			
			run();
		} else {
			System.out.println("Client could not be initiated!");
		}
		
	}
	
	@Override
	public void run()
	{
		try {
			output.println("Welcome!");
			while(true){
				
			}
		} catch (Exception ex){ //todo find out the right exception
			ex.printStackTrace();
		} finally {
			try {
				socket.close();
				//TODO Fire disconnect event
			} catch (IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
