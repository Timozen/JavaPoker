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

package connection.server;

import connection.ConnectionEventListener;

import java.io.IOException;
import java.net.ServerSocket;

public abstract class Server extends ConnectionEventListener implements Runnable {
	
	private ServerSocket socketListener;
	private int port;
	
	Server(int port)
	{
		this.port = port;
	}
	
	boolean init()
	{
		try {
			//add address
			this.socketListener = new ServerSocket(port);
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return false;
	}
	
	public int GetPort()
	{
		return port;
	}
	
	public ServerSocket GetSocketListener()
	{
		return socketListener;
	}
}
