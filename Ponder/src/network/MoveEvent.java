package network;

public class MoveEvent implements Event {
	
	@Override
	public String toString() {
		return "MoveEvent [from=" + from.toString() + ", to=" + to.toString() + "]";
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8308940498190880283L;
	public final Position from, to;

	public MoveEvent(Position from, Position to) {
		this.from = from;
		this.to = to;
	}

	public void run() {

	}

	public void undo() {

	}

}
