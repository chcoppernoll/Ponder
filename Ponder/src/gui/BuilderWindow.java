package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import net.miginfocom.swing.MigLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class BuilderWindow {

	private JFrame frame = new JFrame();
	private JPanel[] players = new JPanel[4];
	private JPanel gameHeader = new JPanel();
	
	private JButton[][] cells = new JButton[9][9];
	
	// TODO Alex
	/*
	 * Get Spawn Area to stack from the bottom for players 2 and 3
	 * Settings and GameList buttons pop up new jPanels over the game board and remove listeners for other objects
	 * Get a JAR Builder
	 * Make JAR out of Artwork
	 * Add JAR to classpath
	 */
	
	// TODO Grayson
	/*
	 * Work on artwork
	 * Work on the api
	 * Move elements into position
	 */
	
	// TODO API
	/*
	 * Be able to click and move pieces
	 * Be able to remove from the spawn area to the board
	 * Need to add a call to clear RefreshQueue
	 * Be able to chain moves
	 * Be able to get a cell from the mouse
	 * Translate MousePosition to Grid coordinate
	 */	
	
	private boolean outOfRange(int p) {
		return p < 1 || p > 9;
	}
	
	private boolean invalidPoint(int x, int y) {
		return outOfRange(x) || outOfRange(y);
	}

	/**
	 * Create the application.
	 */
	public BuilderWindow() {
		initialize();
	}
	
	public void reset() {
		initialize();
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BuilderWindow window = new BuilderWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame.setResizable(false);
		frame.setBounds(100, 100, 810, 640);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		// Settings button
		JButton btnSettings = new JButton("Settings");
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("Clicked Settings!");
			}
		});
		btnSettings.setBounds(20, 30, 110, 25);
		frame.getContentPane().add(btnSettings);
		
		// Game List button
		JButton btnGameList = new JButton("Game List");
		btnGameList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("Clicked Game List!");
			}
		});
		btnGameList.setBounds(670, 30, 110, 25);
		frame.getContentPane().add(btnGameList);
		
		for (int i = 0; i != players.length; ++i) {
			players[i] = new JPanel();
			players[i].setBounds(i < 2 ? 41 : 670, i % 2 == 0 ? 84 : 363, 89, 227);
			players[i].setLayout(null);
			
			// Add player label
			JTextPane player_name = new JTextPane();
			player_name.setBounds(i < 2 ? 7 : 0, i % 2 == 0 ? 0 : 198, 82, 23);
			player_name.setText("Player " + (i + 1));
			// Need a way of not having the background color
			players[i].add(player_name);
			
			// Add flag control icons
			for (int j = 0; j != 4; ++j) {
				JLabel flag_icon = new JLabel("");
				
				if (i < 2)
					flag_icon.setBounds(69 - 22 * j, i % 2 == 0 ? 34 : 173, 20, 20);
				else
					flag_icon.setBounds(0 + 22 * j, i % 2 == 0 ? 34 : 173, 20, 20);
				
				flag_icon.setIcon(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
				
				players[i].add(flag_icon);
			}
			
			// Add Spawn Stack
			// Waiting for work on spawn stack
			
			frame.getContentPane().add(players[i]);
		}
		
		// For Stack Testing
		JPanel stack_test = new JPanel();
		stack_test.setBounds(31, 215, 85, 138);
		frame.getContentPane().add(stack_test);
		
		JButton btnNewButton_1 = new JButton("New button");
		
		JButton btnNewButton = new JButton("New button");
		stack_test.setLayout(new BoxLayout(stack_test, BoxLayout.X_AXIS));
		stack_test.add(btnNewButton_1);
		stack_test.add(btnNewButton);

		// Player "Spawn" stack
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(34, 65, 55, 162);
		players[0].add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		JPanel panel_2 = new JPanel();
		panel_2.setBounds(34, 0, 55, 162);
		players[1].add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 65, 55, 162);
		players[2].add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel_4 = new JPanel();
		panel_4.setBounds(0, 0, 55, 162);
		players[3].add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		
		// Game Data Text
		gameHeader.setBounds(150, 11, 500, 62);
		frame.getContentPane().add(gameHeader);
		gameHeader.setLayout(null);
		
		JTextPane txtpnGameDataGoes = new JTextPane();
		txtpnGameDataGoes.setText("Game Data Goes Here");
		txtpnGameDataGoes.setBounds(149, 31, 209, 20);
		gameHeader.add(txtpnGameDataGoes);
		
		
		// Game Grid
		JPanel grid = new JPanel();
		grid.setBackground(Color.BLACK);
		grid.setBounds(150, 84, 500, 500);
		frame.getContentPane().add(grid);
		grid.setLayout(new GridLayout(9, 9, 1, 1));
		
		for (int x = 0; x != cells.length; ++x)
			for (int y = 0; y != cells.length; ++y) {
				cells[x][y] = new JButton(x + ", " + y);
				cells[x][y].setBackground(Color.BLACK);
				cells[x][y].addMouseListener(new MouseAdapter() {
					private boolean clicked = false;
					
					@Override
					public void mouseEntered(MouseEvent e) {
						((JButton)e.getSource()).setBackground(Color.CYAN);
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						if (!clicked)
							((JButton)e.getSource()).setBackground(Color.BLACK);
					}
					
					public void mouseClicked(MouseEvent e) {						
						//add to Queue
						((JButton)e.getSource()).setBackground(Color.GREEN);
					}
					
					public void mousePressed(MouseEvent e) {
						clicked = true;
						((JButton)e.getSource()).setBackground(Color.RED);
					}
					
					public void mouseReleased(MouseEvent e) {
						clicked = false;
						mouseExited(e);
					}
				});
				
				grid.add(cells[x][y]);
			}
			
		// Testing piece display
		setUpPieces(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif")));
		setUpFlags(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png")));
	}
	
	private void setUpPieces(ImageIcon icon) {
		newPiece(cells[1][2], icon);
		newPiece(cells[1][3], icon);
		newPiece(cells[2][1], icon);
		newPiece(cells[3][1], icon);
		
		newPiece(cells[5][1], icon);
		newPiece(cells[6][1], icon);
		newPiece(cells[7][2], icon);
		newPiece(cells[7][3], icon);
		
		newPiece(cells[1][5], icon);
		newPiece(cells[1][6], icon);
		newPiece(cells[2][7], icon);
		newPiece(cells[3][7], icon);

		newPiece(cells[5][7], icon);
		newPiece(cells[6][7], icon);
		newPiece(cells[7][6], icon);
		newPiece(cells[7][5], icon);
	}
	
	private void newPiece(JButton block, ImageIcon icon) {
		block.setText("");
		block.setIcon(icon);
	}
	
	private void setUpFlags(ImageIcon icon) {
		newPiece(cells[1][1], icon);
		newPiece(cells[7][1], icon);
		newPiece(cells[1][7], icon);
		newPiece(cells[7][7], icon);
	}
	
	public JTextPane getLabel(int player) {
		return (JTextPane)players[player].getComponents()[0];
	}
	
	public JLabel[] getFlagIcons(int player) {
		JLabel[] ret = new JLabel[4];
		
		Component[] subs = players[player].getComponents();
		for (int i = 0; i != ret.length; ++i)
			ret[i] = (JLabel)subs[i + 1];
		
		return ret;
	}
	
	public JPanel getStack(int player) {
		//return (JPanel)players[player].getComponents()[5];
		return null;
	}
	
	JButton getBlock(int x, int y) {
		return !invalidPoint(x, y) ? cells[x][y] : null;
	}
	
	/*
	 * The first column is for slide adjacent tiles
	 * The second column is for jump adjacent tiles
	 * 
	 * The index of the columns correspond to this chart
	 * 0 3 5 
	 * 1 X 6
	 * 2 4 7
	 */
	public JButton[][] getAdjacent(int x, int y) {
		if (invalidPoint(x, y)) return null;
		
		JButton[][] ret = new JButton[2][8];
		int count = 0;
		
		for (int dx = -1; dx != 2; ++dx) {
			for (int dy = -1; dy != 2; ++dy) {
				if (dy != 0 && dx != 0) {
					ret[0][count] = getBlock(x + dx, y + dy);
					ret[1][count++] = getBlock(x + 2 * dx, y + 2 * dy);
				}
			}
		}
		
		return ret;
	}
}
