package network;

import java.util.ArrayList;
import java.util.LinkedList;

public class main_client {
	
	public static void printGameList(ArrayList<Integer> arr){
		for(int i = 0;i<arr.size();i++){
			System.out.println(arr.get(i));
		}
	}

	public static void main(String[] args) {
		
		LinkedList<Event> event;
		// TODO Auto-generated method stub
		Client myclient = new Client();
		ArrayList<Integer> gamelist = new ArrayList<Integer>();
		gamelist = myclient.loadGameList();
		System.out.println("Loading Game List");
		printGameList(gamelist);
		//System.out.println("Creating Game");
		gamelist = myclient.createGame();
		//System.out.println("Created Game");
		//printGameList(gamelist);
		
	
			int temp = gamelist.size()-1;
			System.out.println("Checking Moves of Current Game " + temp);
			event = myclient.getGame(temp);
			if(event == null){
				System.out.println("Oops should be zero, there is no moves in the new game.");
			}else{
				System.out.println("Fuck yeah");
			}
			LinkedList<Event> moves = new LinkedList<Event>();
			int playerId = 0;
			System.out.println("Creating Moves");
			for(int i = 0; i < 20; i++){
				if(i % 5 == 2){
					EndTurnEvent move = new EndTurnEvent(playerId % 4);
					++playerId;
					moves.add(move);
					System.out.println("EndTurnEvent: PlayerId=" + (playerId%4));
				}else if(i % 6 == 2){
					SpawnEvent temp13 = new SpawnEvent(new Position(i-2,i-1),playerId % 4,i%2 ==1 ? true:false);
					moves.add(temp13);
					System.out.println("SpawnEvent: From (" + (i-2) + "," + (i-1) + ") PlayerId=" + (playerId%4) + "Exiled " + (i%2 ==1 ? true:false));
				}else{
					MoveEvent temp12 = new MoveEvent(new Position(i-1,i-2),new Position(i+1,i+2));
					moves.add(temp12);
					System.out.println("MoveEvent: From (" + (i-1) + "," + (i-2) + ") To (" + (i+1) + "," + (i+2) + ")");

				}
			}
			System.out.println("Moves Created, Sending Moves NOW");
			myclient.addMoves(moves);
			System.out.println("Moves Sent");
			moves = null;
			System.out.println("Requesting Moves");
			moves = myclient.getGame();
			if(moves == null || moves.size() == 0){
				System.out.println("Error Receving Moves");
			}else{
				System.out.println("Moves received");
			}
			System.out.println("Writing out moves size of " + moves.size() );
			for(Event e : moves){
				if(e instanceof MoveEvent)
				System.out.println(((MoveEvent)e).toString());
				else if (e instanceof SpawnEvent){
					System.out.println(((SpawnEvent)e).toString());
				}else{
					System.out.println(((EndTurnEvent)e).toString());
				}
			}
			
			
	}
	
}
