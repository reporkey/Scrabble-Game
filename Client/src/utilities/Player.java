package utilities;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Player {

    private String name;
    private String path;
    private int accept = -1;
    private int vote = -1;
    private int score = 0;
    private JSONObject obj;

    public Player(JSONObject obj) {
		this.obj = obj;
		try {
			name = obj.getString("name");
			path = obj.getString("path");
			accept = obj.getInt("accept");
			vote = obj.getInt("vote");
			score = obj.getInt("score");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public int getAccept() throws JSONException {
        return accept;
    }

    public void setAccept() throws JSONException {
        this.accept = 1;
        obj.put("accept", accept);
    }

    public int getScore() {
        return this.score;
    }

    public JSONObject getObj() {
        return obj;
    }


	public String getName() {
		return name;
	}
	
	public String getVoteString() {
		switch (vote) {
		case -1: return "Waiting";
		case 0: return "No";
		case 1: return "Yes";
		default: return "ERROR";
		}
	}
}
