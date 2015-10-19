package network;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
	private static ServerSocket sock = null;

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public void run() {
		System.out.println("Accepting");
		try {
			sock = new ServerSocket(7777);
			sock.accept();
			System.out.println("Accepted");
			sock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed");
			e.printStackTrace();
		}
	}
}
