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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Client {
	private Socket sock = null;
	//private final int port = 7777;
	private final int port = 25567;
	//private final String IP = "141.219.214.24";
	//private final String IP = "71.13.212.62";
	private final String IP = "141.219.213.244";
	private final String macAddress;
	private final int getGameList = 0;
	private final int getGame = 1;
	private final int addMoves = 2;
	private final int createGame = 3;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	int currGameId;
	int playerid;
	private final Lock _mutex = new ReentrantLock(true);


	public Client() {
		currGameId = -1;
		macAddress = this.getMac();
		this.connect();
	}

	private String getMac() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			System.out.println("Current IP address : " + ip.getHostAddress());

			NetworkInterface network = NetworkInterface.getByInetAddress(ip);

			byte[] mac = network.getHardwareAddress();

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
			System.out.println("Accepted");
			out = new ObjectOutputStream(sock.getOutputStream());
			out.flush();
			in = new ObjectInputStream(sock.getInputStream());
		} catch (Exception e) {
			System.out.println("No Server To Connect To");
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

	/**
	 * Creates a new game in the database.
	 * 
	 * @return Returns an ArrayList of Integers, corresponding to the 
	 * list of game ids.
	 */
	public ArrayList<Integer> createGame() {
		
		try {
			CommunicationObject commOut = new CommunicationObject(
					this.createGame, -1, this.macAddress);
			out.writeObject(commOut);
			CommunicationObject commIn = readlock();
			return commIn.getGameIds();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Loads the list of games.
	 * 
	 * @return Returns an ArrayList of Integers, corresponding to the 
	 * list of game ids.
	 */
	public ArrayList<Integer> loadGameList() {
		try {
			CommunicationObject commOut = new CommunicationObject(
					this.getGameList, -1, this.macAddress);
			out.writeObject(commOut);
			CommunicationObject commIn = readlock();
			return commIn.getGameIds();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Adds new moves into the database. Used to update the game.
	 * 
	 * @param moves The arraylist of moves.
	 */
	public void addMoves(LinkedList<Event> moves) {
		//System.out.println(currGameId);
		CommunicationObject comm = new CommunicationObject(this.addMoves,
				this.currGameId, this.macAddress);
		comm.setMoves(moves);
		try {
			out.writeObject(comm);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the currently selected game.
	 * @return Returns a linked list of events pertaining to the game.
	 */
	public LinkedList<Event> getGame() throws NullPointerException{
		if(currGameId != -1){
			return this.getGame(currGameId);
		}
		else{
			throw new NullPointerException("No game has been selected.");
		}
	}

	/**
	 * Loads a game using the gameId parameter, then sets the current game
	 * to the gameId.
	 * 
	 * @param gameId
	 * @return Returns a linked list of events pertaining to the game.
	 */
	public LinkedList<Event> getGame(int gameId) {
		this.currGameId = gameId;
		
		CommunicationObject commOut = new CommunicationObject(this.getGame, gameId, this.macAddress);
		

		try {
			out.writeObject(commOut);
			CommunicationObject commIn = readlock();
			playerid = commIn.getPlayerid();
			if(commIn.getGameId()==-1){
				//Do something
				return null;
			}
			return commIn.getMoves();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public int getMyID(){
		return playerid;
	}
	
	public CommunicationObject readlock(){
		CommunicationObject commIn = null;
		_mutex.lock();
		try {
			commIn = (CommunicationObject) in.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_mutex.unlock();
		return commIn;
	}
}
