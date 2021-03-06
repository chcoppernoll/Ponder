package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketCreator implements Runnable {

	ServerSocket serverSocket = null;
	public static int playerid = 0;

	@Override
	public void run() {
		System.out.println("Accepting");
		try {
			serverSocket = new ServerSocket(25567);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {
				Socket sock = serverSocket.accept();
				System.out.println("Client Accepted");
				new Thread(new ConnectionHandler(sock)).start();
				playerid++;
				System.out.println("Accepting More Clients");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("The Socket closed.");
				System.exit(1);
			}

		}

	}

	public void terminate() {
		try {
			serverSocket.close();
			System.out.println("Closing");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (serverSocket.isClosed()) {
			System.out.println("Closed");
			System.exit(0);
		}
	}

}
