package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextPane;

import game.GameLogic;
import game.GraphicsLogic;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.SwingConstants;


public class BuilderWindow {

	private JFrame frame = new JFrame();
	private JPanel[] players = new JPanel[4];
	private ImageIcon[][] theme = new ImageIcon[4][2];
	private JPanel gameHeader = new JPanel();
	
	private JButton[][] cells = new JButton[9][9];
	
	private GraphicsLogic logic = new GraphicsLogic();
	
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
	 */
	
	// PROBLEM
	// How to represent Flags and pieces on the same tile

	/**
	 * Create the application.
	 */
	public BuilderWindow() {
		ImageIcon piece = new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Error.gif"));
		ImageIcon flag = new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/JavaCup32.png"));
		theme[0] = new ImageIcon[] { piece, flag };
		theme[1] = new ImageIcon[] { piece, flag };
		theme[2] = new ImageIcon[] { piece, flag };
		theme[3] = new ImageIcon[] { piece, flag };
		
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
			JLabel player_name = new JLabel("Player " + (i + 1));
			player_name.setBounds(i < 2 ? 7 : 0, i % 2 == 0 ? 0 : 198, 82, 23);
			if (i < 2) player_name.setHorizontalAlignment(SwingConstants.RIGHT);
			players[i].add(player_name);
			
			// Add flag control icons
			for (int j = 0; j != 4; ++j) {
				JLabel flag_icon = new JLabel(new ImageIcon(BuilderWindow.class.getResource("/com/sun/java/swing/plaf/windows/icons/Computer.gif")));
				
				if (i < 2)
					flag_icon.setBounds(69 - 22 * j, i % 2 == 0 ? 34 : 173, 20, 20);
				else
					flag_icon.setBounds(0 + 22 * j, i % 2 == 0 ? 34 : 173, 20, 20);
				
				players[i].add(flag_icon);
			}
			
			// Add Spawn Stack
			// Waiting for work on spawn stack
			
			frame.getContentPane().add(players[i]);
		}
		
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
		
		for (int y = 0; y != cells.length; ++y)
			for (int x = 0; x != cells.length; ++x) {
				cells[y][x] = new JButton(x + ", " + y);
				cells[y][x].setBackground(Color.BLACK);
				
				cells[y][x].addMouseListener(new MouseAdapter() {
					private void ternaryColor(JButton item, boolean test, Color if_true, Color if_false) {
						item.setBackground(test ? if_true : if_false);
					}
					
					public void mouseEntered(MouseEvent e) {
						// check game
						((JButton)e.getSource()).setBackground(Color.CYAN);
					}
					
					public void mouseExited(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						ternaryColor(src, logic.isClicked(src), Color.GREEN, Color.BLACK);
					}
					
					public void mouseClicked(MouseEvent e) {
						JButton src = (JButton)e.getSource();
						
						// If right-click
						if (e.getButton() == MouseEvent.BUTTON3) {
							int x = src.getX() / 55, y = src.getY() / 55;
							System.out.println("X: " + x + ", Y: " + y);
							
							JButton[][] adj = getAdjacent(src.getX() / 55, src.getY() / 55);
							
							for (JButton imm : adj[0])
								if (imm != null) imm.setBackground(Color.YELLOW);
							
							for (JButton nimm : adj[1])
								if (nimm != null) nimm.setBackground(Color.MAGENTA);
							
							return;
						}
						
						logic.click(src);
						ternaryColor(src, logic.isClicked(src), Color.GREEN, Color.BLACK);
					}
				});
				
				grid.add(cells[y][x]);
				logic.add(cells[y][x]);
			}
			
		// Testing piece display
		for (int i = 0; i != players.length; ++i)
			setUpPieces(i);
		
		setUpFlags();
	}
	
	private void setUpPieces(int player) {
		int x = player < 2 ? 1 : 7;
		int y = player % 2 == 0 ? 1 : 7;
		int dx = player < 2 ? 1 : -1;
		int dy = player % 2 == 0 ? 1 : -1;
		
		newPiece(cells[y + dy][x], theme[0][0]);
		newPiece(cells[y + 2 * dy][x], theme[1][0]);
		newPiece(cells[y][x + dx], theme[2][0]);
		newPiece(cells[y][x + 2 * dx], theme[3][0]);
	}
	
	private void newPiece(JButton block, ImageIcon icon) {
		block.setText("");
		block.setIcon(icon);
	}
	
	private void setUpFlags() {
		newPiece(cells[1][1], theme[0][1]);
		newPiece(cells[1][7], theme[1][1]);
		newPiece(cells[7][1], theme[2][1]);
		newPiece(cells[7][7], theme[3][1]);
	}
	
	public void setTheme(ImageIcon[][] icons) {
		theme = icons;
	}
	
	public JLabel getLabel(int player) {
		return (JLabel)players[player].getComponents()[0];
	}
	
	public JLabel[] getFlagIcons(int player) {
		JLabel[] ret = new JLabel[4];
		
		Component[] subs = players[player].getComponents();
		for (int i = 0; i != ret.length; ++i)
			ret[i] = (JLabel)subs[i + 1];
		
		return ret;
	}
	
	public JPanel getStack(int player) {
		return (JPanel)players[player].getComponents()[5];
	}

	public GraphicsLogic getLogic() {
		return logic;
	}
	
	private boolean outOfRange(int p) {
		return p < 0 || p > 8;
	}
	
	private boolean invalidPoint(int x, int y) {
		return outOfRange(x) || outOfRange(y);
	}
	
	public JButton getCell(int x, int y) {
		return !invalidPoint(x, y) ? cells[y][x] : null;
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
				if (!(dy == 0 && dx == 0)) {
					ret[0][count] = getCell(x + dx, y + dy);
					ret[1][count++] = getCell(x + 2 * dx, y + 2 * dy);
				}
			}
		}
		
		return ret;
	}

	public boolean canReach(JButton a, JButton b) {
		return canSlide(a, b) || canJump(a, b);
	}
	
	public boolean canSlide(JButton a, JButton b) {
		int x0 = a.getX() / 55, x1 = b.getX() / 55;
		int y0 = a.getY() / 55, y1 = b.getY() / 55;
		int dx = Math.abs(x0 - x1), dy = Math.abs(y0 - y1);
		
		return dx == 1 || dy == 1;
	}
	
	public boolean canJump(JButton a, JButton b) {
		int x0 = a.getX() / 55, x1 = b.getX() / 55;
		int y0 = a.getY() / 55, y1 = b.getY() / 55;
		int dx = Math.abs(x0 - x1), dy = Math.abs(y0 - y1);
		
		return dx == 2 || dy == 2;
	}
	
	// Might need to change in the future
	public void move(JComponent from, JComponent to) {
		if (from instanceof JButton) {
			JButton f = (JButton)from;
			
			// Move from cell to cell
			if (to instanceof JButton) {
				((JButton) to).setIcon(f.getIcon());
				((JButton) to).setText("");
				f.setIcon(null);
				
			// Move from cell to spawn stack
			} else if (to instanceof JPanel) {
				JButton spawn = new JButton();
				// addEventListeners
				
				spawn.setIcon(f.getIcon());
				((JPanel)to).add(spawn);
				f.setIcon(null);
			}
			
		// Move from spawn stack to cell
		} else if (from instanceof JPanel) {
			JPanel f = (JPanel)from;
			JButton t = (JButton)to;
			
			// get the icon to switch on
			t.setIcon(((JButton)from.getComponent(0)).getIcon());
			from.remove(0);
		}
	}
}
