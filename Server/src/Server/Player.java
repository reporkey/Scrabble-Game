package Server;

import java.net.InetAddress;
import java.net.Socket;

public class Player {
    private Socket socket;
    private String name;
    private int score = 0;
    private int vote = 0;
    private int ready = 0;
    private InetAddress ip;
    private int port;

    public Player(Socket socket) {
        this.socket = socket;
        this.ip = socket.getInetAddress();
        this.port = socket.getPort();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getReady() {
        return this.ready;
    }

    public void SetReady(int num) {
        this.ready = num;
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getIp() {
        return this.ip;
    }
}
