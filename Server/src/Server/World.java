package Server;

import org.json.JSONObject;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.*;

public class World {

    private static final int port = 8080;
    public volatile MyArrayList<Player> players = new MyArrayList<Player>();
    private char[][] map = new char[20][20];
    public volatile String word = null;
    private int pass = 0;

    public World() {

        ServerSocketFactory factory = ServerSocketFactory.getDefault();

        // Game begin
        Thread game = new Thread(new Game());
        game.start();

        try(ServerSocket server = factory.createServerSocket(port)){
            System.out.println("Waiting for client connection..");

            // Wait for connections.
            while(true){
                Socket client = server.accept();
                System.out.println("Client: Applying for connection!");

                // Start a new thread to read msg
                Thread myListener = new Thread(() -> listener(client));
                myListener.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listener(Socket client) {

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
            String msgStr = null;

            while ((msgStr = in.readLine()) != null) {
                // if request connection

                JSONObject msg = new JSONObject(msgStr);

                if (msg.getString("method").equals("establish")) {
                    Player player = new Player(client, msg);
                    players.add(player);
                }
                if (msg.getString("method").equals("ready")) {
                    for (Player player : players) {
                        if (client.getInetAddress().equals(player.getIp()) && client.getPort() == player.getPort()) {
                            player.setReady();
                        }
                    }
                }
                if (msg.getString("method").equals("word")) {
                    // if players pass, get empty word
                    if (msg.getString("value").equals("") || msg.getString("value").equals(null)) {
                        pass++;
                        if (pass >= players.size()) {
                            exit();
                        }
                    } else {
                        pass = 0;
                        word = msg.getString("value");
                    }
                }
                if (msg.getString("method").equals("vote")) {
                    for (Player player : players) {
                        player.setVote(msg.getInt("value"));
                    }
                }
                if (msg.getString("method").equals("end")) {
                    exit();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public class Game implements Runnable {

        public void run() {

            // Check ready
            while(true) {
                int ready = 0;
                for (Player player : players) {
                    if (player.getReady()) {
                        ready++;
                    }
                }
                if (ready >= players.size()) {
                    break;
                }
            }

            // game begin
            while (true) {
                // iterate player's turn
                for (Player player : players) {

                    // send whose turn
                    JSONObject msg = new JSONObject();
                    msg.put("method", "begin");
                    msg.put("map", map);
                    msg.put("player",player);
                    broadcast(msg);

                    // wait until receive a word
                    while (true) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!word.equals(null)) {
                            break;
                        }
                    }

                    // the player does not pass
                    if (!word.equals("")){
                        msg = new JSONObject();
                        msg.put("method", "vote");
                        msg.put("value", word);
                        msg.put("map", map);
                        msg.put("player",player);
                        broadcast(msg);
                    }

                    // wait until receive all vote
                    int vote;
                    while (true) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        vote = 0;
                        for (Player temp : players){
                            if (temp.getVote() == 1) {
                                vote++;
                            }else if (temp.getVote() == -1){
                                vote = -1;
                                break;
                            }
                        }
                        if (vote == -1 && vote == players.size()) {
                            break;
                        }
                    }

                    // if all player vote yes
                    if (vote == players.size()){
                        player.setScore(player.getScore() + word.length());
                    }

                    // if map full
                    for (char[] row : map){
                        for(char point : row){
                            if (point == '\u0000'){
                                exit();
                            }
                        }
                    }

                }
            }
        }
    }

// methods

    public void broadcast(JSONObject msg){

        for (Player player : players){
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream(), "UTF-8"));
                out.write(msg.toString());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit(){

        JSONObject msg = new JSONObject();
        msg.put("method", "end");
        msg.put("value", players);
        msg.put("map", map);

        broadcast(msg);

        System.out.println("Game Over. Server Closed.");
        System.exit(0);
    }

}
