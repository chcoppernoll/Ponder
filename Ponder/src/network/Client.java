package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

public class Client {
	private Socket sock = null;
	private final int port = 22222;
	private final String IP = "";
	private final String macAddress;
	private final int getGameList = 0;
	private final int getGame = 1;
	private final int addMoves = 2;
	private final int createGame = 3;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	int currGameId;

	public Client() {
		macAddress = this.getMac();
		this.connect();
		this.loadGameList();
	}

	private String getMac() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

			System.out.print("Current MAC address : ");

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i],
						(i < mac.length - 1) ? ":" : ""));
			}

			return sb.toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void connect() {
		try {
			sock = new Socket(IP, port);
			in = new ObjectInputStream(sock.getInputStream());
			out = new ObjectOutputStream(sock.getOutputStream());
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

	public ArrayList<Integer> createGame() {

		try {
			CommunicationObject commOut = new CommunicationObject(
					this.createGame, -1, this.macAddress);
			out.writeObject(commOut);
			while (in.available() <= 0) {
			}
			CommunicationObject commIn = (CommunicationObject) in.readObject();
			return commIn.getGameIds();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ArrayList<Integer> loadGameList() {
		try {
			CommunicationObject commOut = new CommunicationObject(
					this.getGameList, -1, this.macAddress);
			out.writeObject(commOut);
			CommunicationObject commIn = (CommunicationObject) in.readObject();
			return commIn.getGameIds();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public void addMoves() {

	}

	public LinkedList<Event> getGame() {
		return this.getGame(currGameId);
	}

	public LinkedList<Event> getGame(int gameId) {
		this.currGameId = gameId;
		CommunicationObject commOut = new CommunicationObject(this.getGame,
				gameId, this.macAddress);

		try {
			out.writeObject(commOut);
			CommunicationObject commIn = (CommunicationObject) in.readObject();
			return commIn.getMoves();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
