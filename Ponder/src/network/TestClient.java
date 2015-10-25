package network;

import java.net.Socket;

public class TestClient {
	private Socket sock = null;
	private static final int port = 7777;
	private static final String IP = "127.0.0.1";

	public static void main(String[] args) {
		System.out.println("Running");
		TestClient client = new TestClient();
		try {
			Thread.sleep(30000);
		} catch (Exception e) {
		}
		client.close();
	}

	public TestClient() {
		this.connect();
	}

	private void connect() {
		System.out.println("Attempting to connect.");
		System.out.println(this.IP);
		System.out.println(this.port);
		try {
			this.sock = new Socket(this.IP, this.port);
			System.out.println("Connected");
		} catch (Exception e) {
			System.out.println("Failed");
		}
	}

	public void close() {
		try {
			this.sock.close();
		} catch (Exception e) {
			System.out.println("Error closing");
		}
	}
}
