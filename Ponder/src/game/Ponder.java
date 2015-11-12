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
		
		LocalPlayer local = new LocalPlayer();
		NetworkPlayer friend = new NetworkPlayer();
		Player[] players = new Player[]{ local, local, local, local };
		
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
		
		window.reset();
		while (true) {
			while (logic.victor() == -1) {
				logic.nextTurn();

				Player curr = players[logic.getCurrPlayer()];
				curr.onTurnStart(window, net);

				// wait for the player to finish their turn
				while (!curr.turnOver(window, net)) {
					// Have some network communication in here ???
					boolean tmp = curr.turnOver(window, net);
					System.out.print(tmp ? "" : ""); // The program never exits this loop without this statement
				}
				
				//logic.addEvent(new TurnEvent(logic.getCurrPlayer()));
				curr.onTurnEnd(window, net);
			}
			
			System.out.println("Won by player " + (logic.getCurrPlayer() + 1));
			
			// Pause before clearing board and starting new game
		}
	}
}
