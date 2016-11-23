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

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ConnectionServer extends Server {
	
	private HashMap<Integer, Client> connectedClients;
	private int id;
	public ConnectionServer(int port)
	{
		super(port);
		connectedClients = new HashMap<>();
		id = 0;
	}
	
	@Override
	public synchronized void start()
	{
		boolean successfulStart = init();
		
		if (successfulStart) {
			System.out.println("The connection server has started and listens on port: " + GetPort());
			run();
		} else {
			System.out.println("The connection server could not start!");
		}
		
	}
	
	@Override
	public void run()
	{
		try {
			while (true) {
				Socket newClientSocket = GetSocketListener().accept();
				System.out.println("New Client has connected!");
				Client client = new Client(newClientSocket, id);
				connectedClients.put(id, client);
				client.start();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			try {
				GetSocketListener().close();
			} catch (IOException ex){
				ex.printStackTrace();
			} finally {
				System.out.println("The connection server could not be shut down!");
			}
		}
	}
}
