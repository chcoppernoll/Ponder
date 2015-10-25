package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

	private Socket sock;
	private BufferedReader read;

	public ConnectionHandler(Socket sock) {
		this.sock = sock;
		try {
			read = new BufferedReader(new InputStreamReader(
					sock.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!sock.isClosed()) {
			String nextLine;
			try {
				while ((nextLine = read.readLine()) != null) {
					System.out.println(nextLine);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
