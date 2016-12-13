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
import org.json.JSONObject;

/**
 * Created by Tim on 13.12.2016.
 */
public class RegisterRequestEvent extends ConnectionEvent {
	
	public String username;
	public String password;
	
	public RegisterRequestEvent(Client client, JSONObject data)
	{
		super(client, data);
	}
	
	@Override
	public void Build()
	{
		username = object.getString("username");
		password = object.getString("password");
	}
}
