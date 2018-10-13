package server;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

public class Player {

    private Socket socket;
    private String ip;
    private int port;
    private String name;
    private long id;
    private String path;
    private int accept = -1;
    private int vote = -1;
    private int score = 0;
    private JSONObject obj;

    public Player(Socket socket, JSONObject msg) throws JSONException {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.name = msg.getString("name");
       
        
        // initial a random profile icon and id
        Random rand = new Random();
		int randomNum = rand.nextInt(10) + 1;
		this.path = "/img/h" + randomNum + ".png";
		this.id = System.currentTimeMillis();
		
        obj = new JSONObject();
        obj.put("name", name);
        obj.put("id", id);
        obj.put("path", path);
        obj.put("accept", accept);
        obj.put("score", score);
        obj.put("vote", vote);
    }

    public Socket getSocket() {
        return socket;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public int getAccept() {
        return accept;
    }

    public void setAccept(int value){
        this.accept = value;
        try {
			obj.put("accept", accept);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public int getVote() {
        return this.vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
        try {
			obj.put("vote", vote);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
        try {
			obj.put("score", score);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public JSONObject getObj() {
        return obj;
    }

	public String getName() {
		return name;
	}
	
	public String getPath() {
		return this.path;
	}
	
	public long getId() {
		return id;
	}
}
