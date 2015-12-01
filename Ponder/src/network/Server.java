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
		System.out.println("Would you like to stop the server? Y/N");
		input = read.nextLine().charAt(0);
		while (input != 'Y') {
			input = read.nextLine().charAt(0);
		}
		read.close();
		create.terminate();
	}
}
