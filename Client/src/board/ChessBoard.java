package board;

/* NEED A NEW METHOD THAT CONVERT JSONARRAY TO MYARRAYLIST*/

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import utilities.MyArrayList;
import utilities.Player;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class ChessBoard extends JFrame implements ActionListener, KeyListener {

	private JPanel contentPane;
	private Socket client;
	private String name;
	private long id;
	private long whoseTurnId = 0;
	private int xx, xy;
	private MyArrayList<Player> players;
	private static JLabel P1, P2, P3, P4, scoreP1, scoreP2, scoreP3, scoreP4;
	private JButton[][] squares = new JButton[20][20];
	Border borderW = BorderFactory.createLineBorder(Color.WHITE, 1);
	Border borderB = BorderFactory.createLineBorder(Color.BLACK, 1);

	String word = "";
	private JLabel lblTurn, labelPlayerName;
	private JLabel voteP1, voteP2, voteP3, voteP4;
	private JLabel lblImg1, lblImg2, lblImg3, lblImg4;

	public ChessBoard(long id, JSONObject msg, Socket client) {
		this.id = id;
		this.client = client;

		try {
			JSONArray arr = msg.getJSONArray("players");
			players = new MyArrayList<Player>();
			Player player = new Player(msg.getJSONObject("player"));
			this.whoseTurnId = player.getId();
			//update players
			for (int i = 0; i < arr.length(); i++) {
				player = new Player(arr.getJSONObject(i));
				if (player.getId() == id) {
					this.name = player.getName();
				}
				players.add(player);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		guiInitialize();
		
		Listener listener = new Listener();
		listener.start();
		
		Update update = new Update();
		update.start();
	}

	private void guiInitialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 753, 563);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				ChessBoard.this.setLocation(x - xx, y - xy);
			}
		});
		contentPane.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				xx = e.getX();
				xy = e.getY();
			}
		});
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(6, 47, 500, 489);

		panel.setLayout((new GridLayout(20, 20)));
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				squares[i][j] = new JButton();
				squares[i][j].setBackground(Color.white);
				squares[i][j].setOpaque(true);
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(borderB);
				panel.add(squares[i][j]);
				squares[i][j].addActionListener(this);
				squares[i][j].addKeyListener(this);
			}
		}

		setResizable(false);
		setLocationRelativeTo(null);
		panel.setVisible(true);
		contentPane.add(panel);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(84, 127, 206));
		panel_2.setBounds(518, 47, 229, 489);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblPlayers = new JLabel("Player List");
		lblPlayers.setForeground(Color.WHITE);
		lblPlayers.setFont(new Font("Muna", Font.BOLD, 16));
		lblPlayers.setBounds(6, 6, 173, 24);
		panel_2.add(lblPlayers);

		P1 = new JLabel("");
		P1.setBounds(121, 42, 102, 16);
		panel_2.add(P1);

		scoreP1 = new JLabel("");
		scoreP1.setBounds(121, 62, 102, 16);
		panel_2.add(scoreP1);

		voteP1 = new JLabel("");
		voteP1.setBounds(121, 82, 102, 16);
		panel_2.add(voteP1);
		
		lblImg1 = new JLabel("");
		lblImg1.setBounds(26, 36, 69, 69);
		panel_2.add(lblImg1);

		P2 = new JLabel("");
		P2.setBounds(121, 122, 102, 16);
		panel_2.add(P2);

		scoreP2 = new JLabel("");
		scoreP2.setBounds(121, 142, 102, 16);
		panel_2.add(scoreP2);
		
		voteP2 = new JLabel("");
		voteP2.setBounds(121, 162, 102, 16);
		panel_2.add(voteP2);

		lblImg2 = new JLabel("");
		lblImg2.setBounds(26, 113, 69, 69);
		panel_2.add(lblImg2);

		P3 = new JLabel("");
		P3.setBounds(121, 205, 102, 16);
		panel_2.add(P3);

		scoreP3 = new JLabel("");
		scoreP3.setBounds(121, 225, 102, 16);
		panel_2.add(scoreP3);

		voteP3 = new JLabel("");
		voteP3.setBounds(121, 245, 102, 16);
		panel_2.add(voteP3);

		lblImg3 = new JLabel("");
		lblImg3.setBounds(26, 205, 69, 69);
		panel_2.add(lblImg3);

		P4 = new JLabel("");
		P4.setBounds(121, 289, 102, 16);
		panel_2.add(P4);

		scoreP4 = new JLabel("");
		scoreP4.setBounds(121, 309, 102, 16);
		panel_2.add(scoreP4);	

		voteP4 = new JLabel("");
		voteP4.setBounds(121, 329, 102, 16);
		panel_2.add(voteP4);

		lblImg4 = new JLabel("");
		lblImg4.setBounds(26, 273, 69, 69);
		panel_2.add(lblImg4);

		labelPlayerName = new JLabel(name);
		labelPlayerName.setFont(new Font("Muna", Font.BOLD, 14));
		labelPlayerName.setBounds(56, 371, 134, 33);
		labelPlayerName.setForeground(Color.WHITE);
		panel_2.add(labelPlayerName);

		lblTurn = new JLabel("turn");
		lblTurn.setBounds(26, 412, 134, 33);
		lblTurn.setForeground(Color.WHITE);
		lblTurn.setFont(new Font("Muna", Font.PLAIN, 14));
		panel_2.add(lblTurn);

		JButton btnPass = new JButton("Pass");
		btnPass.setBounds(56, 450, 104, 33);
		panel_2.add(btnPass);
		btnPass.setFont(new Font("Monaco", Font.BOLD, 16));
		
		JLabel lblHi = new JLabel("HI,");
		lblHi.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		lblHi.setForeground(Color.WHITE);
		lblHi.setBounds(26, 379, 18, 16);
		panel_2.add(lblHi);
		
		JLabel logout = new JLabel("");

		logout.setIcon(new ImageIcon(ChessBoard.class.getResource("/img/logout.png")));
		logout.setBounds(704, 6, 43, 29);
		contentPane.add(logout);

		// exit
		logout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JSONObject sendMsg = new JSONObject();
//				try {
//					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
//					sendMsg.put("method", "quit");
//					sendMsg.put("id", id);
//					output.write(sendMsg.toString() + "\n");
//					output.flush();
					System.exit(0);
//				} catch (JSONException | IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
			}
		});
		
		// pass
		btnPass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// ignore if it's not myTurn
				if (whoseTurnId != id) {
					return;
				}
				JSONObject sendMsg = new JSONObject();
				try {
					if (lasti != -1 && lastj != -1) {
						squares[lasti][lastj].setText("");
					}
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					sendMsg.put("method", "turn");
					sendMsg.put("id", id);
					sendMsg.put("word", "");
					//System.out.println("sent pass");
					output.write(sendMsg.toString() + "\n");
					output.flush();
				} catch (JSONException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	
	// button click
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			// only if its my turn
			if (whoseTurnId == id) {
				Object source = e.getSource();
				for (int x = 0; x < 20; x++) {
					for (int y = 0; y < 20; y++) {
						if (source == squares[x][y]) {
							processClick(x, y);
						}
					}
				}
			}
		}
	}

	int lasti = -1;
	int lastj = -1;
	@Override
	public void keyPressed(KeyEvent e) {
		// ignore if not my turn
		if (whoseTurnId != id) {
			return;
		}
		Object source = e.getSource();
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				if (source == squares[i][j]) {
					System.out.println("this: " + i + ", " + j + "; last: " + lasti + ", " + lastj);
					// if this is the first step
					if (lasti == -1 && lastj == -1) {
						// and the spot is not empty
						if (!squares[i][j].getText().equals("")) {
							return;
						}
					}else {
						// this is not the first step
						// and it's not the same spot 
						if (lasti != i || lastj != j) {
							// and the spot is not empty
							if (!squares[i][j].getText().equals("")) {
								return;
							}else {
								squares[lasti][lastj].setText("");
							}
						}else {
							// this is the same spot
						}
					}
						
					if (e.getKeyCode() >= 65 && e.getKeyCode() <= 90) {
						char input = e.getKeyChar();
						lasti = i;
						lastj = j;
						// System.out.println(e.getKeyChar());
						squares[i][j].setText(Character.toString(input));
					} else if (!squares[i][j].getText().equals("") && e.getKeyCode() >= 37 && e.getKeyCode() <= 40) { // UP & DOWN
						squares[i][j].setBorderPainted(true);
						// LEFT & RIGHT
						if (e.getKeyCode() == 38 || e.getKeyCode() == 40) {
							word = checkVWord(i, j);
						} else { 
							word = checkHWord(i, j);
						}
						System.out.println("My selected word: " + word);
						JSONObject sendMsg = new JSONObject();
						try {
							// send word to server
							Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
							sendMsg.put("method", "turn");
							sendMsg.put("id", id);
							sendMsg.put("word", word);
							// generated 2D json array
							JSONArray map = new JSONArray();
							for (int x = 0; x < 20; x++) {
								JSONArray cloumn = new JSONArray();
								for (int y = 0; y < 20; y++) {
									cloumn.put(squares[x][y].getText());
								}
								map.put(cloumn);
							}
							sendMsg.put("map", map);
							output.write(sendMsg.toString() + "\n");
							output.flush();
							lasti = -1;
							lastj = -1;
						} catch (JSONException | IOException e1) { // TODOAuto-generated catch block
							e1.printStackTrace();
						}
					} else {
						JOptionPane.showMessageDialog(null, "Please input an alphabet", "Oops!",
							JOptionPane.WARNING_MESSAGE);
					}
				}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	// word confirm
	@Override
	public void keyReleased(KeyEvent e) {
		e.getSource();
	}

	// is valid if no character placed before
	private boolean isValidMove(int i, int j) {
		if (squares[i][j].getText().equals(""))
			return true;
		else
			return false;
	}

	// the effect of border and show character
	private void processClick(int row, int col) {
		if (!isValidMove(row, col))
			return;

		// border letters
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(borderB);
				squares[i][j].setBackground(Color.WHITE);
			}
		}
		squares[row][col].setBorderPainted(true);
		squares[row][col].setBorder(borderW);
		squares[row][col].setBackground(Color.yellow);
	}

	// get the vertical word
	protected String checkVWord(int row, int col) {
		String word = "";
		int start = 0;
		for (int i = row; i > 0; i--) {
			if (squares[i][col].getText().equals("")) {
				start = i + 1;
				break;
			}
		}

		for (int i = start; true; i++) {
			if (!squares[i][col].getText().equals("")) {
				word = word + squares[i][col].getText();
				squares[i][col].setBorderPainted(true);
				squares[i][col].setBorder(borderB);
				squares[i][col].setBackground(Color.yellow);
			} else {
				break;
			}
		}
		System.out.println("vertical: " + word);
		return word;
	}

	// get the horizontal word
	protected String checkHWord(int row, int col) {
		String word = "";
		int start = 0;
		for (int j = col; j > 0; j--) {
			if (squares[row][j].getText().equals("")) {
				start = j + 1;
				break;
			}
		}
		for (int j = start; true; j++) {
			if (!squares[row][j].getText().equals("")) {
				word = word + squares[row][j].getText();
				squares[row][j].setBorderPainted(true);
				squares[row][j].setBorder(borderB);
				squares[row][j].setBackground(Color.yellow);
			} else {
				break;
			}
		}
		System.out.println("horizontal: " + word);
		return word;
	}

	// display score
	public void setPlayerList() {
		if (whoseTurnId == id) {
			lblTurn.setText("Your turn.");
		} else {
			// whose turn
			String name = "Other player";
			for (Player player : players) {
				if (player.getId() == whoseTurnId) {
					name = player.getName();
				}
			}
			lblTurn.setText(name + "'s turn.");
		}
		P4.setVisible(false);
		scoreP4.setVisible(false);
		voteP4.setVisible(false);
		lblImg4.setVisible(false);
		P3.setVisible(false);
		scoreP3.setVisible(false);
		voteP3.setVisible(false);
		lblImg3.setVisible(false);
		P2.setVisible(false);
		scoreP2.setVisible(false);
		voteP2.setVisible(false);
		lblImg2.setVisible(false);
		P1.setVisible(false);
		scoreP1.setVisible(false);
		voteP1.setVisible(false);
		lblImg1.setVisible(false);
		switch (players.size()) {
		case 4: {
			P4.setVisible(true);
			scoreP4.setVisible(true);
			voteP4.setVisible(true);
			lblImg4.setVisible(true);
			
			P4.setText(players.get(3).getName());
			scoreP4.setText(Integer.toString(players.get(3).getScore()));
			voteP4.setText(players.get(3).getVoteString());
			lblImg4.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(3).getPath())));
		}
		case 3: {
			P3.setVisible(true);
			scoreP3.setVisible(true);
			voteP3.setVisible(true);
			lblImg3.setVisible(true);
			
			P3.setText(players.get(2).getName());
			scoreP3.setText(Integer.toString(players.get(2).getScore()));
			voteP3.setText(players.get(2).getVoteString());
			lblImg3.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(2).getPath())));
		}
		case 2: {
			P2.setVisible(true);
			scoreP2.setVisible(true);
			voteP2.setVisible(true);
			lblImg2.setVisible(true);
			
			P2.setText(players.get(1).getName());
			scoreP2.setText(Integer.toString(players.get(1).getScore()));
			voteP2.setText(players.get(1).getVoteString());
			lblImg2.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(1).getPath())));
		}
		case 1: {
			P1.setVisible(true);
			scoreP1.setVisible(true);
			voteP1.setVisible(true);
			lblImg1.setVisible(true);
			
			P1.setText(players.get(0).getName());
			scoreP1.setText(Integer.toString(players.get(0).getScore()));
			voteP1.setText(players.get(0).getVoteString());
			lblImg1.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(0).getPath())));
			break;
		}
		default: {
			System.out.print("Wrong size" + players.size());
		}
		}
	}

	// show vote message
	public static int showMsg(String word) {
		String[] buttons = { "No", "YES" };
		// response =0 is no, =1 is yes
		int response = 0;
		String mgs = "Please vote";
		response = JOptionPane.showOptionDialog(null, "Do you agree [ " + word + " ] is a word?", mgs,
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
		return response;
	}

	// listener subclass / thread
	private class Listener extends Thread {

		@Override
		public void run() {
			String msgStr = null;

			try {
				Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
				BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				while ((msgStr = input.readLine()) != null) {

					JSONObject msg = new JSONObject(msgStr);
					switch (msg.getString("method")) {
					case "turn": {
						// whose turn
						whoseTurnId = msg.getJSONObject("player").getLong("id");						
						// update map
						if (msg.has("map")) {
							JSONArray map = msg.getJSONArray("map");
							for (int i = 0; i < map.length(); i++) {
								JSONArray row = map.getJSONArray(i);
								for (int j = 0; j < row.length(); j++) {
									squares[i][j].setText(row.getString(j));
								}
							}
						}
						// update players
						JSONArray arr = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						for (int i = 0; i < arr.length(); i++) {
							Player player = new Player(arr.getJSONObject(i));
							players.add(player);
						}
						setPlayerList();
						break;
					}
					case "vote": {
						System.out.println("start vote");
						// update map
						JSONArray map = msg.getJSONArray("map");
						for (int i = 0; i < map.length(); i++) {
							JSONArray row = map.getJSONArray(i);
							for (int j = 0; j < row.length(); j++) {
								squares[i][j].setText(row.getString(j));
							}
						}
						// update players
						JSONArray arr = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						for (int i = 0; i < arr.length(); i++) {
							Player player = new Player(arr.getJSONObject(i));
							players.add(player);
						}
						setPlayerList();
						// set the voting word
						int voteResult = showMsg(msg.getString("word"));
						JSONObject sendMsg = new JSONObject();
						try {
							output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
							sendMsg.put("method", "vote");
							sendMsg.put("value", voteResult);
							sendMsg.put("id", id);
							output.write(sendMsg.toString() + "\n");
							output.flush();
						} catch (JSONException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					}
					case "update": {
						// update players
						JSONArray arr = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						for (int i = 0; i < arr.length(); i++) {
							Player player = new Player(arr.getJSONObject(i));
							players.add(player);
						}
						setPlayerList();
						// if is my turn, skip map
						if (whoseTurnId == id) {
							break;
						}
						// update map
						try {
							JSONArray map = msg.getJSONArray("map");
							//System.out.println("update map");
							if (map != null) {
								for (int i = 0; i < map.length(); i++) {
									JSONArray row = map.getJSONArray(i);
									for (int j = 0; j < row.length(); j++) {
										squares[i][j].setText(row.getString(j));
									}
								}
							}
						} catch (JSONException e) {
							// does have map
						}
						break;
					}
					case "end": {
						GameOver end = new GameOver(name, players, client);
						System.out.println("Game over");
						end.setVisible(true);
						dispose();
						return;
					}
					}
				}
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	private class Update extends Thread{
		public void run() {
			while (true) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// update map status each half second
				if (whoseTurnId == id) {
					try {
						JSONObject send = new JSONObject();
						Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
						send.put("method", "update");
						// generated 2D json array
						JSONArray map = new JSONArray();
						for (int x = 0; x < 20; x++) {
							JSONArray cloumn = new JSONArray();
							for (int y = 0; y < 20; y++) {
								cloumn.put(squares[x][y].getText());
							}
							map.put(cloumn);
						}
						send.put("map", map);
						output.write(send.toString() + "\n");
						output.flush();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// server terminate
						JOptionPane.showMessageDialog(null, "Disconnect with server.", "Disconnection",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				}
			}
		}
	}
}
