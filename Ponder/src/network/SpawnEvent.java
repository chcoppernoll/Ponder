package network;

public class SpawnEvent implements Event {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1556590652144719455L;
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

	@Override
	public String toString() {
		return "SpawnEvent [pos=" + pos.toString() + ", owner=" + owner + ", exiled=" + exiled + "]";
	}

}
