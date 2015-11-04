package network;

import java.util.LinkedList;

public class CommunicationObject {

	private LinkedList<Event> moves;
	private int action;
	private int gameId;

	public CommunicationObject(int action, int gameId) {
		this.action = action;
		this.gameId = gameId;
		this.moves = null;
	}

	public CommunicationObject(LinkedList<Event> moves, int action, int gameId) {
		this.moves = moves;
		this.action = action;
		this.gameId = gameId;
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

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

}
