package Server;

import org.json.JSONArray;
import org.json.JSONObject;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.*;

public class World {

    private static final int port = 8080;
    public volatile MyArrayList<Player> players = new MyArrayList<Player>();
    public volatile MyArrayList<Player> potentialPlayers = new MyArrayList<Player>();
    private volatile JSONArray map = new JSONArray();
    public volatile String word = null;
    private volatile int pass = 0;

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

                JSONObject msg = new JSONObject(msgStr);
                JSONObject send = new JSONObject();
                switch (msg.getString("method")) {
                    case "join game": {
                        Player player = new Player(client, msg);
                        potentialPlayers.add(player);
                        send.put("method", "join game");
                        send.put("players", potentialPlayers.toJson());
                        broadcast(send, potentialPlayers);
                        break;
                    }

                    case "request invite": {
                        for (int i=0; i<((JSONArray) msg.get("players")).length() ; i++) {
                            JSONObject invited = ((JSONArray) msg.get("players")).getJSONObject(i);
                            String ip = ((JSONObject) invited).getString("ip");
                            int port = ((JSONObject) invited).getInt("port");
                            for (Player player : potentialPlayers) {
                                if (ip.equals(player.getIp()) && port == player.getPort()) {
                                    players.add(player);
                                }
                            }
                        }
                        send.put("method", "request invite");
                        send.put("from", msg.get("from"));
                        broadcast(send);
                        break;
                    }

                    case "response invite": {
                        String ip = client.getInetAddress().getHostAddress();
                        int port = client.getPort();
                        // search which player send the msg
                        for (Player player : potentialPlayers) {
                            // if the player accept, put into players list
                            if (ip.equals(player.getIp()) && port==player.getPort()) {
                                if (msg.getInt("value") == 1) {
                                    players.add(player);
                                    break;
                                }
                            }
                        }
                        send.put("method", "response invite");
                        send.put("players", players.toJson());
                        broadcast(send);
                        break;
                    }

                    case "ready": {
                        String ip = client.getInetAddress().getHostAddress();
                        int port = client.getPort();
                        // search which player send the msg
                        for (Player player : players) {
                            if (ip.equals(player.getIp()) && port == player.getPort()) {
                                player.setReady();
                            }
                        }
                        send.put("method", "ready");
                        send.put("players", players.toJson());
                        broadcast(send);
                        break;
                    }

                    case "turn": {
                        word = msg.getString("word");
                        map = (JSONArray) msg.get("map");
                        break;
                    }

                    case "vote": {
                        String ip = client.getInetAddress().getHostAddress();
                        int port = client.getPort();
                        // search which player send the msg
                        for (Player player : players) {
                            if (ip.equals(player.getIp()) && port == player.getPort()) {
                                player.setVote(msg.getInt("value"));
                            }
                        }
                        send.put("method", "update vote");
                        send.put("players", players.toJson());
                        broadcast(send);
                        break;
                    }

                    case "end":
                        exit();
                        break;
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
                    if (player.getReady() == 1) {
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
                    JSONObject send = new JSONObject();
                    send.put("method", "turn");
                    send.put("map", map);
                    send.put("player",player.getObj());
                    broadcast(send);

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

                    // the player does not pass, send vote
                    if (!word.equals("")){
                        pass = 0;
                        send = new JSONObject();
                        send.put("method", "vote");
                        send.put("word", word);
                        send.put("map", map);
                        send.put("player",player.getObj());
                        broadcast(send);
                    }else{
                        pass++;
                        continue;
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
                            }else if (temp.getVote() == 0){
                                vote = -1;
                                break;
                            }
                        }
                        if (vote == -1 && vote == players.size()) {
                            break;
                        }
                    }

                    // if all players had voted or someone vote no
                    if (vote == players.size()){
                        player.setScore(player.getScore() + word.length());
                    }else if (vote == -1){
                        continue;
                    }

                    // if map full
                    for (int i=0; i<map.length() ; i++) {
                        JSONArray row = map.getJSONArray(i);
                        for(int j=0; j<map.length() ; j++) {
                            char letter = (char) row.get(i);
                            if (letter == '\u0000'){
                                exit();
                            }
                        }
                    }

                    // if all plays pass
                    if (pass >= players.size()) {
                        exit();
                    }

                    // reset
                    word = null;
                }
            }
        }
    }

// methods

    public void broadcast(JSONObject msg){

        for (Player player : players){
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(player.getSocket().getOutputStream(), "UTF-8"));
                out.write(msg.toString() + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(JSONObject msg, MyArrayList players){

        for (Object player : players){
            try {
                BufferedWriter out =
                        new BufferedWriter(new OutputStreamWriter(((Player) player).getSocket().getOutputStream(),
                                "UTF-8"));
                out.write(msg.toString() + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void exit(){

        JSONObject msg = new JSONObject();
        msg.put("method", "end");
        msg.put("players", players.toJson());
        msg.put("map", map);

        broadcast(msg);

        System.out.println("Game Over. Server Closed.");
        System.exit(0);
    }

}
