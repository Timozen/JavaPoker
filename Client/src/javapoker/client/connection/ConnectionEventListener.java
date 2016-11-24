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

import javapoker.client.connection.events.ConnectionEvent;
import javapoker.client.connection.events.TestEvent;

public abstract class ConnectionEventListener {
	
	public void OnTestEvent(TestEvent e)
	{
	}
	
	public void OnConnectionEvent(ConnectionEvent e)
	{
		if (e instanceof TestEvent) {
			OnTestEvent((TestEvent)e);
		}
	}
}
