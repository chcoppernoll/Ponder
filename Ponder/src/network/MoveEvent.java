package network;

import java.util.Arrays;

public class MoveEvent implements Event {
	static final long serialVersionUID = -1;
	public final Position from, to;
	public final boolean isSlide;
	public final boolean[] movedFlags;
	
	public MoveEvent(Position from, Position to, boolean slide, boolean[] flags) {
		this.from = from;
		this.to = to;
		this.isSlide = slide;
		if(flags !=null){
		this.movedFlags = Arrays.copyOf(flags, flags.length);
		}else{
			this.movedFlags = null;
		}
	}
}
