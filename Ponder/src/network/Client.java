package network;

import java.io.IOException;
import java.net.Socket;

public class Client {
	private Socket sock = null;
	private final int port = 22222;
	private final String IP = "";

	public Client() {
		this.connect();
	}

	private void connect() {
		try {
			sock = new Socket(IP, port);
		} catch (Exception e) {

		}
	}

	public int exit() {
		try {
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
}
