package server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.*;
import java.util.Collections;
import java.util.Iterator;

public class World {

	private static final int port = 8080;
	public volatile MyArrayList<Player> players = new MyArrayList<Player>();
	public volatile MyArrayList<Player> invitedPlayers = new MyArrayList<Player>();
	public volatile MyArrayList<Player> potentialPlayers = new MyArrayList<Player>();
	private volatile JSONArray map = new JSONArray();
	public volatile String word = null;
	private volatile int pass = 0;
	private volatile boolean start = false;

	public World() {

		ServerSocketFactory factory = ServerSocketFactory.getDefault();

		// Game begin
		Thread game = new Thread(new Game());
		game.start();

		try (ServerSocket server = factory.createServerSocket(port)) {
			System.out.println("Waiting for client connection..");
			System.out.println(server.getInetAddress());
			System.out.println(server.getLocalPort());

			// Wait for connections.
			while (true) {
				Socket client = server.accept();
				System.out.println("Client: Applying for connection!");

				// Start a new thread to read msg
				Thread myListener = new Thread(() -> {
					try {
						listener(client);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				myListener.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void listener(Socket client) throws JSONException {

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
					send.put("potentialPlayers", potentialPlayers.toJson());
					send.put("players", players.toJson());
					System.out.println("Join game: " + player.getName());
					broadcast(send, potentialPlayers);
					break;
				}

				case "request invite": {
					JSONArray arr = msg.getJSONArray("players");
					for (int i = 0; i < arr.length(); i++) {
						JSONObject invited = arr.getJSONObject(i);
						String name = invited.getString("name");
						for (Player player : potentialPlayers) {
							if (name.equals(player.getObj().get("name"))) {
								invitedPlayers.add(player);
							}
						}
					}
					// from
					String ip = client.getInetAddress().getHostAddress();
					int port = client.getPort();
					String name = "";
					for (Player player : potentialPlayers) {
						if (ip.equals(player.getIp()) && port == player.getPort()) {
							name = player.getName();							
							if(!players.contains(player)){
								player.setAccept(1);
								players.add(player);
							}								
							break;
						}
					}
					send.put("method", "request invite");
					send.put("from", name);
					broadcast(send, invitedPlayers);
					// print
					System.out.print("Request invite: from " + name + " to -> ");
					for (Player player : invitedPlayers) {
						System.out.print(player.getName() + " || ");
					}
					System.out.println();
					break;
				}

				case "response invite": {
					String ip = client.getInetAddress().getHostAddress();
					int port = client.getPort();
					String name = "";
					// search which player send the msg
					for (Player player : potentialPlayers) {
						// if the player accept, put into players list
						if (ip.equals(player.getIp()) && port == player.getPort()) {
							player.setAccept(msg.getInt("value"));
							name = player.getName();
							players.add(player);
							break;
						}
					}
					send.put("method", "response invite");
					send.put("players", players.toJson());
					broadcast(send, potentialPlayers);
					System.out.print("Request responce: from: " + name + " value: " + msg.getInt("value"));
					break;
				}

				case "start game": {
					start = true;
				}

				case "turn": {
					String name = "";
					String ip = client.getInetAddress().getHostAddress();
					int port = client.getPort();
					for (Player player : potentialPlayers) {
						if (ip.equals(player.getIp()) && port == player.getPort()) {
							name = player.getName();
							break;
						}
					}
					System.out.println("Receive word from: " + name + " word: " + msg.getString("word"));
					word = msg.getString("word");
					map = (JSONArray) msg.get("map");
					break;
				}

				case "vote": {
					String ip = client.getInetAddress().getHostAddress();
					int port = client.getPort();
					String name = "";
					// search which player send the msg
					for (Player player : players) {
						if (ip.equals(player.getIp()) && port == player.getPort()) {
							name = player.getName();
							player.setVote(msg.getInt("value"));
						}
					}
					System.out.println("Vote: " + name + "voted " + msg.getInt("value"));
					send.put("method", "update vote");
					send.put("players", players.toJson());
					broadcast(send, players);
					break;
				}

				case "end":
					exit();// sever cannot close
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class Game implements Runnable {

		public void run() {

			// Check accept
			while (true) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int accept = 0;
				for (Player player : players) {
					if (player.getAccept() == 1) {
						accept++;
					}
				}
				/*
				 * for (Player player : players) { if (player.getAccept() == 1)
				 * { accept++; } }
				 */
				if (accept >= players.size() && accept > 1) {
					if (start) break;
				}
			}

			for (Player player : players) {
				System.out.println(player.getName());
			}

			// game begin
			while (true) {
				// iterate player's turn
				for (Player player : players) {
					
					// if all plays pass
					System.out.println("pass: " + pass + ", player size: " + players.size());
					if (pass >= players.size()) {
						try {
							exit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					System.out.println("SERVER:: This is " + player.getName() + "'s turn.");

					// send whose turn
					JSONObject send = new JSONObject();
					try {
						send.put("method", "turn");
						send.put("map", map);
						send.put("player", player.getObj());
						send.put("players", players.toJson());
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					broadcast(send, players);

					// wait until receive a word
					while (true) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (word != null) {
							break;
						}
					}

					// the player does not pass, send vote
					if (!word.equals("")) {
						pass = 0;
						send = new JSONObject();
						try {
							send.put("method", "vote");
							send.put("word", word);
							send.put("map", map);
							send.put("player", player.getObj());
							broadcast(send, players);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						pass++;
						word = null;
						continue;
					}

					// wait until receive all vote
					int vote = 0;
					while (true) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						vote = 0;
						for (Player temp : players) {
							if (temp.getVote() == 1) {
								vote++;
							} else if (temp.getVote() == 0) {
								vote = -1;
								break;
							}
						}
						if (vote == -1 || vote == players.size()) {
							for (Player temp2 : players) {
								temp2.setVote(-1);
							}
							break;
						}
					}

					// if all players had voted or someone vote no
					if (vote == players.size()) {
						player.setScore(player.getScore() + word.length());
						System.out.println(player.getScore());
					} else if (vote == -1) {
						continue;
					}

					// if map full
		/*			for (int i = 0; i < map.length(); i++) {
						JSONArray row;
						try {
							int empty = 0;
							row = map.getJSONArray(i);
							for (int j = 0; j < map.length(); j++) {
								String letter = (String) row.get(i);
								if (letter == "") {
									empty++;
								}
							}
							if (empty == 0) {
								System.out.println("SERVER:: map is full");
								exit();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}*/

					// reset
					word = null;
				}
			}
		}
	}

	// methods

	public void broadcast(JSONObject msg, MyArrayList players) {

		for (Object player : players) {
			try {
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(((Player) player).getSocket().getOutputStream(), "UTF-8"));
				out.write(msg.toString() + "\n");
				out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void exit() throws JSONException {

		JSONObject msg = new JSONObject();
		msg.put("method", "end");
		msg.put("players", players.toJson());
		msg.put("map", map);

		broadcast(msg, players);

		System.out.println("Game Over. Server Closed.");
		System.exit(0);
	}

}
