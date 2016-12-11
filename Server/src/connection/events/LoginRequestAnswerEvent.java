package connection.events;

import connection.client.Client;
import org.json.JSONObject;

/**
 * Created by Tim on 29.11.2016.
 */
public class LoginRequestAnswerEvent extends ConnectionEvent {
	
	public String username;
	public String password;
	public String id;
	
	public LoginRequestAnswerEvent(Client client, JSONObject object)
	{
		super(client, object);
	}
	
	@Override
	public void Build()
	{
		username = object.getString("username");
		password = object.getString("password");
		id = object.getString("username");
	}
}
