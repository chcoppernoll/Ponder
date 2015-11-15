package game;

import java.util.LinkedList;

import gui.SwingGraphics;
import network.Client;

public class NetworkPlayer implements Player {
	private LinkedList<Event> move;

	public void onTurnStart(SwingGraphics graphics, Client net) {
		//send a message
		move = new LinkedList<>();
	}

	public void onTurnEnd(SwingGraphics graphics, Client net) {
		for (Event e : move) graphics.runEvent(e);
	}

	// Communicate with server/load "unseen" events into move
	public boolean turnOver(SwingGraphics graphics, Client net) {
		
		
		return move.getLast() instanceof TurnEvent;
	}

}
