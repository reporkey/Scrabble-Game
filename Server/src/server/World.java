package server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.net.ServerSocketFactory;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class World {

	private static int port;
	public volatile MyArrayList<Player> players = new MyArrayList<Player>();
	public volatile MyArrayList<Player> invitedPlayers = new MyArrayList<Player>();
	public volatile MyArrayList<Player> potentialPlayers = new MyArrayList<Player>();
	private volatile JSONArray map = new JSONArray();
	public volatile String word = null;
	private volatile int pass = 0;
	private volatile boolean start = false;

	public World(int port) {
		this.port = port;
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

		while (true) {
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				String msgStr = null;
	
				if ((msgStr = in.readLine()) != null) {
	
					JSONObject msg = new JSONObject(msgStr);
					JSONObject send = new JSONObject();
					switch (msg.getString("method")) {
					
					case "update": {
//						System.out.println("Server get map");
						map = msg.getJSONArray("map");
						//System.out.println(map);
						break;
					}
					
					case "join game": {
						// check name validation (is duplication)
						String checkName = msg.getString("name");
						Boolean isValid = true;
						for (Player player : potentialPlayers) {
							if (player.getName().equals(checkName)) {
								isValid = false;
								break;
							}
						}
						for (Player player : invitedPlayers) {
							if (player.getName().equals(checkName)) {
								isValid = false;
								break;
							}
						}
						for (Player player : players) {
							if (player.getName().equals(checkName)) {
								isValid = false;
								break;
							}
						}
						send.put("check", isValid);
						Player player = new Player(client, msg);
						if (isValid) {
							potentialPlayers.add(player);
						}
						send.put("method", "join game");
						send.put("player", player.getObj());
						System.out.println("Join game: " + player.getName() + " Id: " + player.getId());
						MyArrayList<Player> temp = new MyArrayList<Player>();
						temp.add(player);
						broadcast(send, temp);
						// then update to all
						send.put("method", "update");
						send.put("potential players", potentialPlayers.toJson());
						send.put("players", players.toJson());
						broadcast(send, potentialPlayers);
						broadcast(send, players);
						break;
					}
	
					case "request invite": {
						
						// add invited player to invited players
						JSONArray arr = msg.getJSONArray("players");
						ArrayList<Integer> toMoveIndex = new ArrayList<Integer>();
						for (int i = 0; i < arr.length(); i++) {
							JSONObject invited = arr.getJSONObject(i);
							long id = invited.getLong("id");
							for (int j = 0; j < potentialPlayers.size(); j++) {
								Player player = potentialPlayers.get(j);
								if (id == player.getId()) {
									invitedPlayers.add(player);
									toMoveIndex.add(j);
								}
							}
						}
						// remove them from potential players
						for (int i = toMoveIndex.size() - 1; i >= 0; i--) {
							potentialPlayers.remove((int) toMoveIndex.get(i));
						}
						// set inviter "accept" and move to players from potential
						JSONObject inviter = new JSONObject();
						long id = msg.getLong("id");
						// if the inviter is already in the player list
						for (int i = 0; i < players.size(); i++) {
							if (id == players.get(i).getId()) {
								inviter = players.get(i).getObj();
								break;
							}
						}
						for (int i = 0; i < potentialPlayers.size(); i++) {
							if (id == potentialPlayers.get(i).getId()) {
								potentialPlayers.get(i).setAccept(1);
								players.add(potentialPlayers.get(i));
								inviter = potentialPlayers.get(i).getObj();
								// then remove from potential
								potentialPlayers.remove(i);
								break;
							}
						}
						// send back inviter status
						send.put("method", "update");
						send.put("potential players", potentialPlayers.toJson());
						send.put("players", players.toJson());
						broadcast(send, players);
						broadcast(send, potentialPlayers);
						// send to invited
						send = new JSONObject();
						send.put("method", "request invite");
						send.put("from", inviter);
						broadcast(send, invitedPlayers);
						break;
					}
	
					case "response invite": {
						String name = "";
						long id = msg.getLong("id");
						// set player accept
						for (int i = 0; i < invitedPlayers.size(); i++) {
							if (id == invitedPlayers.get(i).getId()) {
								invitedPlayers.get(i).setAccept(msg.getInt("value"));
								if (msg.getInt("value") == 1) {
									// move to players from invited players if accept
									players.add(invitedPlayers.get(i));
								} else {
									// move to potential from invited players if deny
									potentialPlayers.add(invitedPlayers.get(i));
								}
								name = invitedPlayers.get(i).getName();
								invitedPlayers.remove(i);
								break;
							}
						}
	
						send.put("method", "update");
						send.put("potential players", potentialPlayers.toJson());
						send.put("players", players.toJson());
						broadcast(send, potentialPlayers);
						broadcast(send, players);
						System.out.print("Request responce: from: " + name + " value: " + msg.getInt("value"));
						break;
					}
	
					case "leave room": {
						long id = msg.getLong("id");
						for (int i = 0; i < players.size(); i++) {
							if (id == players.get(i).getId()) {
								players.get(i).setAccept(0);
								potentialPlayers.add(players.get(i));
								players.remove(i);
								break;
							}
						}
						System.out.println("leave: " + id);
						send.put("method", "update");
						send.put("potential players", potentialPlayers.toJson());
						send.put("players", players.toJson());
						broadcast(send, potentialPlayers);
						broadcast(send, players);
						break;
					}
	
					case "start game": {
						// remove all invited players
						for (Player invitedplayer : invitedPlayers) {
							potentialPlayers.add(invitedplayer);
						}
						invitedPlayers.clear();
						start = true;
						break;
					}
	
					case "turn": {
						String name = "";
						long id = msg.getLong("id");
						for (Player player : potentialPlayers) {
							if (id == player.getId()) {
								name = player.getName();
								break;
							}
						}
						System.out.println("Receive word from: " + name + " word: " + msg.getString("word"));
						word = msg.getString("word");
						if (msg.has("map")) {
							map = (JSONArray) msg.get("map");
						}
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
						send.put("method", "update");
						send.put("players", players.toJson());
						broadcast(send, players);
						break;
					}
					}
				}
			} catch (IOException e) {
				// connection with this client is lost 
				break;
			}
		}
	}

	public class Update implements Runnable {
		public void run() {
			while (true) {

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// update each half second
				JSONObject send = new JSONObject();
				try {
					send.put("method", "update");
					if (map.length() >= 1) {
						send.put("map", map);
					}
					send.put("potential players", potentialPlayers);
					send.put("players", players.toJson());
					broadcast(send, players);
					broadcast(send, potentialPlayers);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	public class Game implements Runnable {

		public void run() {

			// Start a new thread to keep update
			Thread update = new Thread(new Update());
			update.start();

			// Check accept
			while (!start) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			start = false;
			System.out.println("Game start!!!!!!!!");
			System.out.println();
			for (Player player : players) {
				System.out.println("players in game: " + player.getName());
			}

			int turn = -1;
			// game begin
			while (true) {
				// iterate player's turn
				turn++;
				// use to check is this player disconnected during his turn
				Player player = players.get(turn%players.size());
				long playerId = players.get(turn%players.size()).getId();
				try {
					// reset
					word = null;
					for (Player temp2 : players) {
						temp2.setVote(-1);
					}
					// if all plays pass
					if (pass >= players.size()) {
						try {
							exit();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println();
					}
		
					// send whose turn
					JSONObject send = new JSONObject();
					send.put("method", "turn");
					send.put("map", map);
					send.put("player", player.getObj());
					send.put("players", players.toJson());
					broadcast(send, players);
	
					// wait until receive a word
					while (true) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (playerId != players.get(turn%players.size()).getId()) {
							break;
						}
						if (word != null) {
							break;
						}
					}
					if (playerId != players.get(turn%players.size()).getId()) {
						continue;
					}
	
					// the player does not pass, send vote
					if (!word.equals("")) {
						pass = 0;
						player.setVote(1);
						send = new JSONObject();
						try {
							send.put("method", "vote");
							send.put("word", word);
							send.put("map", map);
							send.put("player", player.getObj());
							send.put("players", players.toJson());
							MyArrayList<Player> temp = new MyArrayList<Player>();
							for (Player each : players) {
								if (!each.equals(player)) {
									temp.add(each);
								}
							}
							broadcast(send, temp);
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
					boolean vote = true;
					while (true) {
						
						int count = 0;
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (playerId != players.get(turn%players.size()).getId()) {
							break;
						}
						for (Player temp : players) {
							if (temp.getVote() == 1) {
								count++;
							} else if (temp.getVote() == 0) {
								count++;
								vote = false;
							}
						}
						if (count == players.size()) {
							break;
						}
					}
					if (playerId != players.get(turn%players.size()).getId()) {
						continue;
					}
	
					// count score depend on the vote
					if (vote) {
						player.setScore(player.getScore() + word.length());
						System.out.println(player.getScore());
					} else {
						continue;
					}
				} catch(ArithmeticException e) {
					// player size == 0
					break;
				} catch (Exception e) {
					System.out.println(e);
					continue;
				}
			}
			Thread game = new Thread(new Game());
			game.start();
		}
	}

	// methods

	public void broadcast(JSONObject msg, MyArrayList players) {

		// check connection of players
		ArrayList<Integer> toRemove = new ArrayList<Integer>();
		for (int i = 0; i < players.size(); i++) {
			Object player = players.get(i);
			try {
				BufferedWriter out = new BufferedWriter(
						new OutputStreamWriter(((Player) player).getSocket().getOutputStream(), "UTF-8"));
				out.write(msg.toString() + "\n");
				out.flush();
			} catch (SocketException e1) {
				// client connection lost
				// kick the player out
				toRemove.add(i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// remove them from list
		for (int i = toRemove.size()-1; i >= 0; i--) {
			players.remove((int) toRemove.get(i));
		}
	}

	public void exit() throws JSONException {

		JSONObject msg = new JSONObject();
		msg.put("method", "end");
		msg.put("players", players.toJson());
		msg.put("map", map);

		broadcast(msg, players);

		System.out.println("Game Over.");
		players.clear();;
	}

}
