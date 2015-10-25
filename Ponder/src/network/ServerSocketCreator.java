package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketCreator implements Runnable {

	ServerSocket serverSocket = null;

	@Override
	public void run() {
		System.out.println("Accepting");
		/*
		 * try { Connection con = DriverManager.getConnection(
		 * "jdbc:mysql://71.13.212.62:3306", "Ponder", "CS3141R02");
		 * System.out.println("This worked"); } catch (SQLException e1) {
		 * System.out.println("Didn't connect to db"); e1.printStackTrace(); }
		 */
		try {
			serverSocket = new ServerSocket(7777);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			try {
				Socket sock = serverSocket.accept();
				System.out.println("Accepted");
				new Thread(new ConnectionHandler(sock)).start();
				System.out.println("What up");
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
			System.out.println("Closed dat shit");
			System.exit(0);
		}
	}

}
