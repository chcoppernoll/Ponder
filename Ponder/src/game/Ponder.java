package game;

import java.awt.EventQueue;
import java.io.InputStream;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import gui.SwingGraphics;
import network.Client;

// TODO Network Interaction
/*
 * Determine how to switch games (and players)
 */

public class Ponder {
	
	/**
	 * Set up the graphical icons for the game
	 * @param window
	 */
	public static void initGraphics(final SwingGraphics window) {
		ImageIcon[][] theme = window.getTheme();
		int w = 55, h = 55;
		
		ImageIcon tmp = new ImageIcon(Ponder.class.getResource("/circle.png"));
		theme[0][0] = new ImageIcon(SwingGraphics.scaleImage(tmp.getImage(), w, h));
		
		tmp = new ImageIcon(Ponder.class.getResource("/asterisk.png"));
		theme[1][0] = new ImageIcon(SwingGraphics.scaleImage(tmp.getImage(), w - 10, h - 10));
		
		tmp = new ImageIcon(Ponder.class.getResource("/ring.png"));
		theme[2][0] = new ImageIcon(SwingGraphics.scaleImage(tmp.getImage(), w - 10, h - 10));
		
		tmp = new ImageIcon(Ponder.class.getResource("/multip.png"));
		theme[3][0] = new ImageIcon(SwingGraphics.scaleImage(tmp.getImage(), w, h));
		
		tmp = new ImageIcon(Ponder.class.getResource("/Flag.png"));
		tmp = new ImageIcon(SwingGraphics.scaleImage(tmp.getImage(), w - 10, h - 15));
		theme[0][1] = tmp;
		theme[1][1] = tmp;
		theme[2][1] = tmp;
		theme[3][1] = tmp;
	}
	
	public static void main(String[] args) {
		PonderLogic logic = new PonderLogic();
		Client net = new Client();
		final SwingGraphics window = new SwingGraphics(logic, net);
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		initGraphics(window);
		
		window.setClientele(new LocalPlayer(), new NetworkPlayer(), null);				// window creates a default player array
		window.reset();
		
		while (true) {
			while (logic.victor() == -1) {
				logic.nextTurn();
				window.setLoaded(false);

				Player curr = window.getCurrentPlayer();
				curr.onTurnStart(window, net);

				// wait for the player to finish their turn
				while (logic.getCurrPlayer() != -1 && !window.loaded() && !curr.turnOver(window, net))
					try{
						//for (Player p : window.getPlayers()) {
							//System.out.print(p instanceof LocalPlayer);
							//System.out.print(" ");
						//}
							
						//System.out.println("");
						Thread.sleep(500);
					} catch (Exception e) {}
				
				if (logic.getCurrPlayer() != -1 && !window.loaded()) curr.onTurnEnd(window, net);
			}
			
			window.displayVictor(logic.getCurrPlayer() + 1);
			
			// Pause before clearing board and starting new game
			while (logic.getCurrPlayer() != -1)
				try {
					Thread.sleep(500);
				} catch (Exception e){}
		}
	}
}
