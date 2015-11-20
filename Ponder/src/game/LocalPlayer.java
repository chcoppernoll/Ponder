package game;

import java.awt.Color;

import javax.swing.JButton;
import gui.SwingGraphics;
import network.Client;
import network.SpawnEvent;
import network.TurnEvent;

public class LocalPlayer implements Player {

	/**
	 * Polymorphic method for the beginning of a player's turn
	 */
	public void onTurnStart(SwingGraphics graphics, Client net) {
		graphics.acceptInput();
	}

	/**
	 * Polymorphic method for the end of a player's turn
	 */
	public void onTurnEnd(SwingGraphics graphics, Client net) {
		graphics.stopInput();
		graphics.color(Color.BLACK);
		
		PonderLogic logic = graphics.getLogic();
		for (JButton piece : logic.getSurrounded()) {
			logic.addEvent(new SpawnEvent(logic.positionOf(piece), logic.getPieceOwner(piece), true));
			graphics.runEvent(logic.lastEvent());
		}
		
		logic.addEvent(new TurnEvent(logic.getCurrPlayer()));
		
		// have to send to client
	}

	/**
	 * Polymorphic method for determining when to switch from the "turn" phase to the "end turn" phase
	 */
	public boolean turnOver(SwingGraphics graphics, Client net) {
		return graphics.getLogic().turnOver();
	}

}
