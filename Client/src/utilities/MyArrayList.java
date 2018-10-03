package utilities;

import org.json.JSONArray;

import java.util.ArrayList;

public class MyArrayList<Players> extends ArrayList<Players> {
    public JSONArray toJson(){
        JSONArray players = new JSONArray();
        for (int i=0; i < size(); i++) {
            players.put(((Player)get(i)).getObj());
        }
        return players;
    }

}
