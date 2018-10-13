package board;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.Socket;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import room.Instrcution;
import utilities.MyArrayList;
import utilities.Player;

import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.JButton;
import java.awt.Font;

public class GameOver extends JFrame {

	private JPanel contentPane;
	private Socket client;
	private String name;
	private int xx, xy;
	
	private MyArrayList<Player> players;
	private static JLabel P1, P2, P3, P4, scoreP1, scoreP2, scoreP3, scoreP4;
	
	private JLabel labelPlayerName;
	private JLabel lblImg1, lblImg2, lblImg3, lblImg4;
	private JLabel winning;
	private JLabel winnerP1, winnerP2, winnerP3, winnerP4;
	
	public GameOver(String name, MyArrayList<Player> players, Socket client) {
		
		this.name = name;
		this.client = client;

		guiInitialize(name, players);
	}
	
	private void guiInitialize(String name, MyArrayList<Player> players) {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 360, 784);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				GameOver.this.setLocation(x - xx, y - xy);
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
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setIcon(new ImageIcon(Instrcution.class.getResource("/img/gameover.png")));
		lblNewLabel.setBounds(117, 0, 128, 146);
		contentPane.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setBounds(0, 226, 360, 430);
		contentPane.add(panel);
		panel.setLayout(null);
		
		lblImg1 = new JLabel("");
		lblImg1.setBounds(56, 46, 69, 69);
		panel.add(lblImg1);
		
		lblImg2 = new JLabel("");
		lblImg2.setBounds(228, 46, 69, 69);
		panel.add(lblImg2);
		
		lblImg3 = new JLabel("");
		lblImg3.setBounds(56, 292, 69, 69);
		panel.add(lblImg3);
		
		lblImg4 = new JLabel("");
		lblImg4.setBounds(228, 292, 69, 69);
		panel.add(lblImg4);
		
		P1 = new JLabel("");
		P1.setBounds(56, 7, 108, 27);
		panel.add(P1);
		
		P2 = new JLabel("");
		P2.setBounds(228, 7, 108, 27);
		panel.add(P2);
		
		P3 = new JLabel("");
		P3.setBounds(56, 253, 108, 27);
		panel.add(P3);
		
		P4 = new JLabel("");
		P4.setBounds(228, 253, 108, 27);
		panel.add(P4);
		
		scoreP1 = new JLabel("");
		scoreP1.setBounds(56, 127, 61, 16);
		panel.add(scoreP1);
		
		scoreP2 = new JLabel("");
		scoreP2.setBounds(228, 127, 61, 16);
		panel.add(scoreP2);
		
		scoreP3 = new JLabel("");
		scoreP3.setBounds(56, 373, 61, 16);
		panel.add(scoreP3);
		
		scoreP4 = new JLabel("");
		scoreP4.setBounds(228, 373, 61, 16);
		panel.add(scoreP4);
		
		displayWinner(panel, players, name);
		
		setPlayerList(players);
		
		JButton btnOk = new JButton("OK");
		btnOk.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		btnOk.setBounds(117, 680, 117, 54);
		contentPane.add(btnOk);
	}
	

	public void setPlayerList(MyArrayList<Player> players) {
		// display name, icon and score
		switch (players.size()) {
			case 4: {
				P4.setText(players.get(3).getName());
				scoreP4.setText("Score: " + Integer.toString(players.get(3).getScore()));
				lblImg4.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				lblImg4.setIcon(new ImageIcon(GameOver.class.getResource(players.get(3).getPath())));
			}
			case 3: {
				P3.setText(players.get(2).getName());
				scoreP3.setText("Score: " + Integer.toString(players.get(2).getScore()));
				lblImg3.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				lblImg3.setIcon(new ImageIcon(GameOver.class.getResource(players.get(2).getPath())));
			}
			case 2: {
				P2.setText(players.get(1).getName());
				scoreP2.setText("Score: " + Integer.toString(players.get(1).getScore()));
				lblImg2.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				lblImg2.setIcon(new ImageIcon(GameOver.class.getResource(players.get(1).getPath())));
			}
			case 1: {
				P1.setText(players.get(0).getName());
				scoreP1.setText("Score: " + Integer.toString(players.get(0).getScore()));
				lblImg1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				lblImg1.setIcon(new ImageIcon(GameOver.class.getResource(players.get(0).getPath())));
				break;
			}
			default: {
				System.out.print("Wrong size" + players.size());
			}
		}
	}
	
	//display winner
	public void displayWinner(JPanel panel, MyArrayList<Player> players, String name) {
		int maxScore = 0;
		// find the max score
		switch (players.size()) {
			case 4: {
				maxScore = players.get(3).getScore();
			}
			case 3: {
				maxScore = Math.max(maxScore,players.get(2).getScore());
			}
			case 2: {
				maxScore = Math.max(maxScore,players.get(1).getScore());
			}
			case 1: {
				maxScore = Math.max(maxScore,players.get(0).getScore());
				break;
			}
			default: {
				System.out.print("Wrong size" + players.size());
			}
		}
		
		winning = new JLabel("");
		winning.setBounds(147, 140, 64, 72);
		winning.setIcon(new ImageIcon(Instrcution.class.getResource("/img/win.png")));
		contentPane.add(winning);
		
		//display winner text
		switch (players.size()) {
			case 4: {
				if (players.get(3).getScore() == maxScore) {
					winnerP4 = new JLabel("Winner");
					winnerP4.setForeground(Color.ORANGE);
					winnerP4.setBounds(228, 401, 61, 16);
					panel.add(winnerP4);
				}
			}
			case 3: {
				if (players.get(2).getScore() == maxScore) {
					winnerP3 = new JLabel("Winner");
					winnerP3.setForeground(Color.ORANGE);
					winnerP3.setBounds(56, 401, 61, 16);
					panel.add(winnerP3);
				}
			}
			case 2: {
				if (players.get(1).getScore() == maxScore) {
					winnerP2 = new JLabel("Winner");
					winnerP2.setForeground(Color.ORANGE);
					winnerP2.setBounds(228, 155, 61, 16);
					panel.add(winnerP2);
				}
			}
			case 1: {
				if (players.get(0).getScore() == maxScore) {
					winnerP1 = new JLabel("Winner");
					winnerP1.setForeground(Color.ORANGE);
					winnerP1.setBounds(56, 155, 61, 16);
					panel.add(winnerP1);	
				}
				break;
			}
			default: {
				System.out.print("Wrong size" + players.size());
			}
		}
	}
}
