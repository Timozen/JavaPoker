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

import javapoker.client.connection.events.*;

public abstract class ConnectionEventListener {
	
	public void OnLoginRequest(LoginRequestEvent event){}
	public void OnLoginResult(LoginResultEvent event) {}
	public void OnOpenTables(OpenTablesEvent event) {}
	public void OnTableJoinEvent(TableJoinEvent event) {}
	public void OnTableLeaveEvent(TableLeaveEvent event) {}
	public void OnPlayerJoinsTableEvent(PlayerJoinsTableEvent event) {}
	public void OnPlayerLeavesTableEvent(PlayerLeavesTableEvent event) {}
	public void OnPlayerActionRequestEvent(PlayerActionRequestEvent event) {}
	public void OnRoundUpdateStartEvent(RoundUpdateStartEvent event) {}
	public void OnRoundUpdateCardDrawEvent(RoundUpdateCardDrawEvent event) {}
	public void OnRoundUpdateRoundEvent(RoundUpdateRoundEvent event) {}
	public void OnRoundUpdateShowdownEvent(RoundUpdateShowdownEvent event) {}
	public void OnRoundUpdatePlayerEvent(RoundUpdatePlayerEvent event) {}
	public void OnRoundUpdateChooserPlayer(RoundUpdateChooserPlayer event) {}
	
	public void OnConnectionEvent(ConnectionEvent e)
	{
		if (e instanceof LoginRequestEvent) {
			OnLoginRequest((LoginRequestEvent)e);
		} else if (e instanceof LoginResultEvent) {
			OnLoginResult((LoginResultEvent) e);
		} else if (e instanceof OpenTablesEvent) {
			OnOpenTables((OpenTablesEvent) e);
		} else if (e instanceof TableJoinEvent) {
			OnTableJoinEvent((TableJoinEvent) e);
		} else if (e instanceof TableLeaveEvent) {
			OnTableLeaveEvent((TableLeaveEvent) e);
		} else if (e instanceof PlayerJoinsTableEvent) {
			OnPlayerJoinsTableEvent((PlayerJoinsTableEvent) e);
		} else if (e instanceof PlayerLeavesTableEvent) {
			OnPlayerLeavesTableEvent((PlayerLeavesTableEvent) e);
		} else if (e instanceof PlayerActionRequestEvent) {
			OnPlayerActionRequestEvent((PlayerActionRequestEvent) e);
		} else if (e instanceof RoundUpdateStartEvent) {
			OnRoundUpdateStartEvent((RoundUpdateStartEvent) e);
		} else if (e instanceof RoundUpdateCardDrawEvent) {
			OnRoundUpdateCardDrawEvent((RoundUpdateCardDrawEvent) e);
		} else if (e instanceof RoundUpdateRoundEvent) {
			OnRoundUpdateRoundEvent((RoundUpdateRoundEvent) e);
		} else if (e instanceof RoundUpdateShowdownEvent) {
			OnRoundUpdateShowdownEvent((RoundUpdateShowdownEvent) e);
		} else if (e instanceof RoundUpdatePlayerEvent) {
			OnRoundUpdatePlayerEvent((RoundUpdatePlayerEvent) e);
		} else if (e instanceof RoundUpdateChooserPlayer) {
			OnRoundUpdateChooserPlayer((RoundUpdateChooserPlayer) e);
		}
	}
}
