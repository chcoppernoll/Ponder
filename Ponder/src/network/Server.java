package network;

import java.util.Scanner;

public class Server {

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
