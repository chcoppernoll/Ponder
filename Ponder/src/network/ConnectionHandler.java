package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;

public class ConnectionHandler implements Runnable {

	private Socket sock;
	private final String insert = "INSERT INTO moves (Move_id, Game_id, Player_id, At_Pos_X, "
			+ "At_Pos_Y, To_Pos_X, To_Pos_Y, Exiled, End_Of_Turn) "
			+ "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?);";
	private final String getAll = "SELECT * FROM moves WHERE Game_id = ? ORDER BY Move_id";
	private final String getGames = "SELECT * FROM game ORDER BY Game_id";
	private final String createGame = "INSERT INTO game (Game_id, Game_Name, Game_In_Progress) "
			+ "VALUES (DEFAULT, '', 0)";
	// private final String getAfter = "SELECT * FROM moves WHERE Game_id = ?"
	// + " AND Move_id > ? ORDER BY Move_id";
	Connection con;
	private ObjectInputStream in;
	private ObjectOutputStream out;

	public ConnectionHandler(Socket sock) {
		try {
			this.sock = sock;
			out = new ObjectOutputStream(sock.getOutputStream());
			out.flush();
			in = new ObjectInputStream(sock.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://71.13.212.62:3306/ponder", "Ponder",
					"CS3141R02");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int iter = 0;
		final int runTo = 300;
		while (!sock.isClosed() && iter < runTo) {

			try {
				Thread.sleep(1000);
				CommunicationObject comm = (CommunicationObject) in
						.readObject();
				switch (comm.getAction()) {
				case 0:
					this.getGameList(comm);
					break;
				case 1:
					this.loadGame(comm);

					break; // Save moves
				case 2:
					this.addMoves(comm);
					break; // Load game state
				case 3:
					this.createGame(comm);
					break;
				default:
					break;
				}
				iter = 0;

			} catch (IOException e) {
				iter++;
				continue;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private int getGameList(CommunicationObject comm) {
		try {
			PreparedStatement prep = con.prepareStatement(this.getGames);
			ResultSet results = prep.executeQuery();
			ArrayList<Integer> gameIds = new ArrayList<Integer>();
			while (results.next()) {
				gameIds.add(results.getInt("Game_id"));
			}
			comm.setGameIds(gameIds);
			out.writeObject(comm);
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	private int createGame(CommunicationObject comm) {
		try {
			PreparedStatement prep = con.prepareStatement(this.createGame);
			prep.executeUpdate();
			return this.getGameList(comm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private int loadGame(CommunicationObject comm) {
		int gameId = comm.getGameId();
		try {
			PreparedStatement prep = con.prepareStatement(this.getAll);
			prep.setInt(1, gameId);
			ResultSet results = prep.executeQuery();
			comm = parseMoves(comm, results);

			out.writeObject(comm);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			return 1;
		}
		return 0;
	}

	private CommunicationObject parseMoves(CommunicationObject comm,
			ResultSet results) {
		LinkedList<Event> moves = new LinkedList<Event>();
		try {
			while (results.next()) {
				
				if (results.getBoolean("Exiled") == true) {
					// Spawn event
					int playerId = results.getInt("Player_id");
					int x = results.getInt("At_Pos_X");
					int y = results.getInt("At_Pos_Y");
					boolean exiled = results.getBoolean("Exiled");
					SpawnEvent spawn = new SpawnEvent(new Position(x, y),
							playerId, exiled);
					moves.addLast(spawn);
				} else if ( results.getBoolean("End_Of_Turn") == true) {
					// End of turn event.
					int playerId = results.getInt("Player_id");
					EndTurnEvent end = new EndTurnEvent(playerId);
					moves.addLast(end);
				} else {
					// Move event
					int at_x = results.getInt("At_Pos_X");
					int at_y = results.getInt("At_Pos_Y");
					int to_x = results.getInt("To_Pos_X");
					int to_y = results.getInt("To_Pos_Y");

					MoveEvent move = new MoveEvent(new Position(at_x, at_y),
							new Position(to_x, to_y));

					moves.addLast(move);
				}

				comm.setMoves(moves);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return comm;
	}

	private void addMoves(CommunicationObject comm) {

		try {
			for (Event event : comm.getMoves()) {
				PreparedStatement prep = null;
				if (event instanceof SpawnEvent) {
					prep = con.prepareStatement(this.insert);
					prep.setInt(1, 0); // TODO change to game id
					prep.setShort(2, (short) 0); // TODO change to player id
					prep.setShort(3, (short) ((SpawnEvent) event).pos.x);
					prep.setShort(4, (short) ((SpawnEvent) event).pos.y);
					prep.setNull(5, java.sql.Types.SMALLINT);
					prep.setNull(6, java.sql.Types.SMALLINT);
					prep.setBoolean(7, ((SpawnEvent) event).exiled);
					prep.setNull(8, java.sql.Types.BOOLEAN);
				}

				else if (event instanceof MoveEvent) {
					prep = con.prepareStatement(insert);
					prep.setInt(1, 0); // TODO change to game id
					prep.setNull(2, java.sql.Types.SMALLINT);// TODO player id
					prep.setShort(3, (short) ((MoveEvent) event).from.x);
					prep.setShort(4, (short) ((MoveEvent) event).from.y);
					prep.setShort(5, (short) ((MoveEvent) event).to.x);
					prep.setShort(6, (short) ((MoveEvent) event).to.y);
					prep.setNull(7, java.sql.Types.BOOLEAN);
					prep.setNull(8, java.sql.Types.BOOLEAN);
				}

				else if (event instanceof EndTurnEvent) {
					prep = con.prepareStatement(insert);
					prep.setInt(1, 0); // TODO change to game id
					prep.setShort(2, (short) 0); // TODO Player id
					prep.setNull(3, java.sql.Types.SMALLINT);
					prep.setNull(4, java.sql.Types.SMALLINT);
					prep.setNull(5, java.sql.Types.SMALLINT);
					prep.setNull(6, java.sql.Types.SMALLINT);
					prep.setNull(7, java.sql.Types.BOOLEAN);
					prep.setBoolean(8, true);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}