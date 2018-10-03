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
import room.Room.Listener;
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

public class ChessBoard extends JFrame {

	private JPanel contentPane;
	private Socket client;
	private String name;
	private boolean myTurn = false;
	private int xx, xy;
	private MyArrayList<Player> players;
	private static JLabel P1, P2, P3, P4, P5, P6, P7, scoreP1, scoreP2, scoreP3, scoreP4, scoreP5, scoreP6, scoreP7;
	private String voteWords;
	private JButton[][] squares = new JButton[20][20];
	Border borderW = BorderFactory.createLineBorder(Color.WHITE, 1);
	Border borderB = BorderFactory.createLineBorder(Color.BLACK, 1);

	String word = "";
	private JLabel lblTurn, voteWord, labelPlayerName;
	private JLabel voteP1,voteP2,voteP3,voteP4,voteP5,voteP6,voteP7;

	public ChessBoard(String name, JSONObject msg, Socket client, boolean myTurn) {
		this.name = name;
		this.myTurn = myTurn;
		this.client = client;
		try {
			JSONArray arr = msg.getJSONArray("players");
			players = new MyArrayList<Player>();
			for (int i = 0; i < arr.length(); i++) {
				Player player = new Player(arr.getJSONObject(i));
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
		setBounds(100, 100, 744, 725);
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
				squares[i][j].setBackground(new Color(242, 229, 210));
				squares[i][j].setOpaque(true);
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(borderW);
				panel.add(squares[i][j]);
				squares[i][j].addActionListener((e) -> {
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
						myTurn = false;
					}
				});
			}
		}

		setResizable(false);
		setLocationRelativeTo(null);
		panel.setVisible(true);
		contentPane.add(panel);

		JLabel lblPleaseVote = new JLabel("Do you think string in black box is a word? Please vote... ");
		lblPleaseVote.setForeground(Color.ORANGE);
		lblPleaseVote.setFont(new Font("Skia", Font.PLAIN, 16));
		lblPleaseVote.setBounds(16, 548, 490, 16);
		contentPane.add(lblPleaseVote);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(26, 587, 277, 93);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JButton btnYes = new JButton("Yes!");
		btnYes.setFont(new Font("Monaco", Font.BOLD, 18));
		btnYes.setBounds(6, 46, 113, 35);
		panel_1.add(btnYes);

		JButton btnNo = new JButton("No..");
		btnNo.setFont(new Font("Monaco", Font.BOLD, 18));
		btnNo.setBounds(151, 46, 106, 35);
		panel_1.add(btnNo);

		voteWord = new JLabel("");
		voteWord.setFont(new Font("Monaco", Font.BOLD, 17));
		voteWord.setBounds(6, 6, 106, 28);
		panel_1.add(voteWord);
/*
		// the label showing the voting result
		JLabel lblVResult = new JLabel();
		lblVResult.setBounds(518, 576, 196, 116);
		contentPane.add(lblVResult);

		// if voting result is no
		ImageIcon icon = new ImageIcon(ChessBoard.class.getResource("/img/sad.png"));
		Image image = icon.getImage();
		Image newimg = image.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg));

		// if voting result is yes
		ImageIcon icon2 = new ImageIcon(ChessBoard.class.getResource("/img/smile.png"));
		Image image2 = icon2.getImage();
		Image newimg2 = image2.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg2));

		// when no result need to be displayed
		ImageIcon icon3 = new ImageIcon(ChessBoard.class.getResource("/img/question.png"));
		Image image3 = icon3.getImage();
		Image newimg3 = image3.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg3));*/

		JLabel lblVotingResult = new JLabel("Voting Result:");
		lblVotingResult.setForeground(new Color(255, 153, 204));
		lblVotingResult.setFont(new Font("Apple Braille", Font.BOLD, 18));
		lblVotingResult.setBounds(518, 538, 196, 35);
		contentPane.add(lblVotingResult);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(84, 127, 206));
		panel_2.setBounds(518, 47, 185, 489);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		JLabel lblPlayers = new JLabel("Players/Score/Vote");
		lblPlayers.setForeground(Color.WHITE);
		lblPlayers.setFont(new Font("Muna", Font.BOLD, 16));
		lblPlayers.setBounds(6, 6, 173, 24);
		panel_2.add(lblPlayers);

		P1 = new JLabel("");
		P1.setBounds(26, 42, 44, 16);
		panel_2.add(P1);

		scoreP1 = new JLabel("");
		scoreP1.setBounds(75, 42, 44, 16);
		panel_2.add(scoreP1);
		
		voteP1 = new JLabel("");
		voteP1.setBounds(121, 42, 44, 16);
		panel_2.add(voteP1);

		P2 = new JLabel("");
		P2.setBounds(26, 83, 44, 16);
		panel_2.add(P2);

		scoreP2 = new JLabel("");
		scoreP2.setBounds(75, 83, 44, 16);
		panel_2.add(scoreP2);

		P3 = new JLabel("");
		P3.setBounds(26, 121, 44, 16);
		panel_2.add(P3);

		scoreP3 = new JLabel("");
		scoreP3.setBounds(75, 121, 44, 16);
		panel_2.add(scoreP3);

		P4 = new JLabel("");
		P4.setBounds(26, 160, 44, 16);
		panel_2.add(P4);

		scoreP4 = new JLabel("");
		scoreP4.setBounds(99, 160, 44, 16);
		panel_2.add(scoreP4);

		P5 = new JLabel("");
		P5.setBounds(26, 198, 44, 16);
		panel_2.add(P5);

		scoreP5 = new JLabel("");
		scoreP5.setBounds(99, 198, 44, 16);
		panel_2.add(scoreP5);

		P6 = new JLabel("");
		P6.setBounds(26, 239, 44, 16);
		panel_2.add(P6);

		scoreP6 = new JLabel("");
		scoreP6.setBounds(99, 239, 44, 16);
		panel_2.add(scoreP6);

		P7 = new JLabel("");
		P7.setBounds(26, 274, 44, 16);
		panel_2.add(P7);

		scoreP7 = new JLabel("");
		scoreP7.setBounds(75, 274, 44, 16);
		panel_2.add(scoreP7);

		labelPlayerName = new JLabel("");
		labelPlayerName.setBounds(26, 371, 134, 33);
		panel_2.add(labelPlayerName);

		lblTurn = new JLabel("");
		lblTurn.setBounds(26, 416, 134, 33);
		panel_2.add(lblTurn);
		
		voteP2 = new JLabel("");
		voteP2.setBounds(121, 83, 44, 16);
		panel_2.add(voteP2);
		
		voteP3 = new JLabel("");
		voteP3.setBounds(121, 121, 44, 16);
		panel_2.add(voteP3);
		
		voteP4 = new JLabel("");
		voteP4.setBounds(121, 160, 44, 16);
		panel_2.add(voteP4);
		
		voteP5 = new JLabel("");
		voteP5.setBounds(121, 198, 44, 16);
		panel_2.add(voteP5);
		
		voteP6 = new JLabel("");
		voteP6.setBounds(121, 239, 44, 16);
		panel_2.add(voteP6);
		
		voteP7 = new JLabel("");
		voteP7.setBounds(121, 274, 44, 16);
		panel_2.add(voteP7);

		JLabel lblNewLabel = new JLabel("Please place a character…");
		lblNewLabel.setForeground(Color.ORANGE);
		lblNewLabel.setFont(new Font("Skia", Font.PLAIN, 16));
		lblNewLabel.setBounds(16, 19, 500, 16);
		contentPane.add(lblNewLabel);

		JLabel logout = new JLabel("");

		logout.setIcon(new ImageIcon(ChessBoard.class.getResource("/img/logout.png")));
		logout.setBounds(653, 8, 61, 29);
		contentPane.add(logout);

		JButton btnPass = new JButton("Pass");
		btnPass.setFont(new Font("Monaco", Font.BOLD, 18));
		btnPass.setBounds(339, 634, 125, 35);
		contentPane.add(btnPass);

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

		// pass
		btnPass.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JSONObject sendMsg = new JSONObject();
				try {
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					sendMsg.put("method", "turn");
					sendMsg.put("word", "");
					// generated 2D json array
					JSONArray map = new JSONArray();
					for (int i = 0; i < 20; i++) {
						JSONArray cloumn = new JSONArray();
						for (int j = 0; j < 20; j++) {
							cloumn.put(squares[i][j].getText());
						}
						map.put(cloumn);
					}
					sendMsg.put("map", map);
					System.out.println("sent pass");
					output.write(sendMsg.toString() + "\n");
					output.flush();
				} catch (JSONException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		//Yes
		btnYes.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JSONObject sendMsg = new JSONObject();
				try {
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					sendMsg.put("method", "vote");
					sendMsg.put("value", 1);
					output.write(sendMsg.toString() + "\n");
					output.flush();
				} catch (JSONException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		//No
		btnNo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JSONObject sendMsg = new JSONObject();
				try {
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					sendMsg.put("method", "vote");
					sendMsg.put("value", 0);
					output.write(sendMsg.toString() + "\n");
					output.flush();
				} catch (JSONException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
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
				squares[i][j].setBorder(borderW);
			}
		}
		squares[row][col].setBorderPainted(true);
		squares[row][col].setBorder(borderB);

		// capture char input
		squares[row][col].addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (!squares[row][col].getText().equals(""))
					return;
				if (e.getKeyCode() >= 65 && e.getKeyCode() <= 90) {
					char input = e.getKeyChar();
					// System.out.println(e.getKeyChar());
					squares[row][col].setText(Character.toString(input));
				} else {
					JOptionPane.showMessageDialog(null, "Please input an alphabet", "Oops!",
							JOptionPane.WARNING_MESSAGE);
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				squares[row][col].setBorderPainted(true);
				if (e.getKeyCode() >= 37 && e.getKeyCode() <= 40) {
					// UP & DOWN
					if (e.getKeyCode() == 38 || e.getKeyCode() == 40) {
						word = checkVWord(row, col);
						voteWord.setText(word);
						voteWord.paintImmediately(voteWord.getVisibleRect());
					} else {
						// LEFT & RIGHT
						word = checkHWord(row, col);
						voteWord.setText(word);
						voteWord.paintImmediately(voteWord.getVisibleRect());
						// send word to server						
					}
					System.out.println("My selected word: " + word);
					JSONObject sendMsg = new JSONObject();
					try {
						Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
						sendMsg.put("method", "turn");
						sendMsg.put("word", word);
						// generated 2D json array
						JSONArray map = new JSONArray();
						for (int i = 0; i < 20; i++) {
							JSONArray cloumn = new JSONArray();
							for (int j = 0; j < 20; j++) {
								cloumn.put(squares[i][j].getText());
							}
							map.put(cloumn);
						}
						sendMsg.put("map", map);
						System.out.println(sendMsg.toString());
						output.write(sendMsg.toString() + "\n");
						output.flush();
					} catch (JSONException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

				
			}
		});

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
		
		for (int i = start; true ; i++) {
			if (!squares[i][col].getText().equals("")) {
				word = word + squares[i][col].getText();
				squares[i][col].setBorderPainted(true);
				squares[i][col].setBorder(borderB);
			}else {
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
			}else {
				break;
			}
		}
		System.out.println("horizontal: " + word);
		return word;
	}

	// display score
	public void setPlayerList() {
		labelPlayerName.setText("Welcome " + name);
		if (myTurn) {
			lblTurn.setText("Your turn.");
			lblTurn.paintImmediately(lblTurn.getVisibleRect());
		} else {
			lblTurn.setText("Other player's turn.");
			lblTurn.paintImmediately(lblTurn.getVisibleRect());
		}
		try {
			switch (players.size()) {
			case 7: {
				P7.setText(players.get(6).getObj().getString("name"));
				scoreP7.setText(Integer.toString(players.get(6).getObj().getInt("score")));
				scoreP7.paintImmediately(scoreP7.getVisibleRect());
				voteP7.setText(Integer.toString(players.get(6).getObj().getInt("vote")));
				voteP7.paintImmediately(voteP7.getVisibleRect());
				// I7.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(5).getObj().getString("path"))));
			}
			case 6: {
				P6.setText(players.get(5).getObj().getString("name"));
				scoreP6.setText(Integer.toString(players.get(5).getObj().getInt("score")));
				scoreP6.paintImmediately(scoreP6.getVisibleRect());
				voteP6.setText(Integer.toString(players.get(5).getObj().getInt("vote")));
				voteP6.paintImmediately(voteP6.getVisibleRect());
				// I6.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(4).getObj().getString("path"))));
			}
			case 5: {
				P5.setText(players.get(4).getObj().getString("name"));
				scoreP5.setText(Integer.toString(players.get(4).getObj().getInt("score")));
				scoreP5.paintImmediately(scoreP5.getVisibleRect());
				voteP5.setText(Integer.toString(players.get(4).getObj().getInt("vote")));
				voteP5.paintImmediately(voteP5.getVisibleRect());
				// I5.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(3).getObj().getString("path"))));
			}
			case 4: {
				P4.setText(players.get(3).getObj().getString("name"));
				scoreP4.setText(Integer.toString(players.get(3).getObj().getInt("score")));
				scoreP4.paintImmediately(scoreP4.getVisibleRect());
				voteP4.setText(Integer.toString(players.get(3).getObj().getInt("vote")));
				voteP4.paintImmediately(voteP4.getVisibleRect());
				// I4.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(2).getObj().getString("path"))));
			}
			case 3: {
				P3.setText(players.get(2).getObj().getString("name"));
				scoreP3.setText(Integer.toString(players.get(2).getObj().getInt("score")));
				scoreP3.paintImmediately(scoreP3.getVisibleRect());
				voteP3.setText(Integer.toString(players.get(2).getObj().getInt("vote")));
				voteP3.paintImmediately(voteP3.getVisibleRect());
				// I3.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(1).getObj().getString("path"))));
			}
			case 2: {
				P2.setText(players.get(1).getObj().getString("name"));
				scoreP2.setText(Integer.toString(players.get(1).getObj().getInt("score")));
				scoreP2.paintImmediately(scoreP2.getVisibleRect());
				voteP2.setText(Integer.toString(players.get(1).getObj().getInt("vote")));
				voteP2.paintImmediately(voteP2.getVisibleRect());
				// I2.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(0).getObj().getString("path"))));
			}
			case 1: {
				P1.setText(players.get(0).getObj().getString("name"));
				scoreP1.setText(Integer.toString(players.get(0).getObj().getInt("score")));
				scoreP1.paintImmediately(scoreP1.getVisibleRect());
				voteP1.setText(Integer.toString(players.get(0).getObj().getInt("vote")));
				voteP1.paintImmediately(voteP1.getVisibleRect());
				// I2.setIcon(new
				// ImageIcon(Room.class.getResource(temp.get(0).getObj().getString("path"))));
				break;
			}
			default:
				System.out.print("Wrong size" + players.size());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
						// update map
						System.out.println("Turn");
						JSONArray map = msg.getJSONArray("map");
						for (int i = 0; i < map.length(); i++) {
							JSONArray row = map.getJSONArray(i);
							for (int j = 0; j < row.length(); j++) {
								squares[i][j].setText(row.getString(j));
								squares[i][j].paintImmediately(squares[i][j].getVisibleRect());
							}
						}
						// update player
						JSONArray arr = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						for (int i = 0; i < arr.length(); i++) {
							Player player = new Player(arr.getJSONObject(i));
							players.add(player);
						}
						// is my turn
						JSONObject player = msg.getJSONObject("player");
						if (player.getString("name").equals(name)) {
							myTurn = true;
						} else {
							myTurn = false;
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
								//squares[i][j].paintImmediately(squares[i][j].getVisibleRect());
							}
						}
						// set the voting word
						voteWord.setText(msg.getString("word"));
						voteWord.paintImmediately(voteWord.getVisibleRect());
						// is my turn
		/*				JSONObject player = msg.getJSONObject("player");
						if (player.getString("name").equals(name)) {
							lblTurn.setText("Your turn.");
							lblTurn.paintImmediately(lblTurn.getVisibleRect());
							myTurn = true;
						} else {
							lblTurn.setText("Other player turns.");
							lblTurn.paintImmediately(lblTurn.getVisibleRect());
							myTurn = false;
						}*/
						break;
					}
					case "update vote": {
						System.out.println("update vote");
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
					}
				}
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
