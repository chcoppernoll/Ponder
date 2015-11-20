package network;

import java.io.Serializable;

public interface Event extends Serializable {
	
	static final long serialVersionUID = -1;
	public void run(); // Will need to pass in something here (but what)

	public void undo();
}
