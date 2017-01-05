package javapoker.client.connection.events;

import javapoker.client.connection.SocketConnection;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Derpie on 12.12.2016.
 */
public class RoundUpdateShowdownPostPaymentEvent extends ConnectionEvent{
    public ArrayList<String[]> winnerData;

    public RoundUpdateShowdownPostPaymentEvent(SocketConnection socketConnection, JSONObject data)
    {
        super(socketConnection, data);
    }

    @Override
    public void Build()
    {
        winnerData = new ArrayList<>();
        JSONArray information = GetData().getJSONArray("information");

        for(int i = 0; i < information.length(); i++) {
            winnerData.add(new String[]{
                    information.getJSONObject(i).getString("playerId"),
                    information.getJSONObject(i).getString("winAmount"),
                    information.getJSONObject(i).getString("money")
            });
        }
    }
}