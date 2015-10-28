package game;

import gui.SwingGraphics;
import network.Client;

public class LocalPlayer implements Player {

	public void onTurnStart(SwingGraphics graphics, Client net) {
		graphics.acceptInput();
	}

	public void onTurnEnd(SwingGraphics graphics, Client net) {
		graphics.stopInput();
		// have to send to client
	}

	public boolean turnOver(SwingGraphics graphics, Client net) {
		return graphics.getLogic().turnOver();
	}

}
