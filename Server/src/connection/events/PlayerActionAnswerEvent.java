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

package connection.events;

import connection.client.Client;
import game.models.BettingOperations;
import org.json.JSONObject;

public class PlayerActionAnswerEvent extends ConnectionEvent{
	
	public int tableId;
	public BettingOperations action;
	public int betAmount;
	public boolean isAllIn;
	
	public PlayerActionAnswerEvent(Client client, JSONObject object)
	{
		super(client, object);
	}
	
	@Override
	public void Build()
	{
		tableId = object.getInt("tableId");
		action = BettingOperations.valueOf(object.getString("action"));
		betAmount = object.getInt("betAmount");
		isAllIn = object.getBoolean("isAllIn");
	}
}
