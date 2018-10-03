package players;

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
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoard extends JFrame {

	private JPanel contentPane;
	private Socket client;
	private static ArrayList<PlayerC> players;
	private JButton[][] squares = new JButton[20][20];
	Border borderW = BorderFactory.createLineBorder(Color.WHITE,1);
	Border borderB = BorderFactory.createLineBorder(Color.BLACK,1);
	
	String votingWord = "";


	/**
	 * Create the frame.
	 */
	public ChessBoard(ArrayList<PlayerC> players,Socket client) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 720, 720);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		JPanel panel = new JPanel();
		panel.setBounds(6, 36, 500, 500);
		
		ButtonHandler buttonHandler = new ButtonHandler();
		
		panel.setLayout((new GridLayout(20,20)));
		for (int i = 0; i<20; i++)
		{
			for (int j=0; j<20; j++)
			{
				squares[i][j] = new JButton();
				squares[i][j].setBackground(new Color(242, 229, 210));
				squares[i][j].setOpaque(true);
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(borderW);
				
				panel.add(squares[i][j]);
				squares[i][j].addActionListener(buttonHandler);
			}
		}
		
		setResizable(false);
		setLocationRelativeTo(null);
		panel.setVisible(true);
		
		contentPane.add(panel);
		
		JLabel lblPleaseVote = new JLabel("Do you think string in black box is a word? Please vote... ");
		lblPleaseVote.setForeground(new Color(0, 102, 255));
		lblPleaseVote.setFont(new Font("Skia", Font.PLAIN, 16));
		lblPleaseVote.setBounds(16, 548, 490, 16);
		contentPane.add(lblPleaseVote);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		panel_1.setBounds(6, 576, 500, 116);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JButton btnNewButton = new JButton("Yes!");
		btnNewButton.setFont(new Font("Monaco", Font.BOLD, 20));
		btnNewButton.setBounds(6, 41, 212, 69);
		panel_1.add(btnNewButton);
		
		JButton btnNo = new JButton("No..");
		btnNo.setFont(new Font("Monaco", Font.BOLD, 20));
		btnNo.setBounds(255, 41, 212, 69);
		panel_1.add(btnNo);
		
		//the label showing the voting result
		JLabel lblVResult = new JLabel();
		lblVResult.setBounds(518, 576, 196, 116);
		contentPane.add(lblVResult);
		
		//if voting result is no
		ImageIcon icon = new ImageIcon(ChessBoard.class.getResource("/players/img/sad.png"));
		Image image = icon.getImage();
		Image newimg = image.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg));
		
		//if voting result is yes
		ImageIcon icon2 = new ImageIcon(ChessBoard.class.getResource("/players/img/smile.png"));
		Image image2 = icon2.getImage();
		Image newimg2 = image2.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg2));
		
		//when no result need to be displayed
		ImageIcon icon3 = new ImageIcon(ChessBoard.class.getResource("/players/img/question.png"));
		Image image3 = icon3.getImage();
		Image newimg3 = image3.getScaledInstance(110, 110, java.awt.Image.SCALE_SMOOTH);
		lblVResult.setIcon(new ImageIcon(newimg3));
		
		JLabel lblVotingResult = new JLabel("Voting Result:");
		lblVotingResult.setForeground(new Color(255, 153, 204));
		lblVotingResult.setFont(new Font("Apple Braille", Font.BOLD, 18));
		lblVotingResult.setBounds(518, 538, 196, 35);
		contentPane.add(lblVotingResult);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(204, 255, 255));
		panel_2.setBounds(518, 36, 185, 500);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel lblPlayers = new JLabel("Players");
		lblPlayers.setFont(new Font("Muna", Font.BOLD, 16));
		lblPlayers.setBounds(6, 6, 173, 24);
		panel_2.add(lblPlayers);
		
		JLabel lblNewLabel = new JLabel("Please place a character…");
		lblNewLabel.setForeground(new Color(0, 153, 255));
		lblNewLabel.setFont(new Font("Skia", Font.ITALIC, 16));
		lblNewLabel.setBounds(6, 8, 500, 16);
		contentPane.add(lblNewLabel);
		
		JLabel logout = new JLabel("");
		logout.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
			}
		});
		
		logout.setIcon(new ImageIcon(ChessBoard.class.getResource("/players/img/logout.png")));
		logout.setBounds(653, 8, 61, 29);
		contentPane.add(logout);
	}
	
	private class ButtonHandler implements ActionListener
	{
		public void actionPerformed(ActionEvent e) {
			Object source = e.getSource();
			
			for (int i = 0; i<20; i++){
				for (int j=0; j<20; j++){
					if (source == squares[i][j]){
						processClick(i,j);
						return;
					}
				}
			}
		}
	}
	
	
	
	//is valid if no character placed before
	private boolean isValidMove(int i, int j)
	{
		if (squares[i][j].getText().equals(""))  
			return true;
		else
			return false;
	}
	
	//the effect of border and show character
	private void processClick(int row, int col)
	{
		if (isValidMove(row, col) == false)
		{
			return;
		}
		for (int i = 0; i<20; i++){
			for (int j=0; j<20; j++){
				squares[i][j].setBorderPainted(true);
				squares[i][j].setBorder(borderW);
			}
		}
		squares[row][col].setBorderPainted(true);
		squares[row][col].setBorder(borderB);
		
		squares[row][col].addKeyListener(new KeyListener(){
			@Override
            public void keyPressed(KeyEvent e) {
				if (!squares[row][col].getText().equals("")) return;
                if(e.getKeyCode() >= 65 && e.getKeyCode() <= 90){
					char input = e.getKeyChar();
					System.out.println(e.getKeyChar());
					squares[row][col].setText(Character.toString(input));
                }
                else {
                	JOptionPane.showMessageDialog(null,"Please input an alphabet", "Oops!", JOptionPane.WARNING_MESSAGE);
                }
            }
			@Override
            public void keyTyped(KeyEvent e) {
				
			}
			@Override
            public void keyReleased(KeyEvent e) {
				squares[row][col].setBorderPainted(true);
				if(e.getKeyCode()>=37 && e.getKeyCode()<=40)
				{
					if (e.getKeyCode()==38 || e.getKeyCode()==40)
					votingWord = checkVWord(row,col);
					else votingWord = checkHWord(row,col);
				}
			}
			
			
		});
		
	}
			
		
	//get the vertical word
	protected String checkVWord(int row, int col) {
		String vWord = "";
		int start_r = 0;
		int end_r = 0;
		
		for (int i = row; i>0; i--)
		{
			if (squares[i][col].getText().equals("")) {
				start_r = i;
				break;
			}
		}
		for (int i = row; i<20; i++)
		{
			if (squares[i][col].getText().equals("")) {
				end_r = i;
				break;
			}
		}
		for (int i = start_r; i<=end_r; i++)
		{
			if (!squares[i][col].getText().equals("")) {
				vWord = vWord + squares[i][col].getText();
				squares[i][col].setBorderPainted(true);
				squares[i][col].setBorder(borderB);
			}
		}
		System.out.println(vWord);
		return vWord;
	}
	
	//get the horizontal word
	protected String checkHWord(int row, int col) {
		String hWord = "";
		int start_c = 0;
		int end_c = 0;
		for (int j=col; j>0; j--)
		{
			if (squares[row][j].getText().equals("")) {
				start_c = j;
				break;
			}
		}
		for (int j=col; j<20; j++)
		{
			if (squares[row][j].getText().equals("")) {
				end_c = j;
				break;
			}
		}
		for (int j = start_c; j<=end_c; j++)
		{
			if (!squares[row][j].getText().equals("")) {
				hWord = hWord + squares[row][j].getText();
				squares[row][j].setBorderPainted(true);
				squares[row][j].setBorder(borderB);
			}
		}
		System.out.println(hWord);
		return hWord;
	}

	protected static void showMessageDialog(Object object, String string, String string2, int warningMessage) {
		// TODO Auto-generated method stub
		
	}
}