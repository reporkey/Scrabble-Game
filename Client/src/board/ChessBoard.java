package board;

/* NEED A NEW METHOD THAT CONVERT JSONARRAY TO MYARRAYLIST*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.Socket;
import java.util.ArrayList;

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

import room.Room;
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
import java.io.Writer;

public class ChessBoard extends JFrame implements ActionListener, KeyListener {

	private JPanel contentPane;
	private Socket client;
	private String name;
	private long id;
	private boolean myTurn = false;
	private int xx, xy;
	private MyArrayList<Player> players;
	private static JLabel P1, P2, P3, P4, scoreP1, scoreP2, scoreP3, scoreP4;
	private String voteWords;
	private JButton[][] squares = new JButton[20][20];
	Border borderW = BorderFactory.createLineBorder(Color.WHITE, 1);
	Border borderB = BorderFactory.createLineBorder(Color.BLACK, 1);

	String word = "";
	private JLabel lblTurn, labelPlayerName;
	private JLabel voteP1, voteP2, voteP3, voteP4;
	private JLabel lblImg1, lblImg2, lblImg3, lblImg4;

	public ChessBoard(String name, long id, JSONObject msg, Socket client) {
		this.name = name;
		this.id = id;
		this.client = client;

		try {
			JSONArray arr = msg.getJSONArray("players");
			players = new MyArrayList<Player>();
			Player player = new Player(msg.getJSONObject("player"));
			if (player.getId() == id) {
				this.myTurn = true;
			}
			//update players
			for (int i = 0; i < arr.length(); i++) {
				player = new Player(arr.getJSONObject(i));
				players.add(player);
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Listener listener = new Listener();
		listener.start();

		guiInitialize();
	}

	private void guiInitialize() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 711, 577);
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
		panel_2.setBounds(518, 47, 185, 489);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblPlayers = new JLabel("Player List");
		lblPlayers.setForeground(Color.WHITE);
		lblPlayers.setFont(new Font("Muna", Font.BOLD, 16));
		lblPlayers.setBounds(6, 6, 173, 24);
		panel_2.add(lblPlayers);

		P1 = new JLabel("");
		P1.setBounds(121, 42, 44, 16);
		panel_2.add(P1);

		scoreP1 = new JLabel("");
		scoreP1.setBounds(121, 62, 44, 16);
		panel_2.add(scoreP1);

		voteP1 = new JLabel("");
		voteP1.setBounds(121, 82, 44, 16);
		panel_2.add(voteP1);

		P2 = new JLabel("");
		P2.setBounds(121, 122, 44, 16);
		panel_2.add(P2);

		scoreP2 = new JLabel("");
		scoreP2.setBounds(121, 142, 44, 16);
		panel_2.add(scoreP2);

		P3 = new JLabel("");
		P3.setBounds(121, 205, 44, 16);
		panel_2.add(P3);

		scoreP3 = new JLabel("");
		scoreP3.setBounds(121, 225, 44, 16);
		panel_2.add(scoreP3);

		P4 = new JLabel("");
		P4.setBounds(121, 289, 44, 16);
		panel_2.add(P4);

		scoreP4 = new JLabel("");
		scoreP4.setBounds(121, 309, 44, 16);
		panel_2.add(scoreP4);

		labelPlayerName = new JLabel("name");
		labelPlayerName.setBounds(26, 371, 134, 33);
		labelPlayerName.setForeground(Color.WHITE);
		panel_2.add(labelPlayerName);

		lblTurn = new JLabel("turn");
		lblTurn.setBounds(26, 412, 134, 33);
		lblTurn.setForeground(Color.WHITE);
		lblTurn.setFont(new Font("Muna", Font.PLAIN, 14));
		panel_2.add(lblTurn);

		voteP2 = new JLabel("");
		voteP2.setBounds(121, 162, 44, 16);
		panel_2.add(voteP2);

		voteP3 = new JLabel("");
		voteP3.setBounds(121, 245, 44, 16);
		panel_2.add(voteP3);

		voteP4 = new JLabel("");
		voteP4.setBounds(121, 329, 44, 16);
		panel_2.add(voteP4);

		lblImg1 = new JLabel("");
		lblImg1.setBounds(26, 36, 69, 69);
		panel_2.add(lblImg1);

		lblImg2 = new JLabel("");
		lblImg2.setBounds(26, 113, 69, 69);
		panel_2.add(lblImg2);

		lblImg3 = new JLabel("");
		lblImg3.setBounds(26, 205, 69, 69);
		panel_2.add(lblImg3);

		lblImg4 = new JLabel("");
		lblImg4.setBounds(26, 273, 69, 69);
		panel_2.add(lblImg4);

		JButton btnPass = new JButton("Pass");
		btnPass.setBounds(56, 450, 104, 33);
		panel_2.add(btnPass);
		btnPass.setFont(new Font("Monaco", Font.BOLD, 16));

		// pass
		btnPass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JSONObject sendMsg = new JSONObject();
				try {
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					sendMsg.put("method", "turn");
					sendMsg.put("id", id);
					sendMsg.put("word", "");
					System.out.println("sent pass");
					output.write(sendMsg.toString() + "\n");
					output.flush();
				} catch (JSONException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		JLabel logout = new JLabel("");

		logout.setIcon(new ImageIcon(ChessBoard.class.getResource("/img/logout.png")));
		logout.setBounds(653, 8, 61, 29);
		contentPane.add(logout);

		// exit
		logout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JSONObject sendMsg = new JSONObject();
				try {
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					sendMsg.put("method", "end");
					output.write(sendMsg.toString() + "\n");
					output.flush();
					System.exit(0);
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
			if (myTurn) {
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
		Object source = e.getSource();
		for (int i = 0; i < 20; i++) {
			for (int j = 0; j < 20; j++) {
				if (source == squares[i][j]) {
					System.out.println("this: " + i + ", " + j + "; last: " + lasti + ", " + lastj);
					// if this is the first step
					if (lasti == -1 && lastj == -1) {
						if (!squares[i][j].getText().equals("")) {
							System.out.println("not empty");
							return;
						}
					}else {
						// this is not the first step
						// and same spot as last step
						if (lasti == i && lastj == j) {
							System.out.println("same spot");
						}else {
							// not same spot as last step
							// if empty, remove what player typed
							if (squares[i][j].getText().equals("")) {
								System.out.println("not same spot");
								if (!squares[i][j].getText().equals("")) {
									System.out.println("not empty");
									return;
								}else {
									squares[lasti][lastj].setText("");
								}
							}else {
								// if not empty, do nothing
								return;
							}
						}
						// and this spot is not empty
						
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
							System.out.println(sendMsg.toString());
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

	@Override
	public void keyReleased(KeyEvent e) {
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
		labelPlayerName.setText("Welcome " + name);
		labelPlayerName.setFont(new Font("Muna", Font.BOLD, 16));
		if (myTurn) {
			lblTurn.setText("Your turn.");
			//lblTurn.paintImmediately(lblTurn.getVisibleRect());
		} else {
			lblTurn.setText("Other player's turn.");
			//lblTurn.paintImmediately(lblTurn.getVisibleRect());
		}
		switch (players.size()) {
		case 4: {
			P4.setText(players.get(3).getName());
			scoreP4.setText(Integer.toString(players.get(3).getScore()));
			//scoreP4.paintImmediately(scoreP4.getVisibleRect());
			voteP4.setText(players.get(3).getVoteString());
			//voteP4.paintImmediately(voteP4.getVisibleRect());
			lblImg4.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(3).getPath())));
		}
		case 3: {
			P3.setText(players.get(2).getName());
			scoreP3.setText(Integer.toString(players.get(2).getScore()));
			//scoreP3.paintImmediately(scoreP3.getVisibleRect());
			voteP3.setText(players.get(2).getVoteString());
			//voteP3.paintImmediately(voteP3.getVisibleRect());
			lblImg3.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(2).getPath())));
		}
		case 2: {
			P2.setText(players.get(1).getName());
			scoreP2.setText(Integer.toString(players.get(1).getScore()));
			//scoreP2.paintImmediately(scoreP2.getVisibleRect());
			voteP2.setText(players.get(1).getVoteString());
			//voteP2.paintImmediately(voteP2.getVisibleRect());
			lblImg2.setIcon(new ImageIcon(ChessBoard.class.getResource(players.get(1).getPath())));
		}
		case 1: {
			P1.setText(players.get(0).getName());
			scoreP1.setText(Integer.toString(players.get(0).getScore()));
			//scoreP1.paintImmediately(scoreP1.getVisibleRect());
			voteP1.setText(players.get(0).getVoteString());
			//voteP1.paintImmediately(voteP1.getVisibleRect());
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
						// is my turn
						if (msg.getJSONObject("player").getString("name").equals(name)) {
							myTurn = true;
						} else {
							myTurn = false;
						}
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
						// update player
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
						// set the voting word
						int voteResult = showMsg(msg.getString("word"));
						JSONObject sendMsg = new JSONObject();
						try {
							output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
							sendMsg.put("method", "vote");
							sendMsg.put("value", voteResult);
							output.write(sendMsg.toString() + "\n");
							output.flush();
						} catch (JSONException | IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						break;
					}
					case "update": {
						// if is my turn, skip
						if (myTurn) {
							break;
						}
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
						// update player
						JSONArray arr = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						for (int i = 0; i < arr.length(); i++) {
							Player player = new Player(arr.getJSONObject(i));
							players.add(player);
						}
						setPlayerList();
						break;
					}
					case "end": {
						GameOver end = new GameOver(name, players, client);
						System.out.println("Game over");
						return;
					}
					}
				}
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
