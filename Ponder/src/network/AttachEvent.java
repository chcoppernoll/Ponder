package game;

public class AttachEvent implements Event {
	public final Position pos;
	public final int flag;
	public final boolean dropped;
	
	public AttachEvent(Position pos, int flag, boolean dropped) {
		this.pos = pos;
		this.flag = flag;
		this.dropped = dropped;
	}
}
