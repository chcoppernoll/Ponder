package network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

public class CommunicationObject implements Serializable {

	private static final long serialVersionUID = -1;
	private LinkedList<Event> moves;
	private int action;
	private int gameId;
	@SuppressWarnings("unused")
	private String mac;
	private ArrayList<Integer> gameIds;
	private int playerid;

	public int getPlayerid() {
		return playerid;
	}

	public void setPlayerid(int playerid) {
		this.playerid = playerid;
	}

	public CommunicationObject(int action, int gameId, String mac) {
		this.action = action;
		this.gameId = gameId;
		this.moves = null;
		this.mac = mac;
	}

	public CommunicationObject(LinkedList<Event> moves, int action, int gameId,
			String mac) {
		this.moves = moves;
		this.action = action;
		this.gameId = gameId;
		this.mac = mac;
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

	public ArrayList<Integer> getGameIds() {
		return gameIds;
	}

	public void setGameIds(ArrayList<Integer> gameIds) {
		this.gameIds = gameIds;
	}

}
