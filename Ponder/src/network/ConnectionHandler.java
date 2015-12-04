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

int playerid = ServerSocketCreator.playerid;

private Socket sock;

private final String insert = "INSERT INTO moves (Move_id, Game_id, Player_id, At_Pos_X, "

+ "At_Pos_Y, To_Pos_X, To_Pos_Y, Exiled, End_Of_Turn) "

+ "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?);";

private final String getAll = "SELECT * FROM moves WHERE Game_id = ? ORDER BY Move_id";

private final String getGames = "SELECT * FROM game ORDER BY Game_id";

private final String createGame = "INSERT INTO game (Game_id, Game_Name, Game_In_Progress) "

+ "VALUES (DEFAULT, '', 0)";

private final String numInGame = "SELECT COUNT(*) FROM player WHERE Game_id=?";

private final String getPlayers = "SELECT * FROM player WHERE Game_id=?";

private final String addPlayer = "INSERT INTO player (player_id, Game_id, mac) VALUES (?, ?, ?)";

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

DriverManager.registerDriver(new com.mysql.jdbc.Driver());

con = DriverManager.getConnection(

"jdbc:mysql://71.13.212.62:3306/ponder ", "Ponder",

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

comm.setPlayerid(playerid % 4);

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



private void loadGame(CommunicationObject comm) {

int gameId = comm.getGameId();

int playerId = this.getPlayerId(gameId, comm.getMac());

if (playerId != -1) {

comm.setPlayerid(playerId);

this.sendGame(gameId, comm);

} else {

int playerCount = this.getPlayerCount(gameId);

if (playerCount < 4) {

playerId = this.addPlayer(comm, playerCount + 1);

if (playerId != -1) {

comm.setPlayerid(playerId);

this.sendGame(gameId, comm);

}

} else {

comm.setGameId(-1);

try {

out.writeObject(comm);

} catch (IOException e) {

e.printStackTrace();

}

}

}

}



/**

* Gets the player id. Returns -1 if the player isn't in the game.

* 

* @param gameId

* @param mac

* @return The player id. -1 if player isn't in game.

*/

private int getPlayerId(int gameId, String mac) {

try {

PreparedStatement prep = con.prepareStatement(this.getPlayers);

prep.setInt(1, gameId);

ResultSet rs = prep.executeQuery();

int playerId = 1;

while (rs.next()) {

if (mac.equals(rs.getString("mac"))) {

return playerId;

}

playerId++;

}

} catch (SQLException e) {

e.printStackTrace();

}

return -1;

}



private void sendGame(int gameId, CommunicationObject comm) {

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

}

}



private CommunicationObject parseMoves(CommunicationObject comm,

ResultSet results) {

LinkedList<Event> moves = new LinkedList<Event>();

try {

while (results.next()) {

results.getInt("To_Pos_X");

if (!results.wasNull()) {

int at_x = results.getInt("At_Pos_X");

int at_y = results.getInt("At_Pos_Y");

int to_x = results.getInt("To_Pos_X");

int to_y = results.getInt("To_Pos_Y");



MoveEvent move = new MoveEvent(new Position(at_x, at_y),

new Position(to_x, to_y), false, null);



moves.addLast(move);

} else if (results.getString("End_Of_Turn") != null

&& results.getString("End_Of_Turn").charAt(0) == 'T') {

// End of turn event.

int playerId = results.getInt("Player_id");

TurnEvent end = new TurnEvent(playerId);

moves.addLast(end);

} else {

int playerId = results.getInt("Player_id");

int x = results.getInt("At_Pos_X");

int y = results.getInt("At_Pos_Y");

boolean exiled = false;

if (results.getString("Exiled") != null

&& results.getString("Exiled").charAt(0) == 'T') {

exiled = true;

}

SpawnEvent spawn = new SpawnEvent(new Position(x, y),

playerId, exiled);

moves.addLast(spawn);



}



}

} catch (SQLException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}

comm.setMoves(moves);

return comm;

}



private void addMoves(CommunicationObject comm) {



try {

for (Event event : comm.getMoves()) {

PreparedStatement prep = null;

if (event instanceof SpawnEvent) {

prep = con.prepareStatement(this.insert);

prep.setInt(1, comm.getGameId()); // TODO change to game id

prep.setShort(2, (short) ((SpawnEvent) event).owner); // TODO

// change

// to

// player

// id

prep.setShort(3, (short) ((SpawnEvent) event).pos.x);

prep.setShort(4, (short) ((SpawnEvent) event).pos.y);

prep.setNull(5, java.sql.Types.SMALLINT);

prep.setNull(6, java.sql.Types.SMALLINT);

prep.setString(7, String

.valueOf(((SpawnEvent) event).exiled ? 'T' : 'F'));

prep.setNull(8, java.sql.Types.CHAR);

prep.executeUpdate();

}



else if (event instanceof MoveEvent) {

prep = con.prepareStatement(insert);

prep.setInt(1, comm.getGameId()); // TODO change to game id

prep.setNull(2, comm.getPlayerid());// TODO player id

prep.setShort(3, (short) ((MoveEvent) event).from.x);

prep.setShort(4, (short) ((MoveEvent) event).from.y);

prep.setShort(5, (short) ((MoveEvent) event).to.x);

prep.setShort(6, (short) ((MoveEvent) event).to.y);

prep.setNull(7, java.sql.Types.CHAR);

prep.setNull(8, java.sql.Types.CHAR);

prep.executeUpdate();

}



else if (event instanceof TurnEvent) {

prep = con.prepareStatement(insert);

prep.setInt(1, comm.getGameId()); // TODO change to game id

prep.setShort(2, (short) comm.getPlayerid()); // TODO Player

// id

prep.setNull(3, java.sql.Types.SMALLINT);

prep.setNull(4, java.sql.Types.SMALLINT);

prep.setNull(5, java.sql.Types.SMALLINT);

prep.setNull(6, java.sql.Types.SMALLINT);

prep.setNull(7, java.sql.Types.CHAR);

prep.setString(8, String.valueOf('T'));

prep.executeUpdate();

}

}

} catch (SQLException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}

}



private int addPlayer(CommunicationObject comm, int playerId) {

try {

PreparedStatement prep = con.prepareStatement(this.addPlayer);

prep.setInt(1, playerId);

prep.setInt(2, comm.getGameId());

prep.setString(3, comm.getMac());

prep.executeUpdate();

return playerId;

} catch (SQLException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}



return -1;

}



private int getPlayerCount(int gameId) {

try {

PreparedStatement prep = con.prepareStatement(numInGame);

prep.setInt(1, gameId);

ResultSet rs = prep.executeQuery();

if (rs.next()) {

return rs.getInt(1);

} else {

return -1;

}



} catch (SQLException e) {

// TODO Auto-generated catch block

e.printStackTrace();

}



return -1;



}

}