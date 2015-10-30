package game;

import java.util.LinkedList;

public class CommunicationObject {

	private LinkedList<Event> moves;
	private int action;

	public CommunicationObject(LinkedList<Event> moves, int action) {
		this.moves = moves;
		this.action = action;
	}

	public LinkedList<Event> getMoves() {
		return moves;
	}

	public void setMoves(LinkedList<Event> moves) {
		this.moves = moves;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

}
