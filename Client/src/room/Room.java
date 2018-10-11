package room;

import java.net.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;
import javax.swing.text.Position;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.Color;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import board.ChessBoard;
import utilities.MyArrayList;
import utilities.Player;

import com.jgoodies.forms.layout.FormSpecs;

public class Room extends JFrame {

	private JPanel contentPane;
	private static String name;
	private String path;
	private int xx, xy;
	private Socket client;
	private static MyArrayList<Player> potentialPlayers = new MyArrayList<Player>(); // not include self
	private static MyArrayList<Player> invitedPlayers = new MyArrayList<Player>();
	private static MyArrayList<Player> players = new MyArrayList<Player>();
	private static JPanel panelPlayer2, panelPlayer3, panelPlayer4, panelPlayer5, panelPlayer6, panelPlayer7, panel_2;
	private static JLabel profile, I2, I3, I4, I5, I6, I7;
	private static JLabel P2, P3, P4, P5, P6, P7;
	private JList<String> playerList;
	private BufferedReader input;

	public Room(String name, Socket client, String path) throws JSONException {
		this.client = client;
		this.name = name;
		this.path = path;

		// init listener
		guiInitialize();

		Listener listener = new Listener();
		listener.start();
	}

	// Room GUI init
	private void guiInitialize() throws JSONException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 713, 499);
		contentPane = new JPanel();
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				Room.this.setLocation(x - xx, y - xy);
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

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(71, 120, 197));
		panel_1.setBounds(0, 0, 719, 51);
		contentPane.add(panel_1);
		panel_1.setLayout(null);

		JLabel logout = new JLabel("");
		logout.setBounds(677, 14, 24, 24);
		panel_1.add(logout);
		logout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		logout.setIcon(new ImageIcon(Room.class.getResource("/img/logout.png")));

		JLabel lblScrabble = new JLabel("SCRABBLE");
		lblScrabble.setForeground(Color.WHITE);
		lblScrabble.setFont(new Font("Charlemagne Std", Font.BOLD, 16));
		lblScrabble.setBounds(18, 14, 121, 31);
		panel_1.add(lblScrabble);

		panel_2 = new JPanel();
		panel_2.setBackground(new Color(71, 120, 197));
		panel_2.setBounds(0, 40, 190, 439);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		playerList = new JList();
		playerList.setBackground(new Color(71, 120, 197));
		playerList.setForeground(Color.WHITE);
		playerList.setFont(new Font("Lithos Pro", Font.PLAIN, 15));
		playerList.setBounds(20, 222, 141, 132);
		panel_2.add(playerList);

		JPanel panel_3 = new JPanel();
		panel_3.setBackground(new Color(120, 168, 252));
		panel_3.setBounds(-6, 6, 195, 56);
		panel_2.add(panel_3);
		panel_3.setLayout(null);

		JLabel usernameLable = new JLabel("");
		usernameLable.setBounds(12, 20, 177, 19);
		panel_3.add(usernameLable);
		usernameLable.setFont(new Font("Lithos Pro", Font.PLAIN, 15));
		usernameLable.setForeground(Color.WHITE);
		usernameLable.setText("Welcome, " + name);

		JPanel panel_4 = new JPanel();
		panel_4.setBackground(new Color(84, 127, 206));
		panel_4.setBounds(0, 61, 189, 121);
		panel_2.add(panel_4);
		GridBagLayout gbl_panel_4 = new GridBagLayout();
		gbl_panel_4.columnWidths = new int[] { 94, 0, 94, 0 };
		gbl_panel_4.rowHeights = new int[] { 70, 0, 0, 0, 70, 0 };
		gbl_panel_4.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel_4.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panel_4.setLayout(gbl_panel_4);

		JLabel label = new JLabel("");
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 2;
		gbc_label.gridy = 0;
		panel_4.add(label, gbc_label);

		profile = new JLabel("");
		profile.setIcon(new ImageIcon(Room.class.getResource(path)));
		GridBagConstraints gbc_profile = new GridBagConstraints();
		gbc_profile.fill = GridBagConstraints.BOTH;
		gbc_profile.insets = new Insets(0, 0, 5, 5);
		gbc_profile.gridx = 1;
		gbc_profile.gridy = 2;
		panel_4.add(profile, gbc_profile);

		JLabel label_1 = new JLabel("");
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.fill = GridBagConstraints.BOTH;
		gbc_label_1.gridx = 2;
		gbc_label_1.gridy = 4;
		panel_4.add(label_1, gbc_label_1);

		JPanel poolPanel = new JPanel();
		poolPanel.setBackground(Color.WHITE);
		poolPanel.setBounds(186, 40, 529, 439);
		contentPane.add(poolPanel);
		poolPanel.setLayout(null);

		panelPlayer2 = new JPanel();
		panelPlayer2.setBackground(Color.WHITE);
		panelPlayer2.setBounds(40, 64, 112, 103);
		poolPanel.add(panelPlayer2);
		panelPlayer2.setLayout(null);
		panelPlayer2.setVisible(false);

		P2 = new JLabel("");
		P2.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		P2.setBounds(32, 81, 61, 16);
		panelPlayer2.add(P2);

		JCheckBox C1 = new JCheckBox("");
		C1.setBounds(0, 0, 28, 23);
		panelPlayer2.add(C1);

		I2 = new JLabel("");
		I2.setBounds(22, 6, 84, 81);
		panelPlayer2.add(I2);

		panelPlayer5 = new JPanel();
		panelPlayer5.setBackground(Color.WHITE);
		panelPlayer5.setBounds(40, 209, 112, 106);
		poolPanel.add(panelPlayer5);
		panelPlayer5.setLayout(null);
		panelPlayer5.setVisible(false);

		P5 = new JLabel("");
		P5.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		P5.setBounds(32, 81, 61, 16);
		panelPlayer5.add(P5);

		JCheckBox C4 = new JCheckBox("");
		C4.setBounds(0, 0, 28, 23);
		panelPlayer5.add(C4);

		I5 = new JLabel("");
		I5.setBounds(22, 6, 84, 81);
		panelPlayer5.add(I5);

		panelPlayer6 = new JPanel();
		panelPlayer6.setBackground(Color.WHITE);
		panelPlayer6.setBounds(210, 209, 112, 106);
		poolPanel.add(panelPlayer6);
		panelPlayer6.setLayout(null);
		panelPlayer6.setVisible(false);

		P6 = new JLabel("");
		P6.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		P6.setBounds(20, 81, 61, 16);
		panelPlayer6.add(P6);

		JCheckBox C5 = new JCheckBox("");
		C5.setBounds(0, 2, 28, 23);
		panelPlayer6.add(C5);

		I6 = new JLabel("");
		I6.setBounds(20, 6, 84, 85);
		panelPlayer6.add(I6);

		panelPlayer7 = new JPanel();
		panelPlayer7.setBackground(Color.WHITE);
		panelPlayer7.setBounds(375, 209, 112, 106);
		poolPanel.add(panelPlayer7);
		panelPlayer7.setLayout(null);
		panelPlayer7.setVisible(false);

		P7 = new JLabel("");
		P7.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		P7.setBounds(21, 81, 61, 16);
		panelPlayer7.add(P7);

		JCheckBox C6 = new JCheckBox("");
		C6.setBounds(0, 0, 28, 23);
		panelPlayer7.add(C6);

		I7 = new JLabel("");
		I7.setBounds(21, 6, 84, 81);
		panelPlayer7.add(I7);

		panelPlayer4 = new JPanel();
		panelPlayer4.setBackground(Color.WHITE);
		panelPlayer4.setBounds(375, 64, 112, 107);
		poolPanel.add(panelPlayer4);
		panelPlayer4.setLayout(null);
		panelPlayer4.setVisible(false);

		P4 = new JLabel("");
		P4.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		P4.setBounds(21, 81, 61, 16);
		panelPlayer4.add(P4);

		JCheckBox C3 = new JCheckBox("");
		C3.setBounds(0, 0, 28, 23);
		panelPlayer4.add(C3);

		I4 = new JLabel("");
		I4.setBounds(21, 6, 84, 81);
		panelPlayer4.add(I4);

		panelPlayer3 = new JPanel();
		panelPlayer3.setBackground(Color.WHITE);
		panelPlayer3.setBounds(210, 64, 112, 106);
		poolPanel.add(panelPlayer3);
		panelPlayer3.setLayout(null);
		panelPlayer3.setVisible(false);

		P3 = new JLabel("");
		P3.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		P3.setBounds(18, 81, 61, 16);
		panelPlayer3.add(P3);

		JCheckBox C2 = new JCheckBox("");
		C2.setBounds(0, 0, 28, 23);
		panelPlayer3.add(C2);

		I3 = new JLabel("");
		I3.setBounds(22, 6, 84, 81);
		panelPlayer3.add(I3);

		// Start the game, only the room host is able to.
		// JButton btnNewButton = new JButton("Scrabble");
		// btnNewButton.addActionListener(new ActionListener() {
		// public void actionPerformed(ActionEvent e) {
		// if (players.size() > 1) {
		// JSONObject sendMsg = new JSONObject();
		// try {
		// Writer output = new OutputStreamWriter(client.getOutputStream(),
		// "UTF-8");
		// sendMsg.put("method", "start");
		// output.write(sendMsg.toString() + "\n");
		// output.flush();
		// //System.exit(0);
		// } catch (JSONException | IOException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		// } else
		// JOptionPane.showMessageDialog(null, "There are no enough player!",
		// "Notice",
		// JOptionPane.ERROR_MESSAGE);
		// }
		// });
		// btnNewButton.setBounds(39, 209, 117, 29);
		// panel_2.add(btnNewButton);

		
		JLabel lblRoomPlayerList = new JLabel("Players in Room: ");
		lblRoomPlayerList.setFont(new Font("Lithos Pro", Font.PLAIN, 16));
		lblRoomPlayerList.setForeground(Color.WHITE);
		lblRoomPlayerList.setBounds(10, 194, 157, 16);
		panel_2.add(lblRoomPlayerList);

		JButton btnScrabble = new JButton("Scrabble");
		btnScrabble.setBounds(116, 392, 117, 29);
		poolPanel.add(btnScrabble);

		JButton btnInvite = new JButton("Invite");
		btnInvite.setBounds(246, 392, 117, 29);
		poolPanel.add(btnInvite);

		JButton btnLeave = new JButton("Leave Room");
		btnLeave.setBounds(375, 392, 117, 29);
		poolPanel.add(btnLeave);

		btnScrabble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (playerList.getModel().getSize() != 0) {				
					JSONObject reply = new JSONObject();
					try {
						reply.put("method", "start game");
						Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
						output.write(reply + "\n");
						output.flush();
					} catch (JSONException | IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}else{
					JOptionPane.showMessageDialog(null, "Sorry..there is no player in the room..", "Join the room!",
							JOptionPane.WARNING_MESSAGE);
				}
				
			}
		});
		
		btnInvite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JSONObject sendMsg = new JSONObject();
				MyArrayList<Player> temp = new MyArrayList<Player>();
				try {
					Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
					if (playerList.getModel().getSize() > 0) {
						if (C1.isSelected()) {
							int index = playerList.getNextMatch(potentialPlayers.get(0).getName(),0,Position.Bias.Forward);
							if (index == -1){
								temp.add(potentialPlayers.get(0));
							}else{
								JOptionPane.showMessageDialog(null, "This player already in the room!", "Check the room player list!",
										JOptionPane.WARNING_MESSAGE);
							}
							
						}
						if (C2.isSelected()) {
							int index = playerList.getNextMatch(potentialPlayers.get(1).getName(),0,Position.Bias.Forward);
							if (index == -1){
								temp.add(potentialPlayers.get(1));
							}else{
								JOptionPane.showMessageDialog(null, "This player already in the room!", "Check the room player list!",
										JOptionPane.WARNING_MESSAGE);
							}
						}
						if (C3.isSelected()) {
							int index = playerList.getNextMatch(potentialPlayers.get(2).getName(),0,Position.Bias.Forward);
							if (index == -1){
								temp.add(potentialPlayers.get(2));
							}else{
								JOptionPane.showMessageDialog(null, "This player already in the room!", "Check the room player list!",
										JOptionPane.WARNING_MESSAGE);
							}
						}
						if (C4.isSelected()) {
							int index = playerList.getNextMatch(potentialPlayers.get(3).getName(),0,Position.Bias.Forward);
							if (index == -1){
								temp.add(potentialPlayers.get(3));
							}else{
								JOptionPane.showMessageDialog(null, "This player already in the room!", "Check the room player list!",
										JOptionPane.WARNING_MESSAGE);
							}
						}
						if (C5.isSelected()) {
							int index = playerList.getNextMatch(potentialPlayers.get(4).getName(),0,Position.Bias.Forward);
							if (index == -1){
								temp.add(potentialPlayers.get(4));
							}else{
								JOptionPane.showMessageDialog(null, "This player already in the room!", "Check the room player list!",
										JOptionPane.WARNING_MESSAGE);
							}
						}
						if (C6.isSelected()) {
							int index = playerList.getNextMatch(potentialPlayers.get(5).getName(),0,Position.Bias.Forward);
							if (index == -1){
								temp.add(potentialPlayers.get(5));
							}else{
								JOptionPane.showMessageDialog(null, "This player already in the room!", "Check the room player list!",
										JOptionPane.WARNING_MESSAGE);
							}
						}
						sendMsg.put("method", "request invite");
						sendMsg.put("players", temp.toJson());
						output.write(sendMsg.toString() + "\n");
						output.flush();
					}else{
						JOptionPane.showMessageDialog(null, "Sorry..there is no player in the room..", "Join the room!",
								JOptionPane.WARNING_MESSAGE);
					}				
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public static void updatePotentialPlayersNumber(MyArrayList<Player> temp) {
		panelPlayer2.setVisible(false);
		panelPlayer3.setVisible(false);
		panelPlayer4.setVisible(false);
		panelPlayer5.setVisible(false);
		panelPlayer6.setVisible(false);
		panelPlayer7.setVisible(false);
		try {
			switch (temp.size()) {
			case 6: {
				panelPlayer7.setVisible(true);
				P7.setText(temp.get(5).getObj().getString("name"));
				I7.setIcon(new ImageIcon(Room.class.getResource(temp.get(5).getObj().getString("path"))));
			}
			case 5: {
				panelPlayer6.setVisible(true);
				P6.setText(temp.get(4).getObj().getString("name"));
				I6.setIcon(new ImageIcon(Room.class.getResource(temp.get(4).getObj().getString("path"))));
			}
			case 4: {
				panelPlayer5.setVisible(true);
				P5.setText(temp.get(3).getObj().getString("name"));
				I5.setIcon(new ImageIcon(Room.class.getResource(temp.get(3).getObj().getString("path"))));
			}
			case 3: {
				panelPlayer4.setVisible(true);
				P4.setText(temp.get(2).getObj().getString("name"));
				I4.setIcon(new ImageIcon(Room.class.getResource(temp.get(2).getObj().getString("path"))));
			}
			case 2: {
				panelPlayer3.setVisible(true);
				P3.setText(temp.get(1).getObj().getString("name"));
				I3.setIcon(new ImageIcon(Room.class.getResource(temp.get(1).getObj().getString("path"))));
			}
			case 1: {
				panelPlayer2.setVisible(true);
				P2.setText(temp.get(0).getObj().getString("name"));
				I2.setIcon(new ImageIcon(Room.class.getResource(temp.get(0).getObj().getString("path"))));
				break;
			}
			default:
				System.out.println("Player size: " + potentialPlayers.size());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private static int showMsg(String invitor) {
		String[] buttons = { "No", "YES" };
		// response =0 is no, =1 is yes
		int response = 0;
		String mgs = "Hi," + name + ", a invite message from" + invitor;
		response = JOptionPane.showOptionDialog(null, "Do you want to play a game?", mgs, JOptionPane.DEFAULT_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[1]);
		return response;
	}

	// listener subclass \ thread
	public class Listener extends Thread {

		@Override
		public void run() {
			String msgStr = null;

			try {
				Writer output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
				input = new BufferedReader(new InputStreamReader(client.getInputStream(), "UTF-8"));
				while ((msgStr = input.readLine()) != null) {
					JSONObject msg = new JSONObject(msgStr);
					switch (msg.getString("method")) {
					case "join game": {
						JSONArray arr = msg.getJSONArray("potentialPlayers");
						potentialPlayers = new MyArrayList<Player>();
						for (int i = 0; i < arr.length(); i++) {
							String getName = arr.getJSONObject(i).getString("name");
							String path = arr.getJSONObject(i).getString("path");
							Player player = new Player(getName, path);
							if (!getName.equals(name)) {
								potentialPlayers.add(player);
							}
						}
						JSONArray arr2 = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						if (arr2.length() > 0) {
							String[] model = new String[10];
							for (int i = 0; i < arr2.length(); i++) {
								Player player = new Player(arr2.getJSONObject(i));
								players.add(player);
								model[i] = player.getName();
							}
							playerList.setListData(model);
						}
						Room.updatePotentialPlayersNumber(potentialPlayers);
						break;
					}
					case "request invite": {
						String invitor = msg.getString("from");
						if (!invitor.equals(name)) {
							int choose = showMsg(invitor);
							JSONObject reply = new JSONObject();
							reply.put("method", "response invite");
							reply.put("value", choose);
							output.write(reply + "\n");
							output.flush();
						}
						break;
					}
					case "response invite": {
						JSONArray arr = msg.getJSONArray("players");
						players = new MyArrayList<Player>();
						String[] model = new String[10];
						for (int i = 0; i < arr.length(); i++) {
							Player player = new Player(arr.getJSONObject(i));
							players.add(player);
							model[i] = player.getName();
						}
						playerList.setListData(model);
						break;
					}
					case "turn": {
						System.out.println("chessboard");
						// is my turn
						boolean myTurn = false;
						JSONObject player = msg.getJSONObject("player");
						if (player.getString("name").equals(name)) {
							myTurn = true;
						} else {
							myTurn = false;
						}
						instrcution frame = new instrcution();
						frame.setVisible(true);
						frame.setTitle("Instructions");	
						ChessBoard game = new ChessBoard(name, msg, client, myTurn);
						System.out.println(msg);
						game.setUndecorated(true);
						game.setVisible(true);
						game.setPlayerList();
						contentPane.setVisible(false);
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