package Server;

import org.json.JSONObject;
import java.net.InetAddress;
import java.net.Socket;

public class Player {

    private Socket socket;
    private String ip;
    private int port;
    private String name;
    private boolean ready = false;
    private int vote = -1;
    private int score = 0;
    private JSONObject obj;

    public Player(Socket socket, JSONObject msg) {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.name = msg.getString("value");

        obj = new JSONObject();
        obj.put("ip", ip);
        obj.put("port", port);
        obj.put("name", name);
        obj.put("ready", ready);
        obj.put("score", score);
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

    public boolean getReady() {
        return ready;
    }

    public void setReady() {
        this.ready = true;
        obj.put("ready", ready);
    }

    public int getVote() {
        return this.vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
        obj.put("score", score);
    }

    public JSONObject getObj() {
        return obj;
    }
}
