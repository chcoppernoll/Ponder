package network;

public class SpawnEvent implements Event {
	public final Position pos;
	public int owner;
	public boolean exiled;
	
	public SpawnEvent(Position pos, int owner, boolean toExile) {
		this.pos = pos;
		this.owner = owner;
		this.exiled = toExile;
	}

	public void run() {

	}

	public void undo() {

	}

}
