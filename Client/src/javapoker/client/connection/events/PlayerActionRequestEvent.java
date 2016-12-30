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

package javapoker.client.connection.events;

import javapoker.client.connection.SocketConnection;
import javapoker.client.game.BettingOperations;
import org.json.JSONObject;

/**
 * Created by Tim on 04.12.2016.
 */
public class PlayerActionRequestEvent extends ConnectionEvent{
	public int maximumBetAmount;
	public int operations;
	//public BettingOperations[] operations;

	private final BettingOperations[] betOptionsPreBet = {BettingOperations.FOLD, BettingOperations.BET, BettingOperations.CHECK};
	private final BettingOperations[] betOptionsPostBet = {BettingOperations.FOLD, BettingOperations.RAISE, BettingOperations.CALL};
	public PlayerActionRequestEvent(SocketConnection socketConnection, JSONObject data)
	{
		super(socketConnection, data);
	}
	
	@Override
	public void Build()
	{
		//if (GetData().getInt("actions") == 0) {
		//	operations = betOptionsPreBet;
		//	//also needs max input
		//} else {
		//	operations = betOptionsPostBet;
		//}
		operations = GetData().getInt("actions");
		maximumBetAmount = GetData().getInt("maximumPlayerBet");
	}
}
