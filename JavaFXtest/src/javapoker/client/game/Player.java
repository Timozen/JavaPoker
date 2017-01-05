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

package javapoker.client.game;

import main.components.Component;
import main.components.playerObject.PlayerObject;
import org.json.JSONObject;

import java.util.ArrayList;

public class Player {
	public ArrayList<String> cards;
	public int money;
	public int roundBetAll;
	public String nickname;
	public String id;
	public int roundBetCurrent;
	
	public BettingOperations playerState;
	public BettingOperations bettingAction;
	
	public Component playerObject;
	
	public Player()
	{
		cards = new ArrayList<>();
	}
	
	public static Player Build(JSONObject obj)
	{
		Player player = new Player();

		player.nickname = obj.getString("nickname");
		player.id = obj.getString("nickname");
		player.money = obj.getInt("money");
		player.roundBetAll = obj.getInt("roundBetAll");
		player.roundBetCurrent = obj.getInt("roundBetCurrent");
		
		if (obj.has("playerState")) {
			player.playerState = BettingOperations.valueOf(obj.getString("playerState"));
		}
		if (obj.has("bettingAction")) {
			player.bettingAction = BettingOperations.valueOf(obj.getString("bettingAction"));
		}
		
		return player;
	}
	
}
