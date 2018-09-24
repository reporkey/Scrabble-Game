package Server;

import org.json.JSONObject;
import java.net.InetAddress;
import java.net.Socket;

public class Player {
    private Socket socket;
    private String name;
    private int score = 0;
    private int vote = 0;
    private boolean ready = false;
    private InetAddress ip;
    private int port;

    public Player(Socket socket, JSONObject msg) {
        this.socket = socket;
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
        this.name = msg.getString("value");

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
}
