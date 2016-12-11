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


import connection.events.*;

public abstract class ConnectionEventListener {
	
	public void OnClientConnectEvent(ClientConnectEvent event){}
	public void OnClientDisconnectEvent(ClientDisconnectEvent event){}
	public void OnLoginRequestAnswerEvent(LoginRequestAnswerEvent event) {}
	public void OnPlayerActionAnswerEvent(PlayerActionAnswerEvent event){}
	public void OnTableJoinRequestEvent(TableJoinRequestEvent event) {}
	public void OnOpenTablesRefreshEvent(OpenTablesRefreshEvent event) {}
	
	public void OnConnectionEvent(ConnectionEvent e)
	{
		if(e instanceof ClientConnectEvent){
			OnClientConnectEvent((ClientConnectEvent) e);
		} else if (e instanceof ClientDisconnectEvent){
			OnClientDisconnectEvent((ClientDisconnectEvent) e);
		} else if (e instanceof  LoginRequestAnswerEvent){
			OnLoginRequestAnswerEvent((LoginRequestAnswerEvent) e);
		} else if (e instanceof PlayerActionAnswerEvent) {
			OnPlayerActionAnswerEvent((PlayerActionAnswerEvent) e);
		} else if (e instanceof TableJoinRequestEvent) {
			OnTableJoinRequestEvent((TableJoinRequestEvent) e);
		} else if (e instanceof OpenTablesRefreshEvent) {
			OnOpenTablesRefreshEvent((OpenTablesRefreshEvent) e);
		}
	}
}
