package game;

import java.awt.EventQueue;

import gui.SwingGraphics;
import network.Client;

public class Ponder {
	public static void main(String[] args) {
		PonderLogic logic = new PonderLogic();
		SwingGraphics window = new SwingGraphics(logic);
		Player[] players = new Player[4];
		LocalPlayer local = new LocalPlayer();
		NetworkPlayer friend = new NetworkPlayer();
		Client net = new Client();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//window.reset();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		players[0] = local;
		players[1] = local;
		players[2] = local;
		players[3] = local;
		
		// Game Loop
			// get next player
			// player.onTurnStart(window, net);
			// wait for end turn (allow switching games)
			// Test Corner Rule
			// player.onTurnEnd();
		
		window.reset();
		while (logic.victor() == -1) {
			logic.nextTurn();
			
			Player curr = players[logic.getCurrPlayer()];
			curr.onTurnStart(window, net);
			
			// curr.turnOver is re
			while (!curr.turnOver(window, net)) {
				boolean tmp = curr.turnOver(window, net);
				System.out.print(tmp ? "" : "");						// The program never exits this loop without this statement
			}
			// wait for end turn
			
			//if (curr instanceof LocalPlayer)
				//for (JButton piece : logic.getSurrounded()) {
					//logic.addEvent(new SpawnEvent(logic.positionOf(piece), logic.getPieceOwner(piece), true))
					//window.move(piece, window.getStack(logic.getPieceOwner(piece)));
				//}

			curr.onTurnEnd(window, net);
		}
		
		System.out.println("Won by player " + (logic.getCurrPlayer() + 1));
	}
}
