package network;

import java.sql.Connection;
import java.util.Scanner;

public class Server {

	private final String insert = "INSERT INTO moves (Game_id, Move_id, Player_id, At_Pos_X, "
			+ "At_Pos_Y, To_Pos_X, To_Pos_Y, Exiled, End_Of_Turn) "
			+ "VALUES (?, Default, ?, ?, ?, ?, ?, ?, ?);";
	Connection con;

	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public void run() {

		ServerSocketCreator create = new ServerSocketCreator();
		Thread serverSock = new Thread(create);
		serverSock.start();

		Scanner read = new Scanner(System.in);
		char input = 'N';

		while (input != 'Y') {
			System.out.println("Would you like to stop the server? Y/N");
			input = read.nextLine().charAt(0);
			System.out.println(input);

		}

		create.terminate();
	}
}
