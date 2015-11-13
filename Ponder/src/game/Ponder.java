package game;

import java.awt.EventQueue;

import javax.swing.JButton;

import gui.SwingGraphics;
import network.Client;

// TODO Network Interaction
/*
 * Determine how to switch games (and players)
 */

public class Ponder {
	public static void main(String[] args) {
		PonderLogic logic = new PonderLogic();
		final SwingGraphics window = new SwingGraphics(logic);
		Client net = new Client();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		window.setClientele(new LocalPlayer(), new NetworkPlayer(), null);				// pass null for players and window will create the Player[]
		window.reset();
		
		while (true) {
			while (logic.victor() == -1) {
				logic.nextTurn();

				Player curr = window.getCurrentPlayer();
				curr.onTurnStart(window, net);

				// wait for the player to finish their turn
				while (logic.getCurrPlayer() != -1 && !curr.turnOver(window, net)) {
					// Have some network communication in here ???
					boolean tmp = curr.turnOver(window, net);
					System.out.print(tmp ? "" : ""); // The program never exits this loop without this statement
				}
				
				if (logic.getCurrPlayer() != -1) curr.onTurnEnd(window, net);
			}
			
			System.out.println("Won by player " + (logic.getCurrPlayer() + 1));
			
			// Pause before clearing board and starting new game
			while (logic.getCurrPlayer() != -1) System.out.println("");
		}
	}
}
