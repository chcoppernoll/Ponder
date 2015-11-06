package game;

import java.awt.Color;

import javax.swing.JButton;

import gui.SwingGraphics;
import network.Client;

public class LocalPlayer implements Player {

	public void onTurnStart(SwingGraphics graphics, Client net) {
		graphics.acceptInput();
	}

	public void onTurnEnd(SwingGraphics graphics, Client net) {
		graphics.stopInput();
		graphics.color(Color.BLACK);
		
		PonderLogic logic = graphics.getLogic();
		for (JButton piece : logic.getSurrounded()) {
			logic.addEvent(new SpawnEvent(logic.positionOf(piece), logic.getPieceOwner(piece), true));
			graphics.runEvent(logic.lastEvent());
		}
		
		//logic.addEvent(new TurnEvent());
		// have to send to client
	}

	public boolean turnOver(SwingGraphics graphics, Client net) {
		return graphics.getLogic().turnOver();
	}

}
