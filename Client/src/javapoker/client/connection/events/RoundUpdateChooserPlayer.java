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
import org.json.JSONObject;

/**
 * Created by Derpie on 11.12.2016.
 */
public class RoundUpdateChooserPlayer extends ConnectionEvent{
    public String playerId;
    public int pot;
    public RoundUpdateChooserPlayer(SocketConnection socketConnection, JSONObject data)
    {
        super(socketConnection, data);
    }

    @Override
    public void Build()
    {
        playerId = GetData().getString("playerId");
        pot = GetData().getInt("pot");
    }
}
