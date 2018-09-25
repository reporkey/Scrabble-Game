package players;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetAddress;
import java.net.Socket;

public class PlayerC {
    private Socket socket;
    private String name;
    private String img;
    private int score = 0;
    private int vote = 0;
    private boolean ready = false;
    private InetAddress ip;
    private int port;

    public PlayerC(Socket socket, JSONObject msg) throws JSONException {
        this.socket = socket;
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
    }


    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getVote() {
        return this.vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public boolean getReady() {
        return ready;
    }

    public void setReady() {
        this.ready = true;
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getIp() {
        return this.ip;
    }

    public Socket getSocket() {
        return socket;
    }


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getImg() {
		return img;
	}


	public void setImg(String img) {
		this.img = img;
	}
}
