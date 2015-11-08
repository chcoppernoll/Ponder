package game;

import java.util.Arrays;

public class MoveEvent implements Event {
	public final Position from, to;
	public final boolean isSlide;
	public final boolean[] movedFlags;
	
	public MoveEvent(Position from, Position to, boolean slide, boolean[] flags) {
		this.from = from;
		this.to = to;
		this.isSlide = slide;
		this.movedFlags = Arrays.copyOf(flags, flags.length);
	}
}
