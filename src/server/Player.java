package server;

import org.json.JSONException;
import org.json.JSONObject;
import java.net.InetAddress;
import java.net.Socket;

public class Player {

    private Socket socket;
    private String ip;
    private int port;
    private String name;
    private int ready = 0;
    private int vote = -1;
    private int score = 0;
    private JSONObject obj;

    public Player(Socket socket, JSONObject msg) throws JSONException {
        this.socket = socket;
        this.ip = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.name = msg.getString("name");

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

    public int getReady() {
        return ready;
    }

    public void setReady() throws JSONException {
        this.ready = 1;
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

    public void setScore(int score) throws JSONException {
        this.score = score;
        obj.put("score", score);
    }

    public JSONObject getObj() {
        return obj;
    }
}