// Move.java
// Represents one move 

import java.util.ArrayList;

class Move{

	final int REMOVAL_STEPS=0;
	final int ROW = 0;
	final int COL = 1;

	int steps; 			// The number of steps in the move (usually 1; 0 for a removal move)
	int[] startCoordinates; // The initial coordinates of the piece we are moving 
	ArrayList<int[]> nextCoordinates; // The coordinates to which we move (for a normal move, at least one set but possibly more. For a removal move, empty)
	int player; 

	// Create a normal move 
	public Move(int _steps, int[] _startCoordinates, ArrayList<int[]> _nextCoordinates, int _player){
		if (_steps < 1){
			throw new IllegalArgumentException("Normal move must take at least one step");
		}
		if (_startCoordinates.length != 2){
			throw new IllegalArgumentException("Start coordinates should be one x and one y coordinate");
		}
		if (_nextCoordinates.size() != _steps){
			throw new IllegalArgumentException("Number of steps must match number of coordinates");
		}
		for(int i=0; i<_nextCoordinates.size(); i++){
			if(_nextCoordinates.get(i).length != 2){
				throw new IllegalArgumentException("Each coordinate must have an x and y component");
			}
		}
		if(!(_player==GameState.PLAYER1 || _player==GameState.PLAYER2)){
			throw new IllegalArgumentException("Player must be a valid player");
		}
		steps = _steps;
		startCoordinates = _startCoordinates;
		nextCoordinates = _nextCoordinates;
		player = _player;
	}

	// Create a removal move 
	public Move(int[] removalCoordinates, int _player){
		if (removalCoordinates.length != 2){
			throw new IllegalArgumentException("Removal move should be one x and one y coordinate");
		}
		if(!(_player==GameState.PLAYER1 || _player==GameState.PLAYER2)){
			throw new IllegalArgumentException("Player must be a valid player");
		}
		steps = 0;
		startCoordinates = removalCoordinates;
		nextCoordinates = null; 
		player = _player;
	}

	public String toString(){
		String returnString = GameState.PLAYER_SYMBOL[player] + " ";
		if (steps==REMOVAL_STEPS){
			// A removal move.
			returnString += "removes ";
			returnString += "<" + Integer.toString(startCoordinates[ROW]) + "," + Integer.toString(startCoordinates[COL]) + ">";
		} else {
			// A normal move. 
			returnString += "moves ";
			returnString += "<" + Integer.toString(startCoordinates[ROW]) + "," + Integer.toString(startCoordinates[COL]) + "> ";
			for (int i=0; i<steps; i++){
				returnString += "to ";
				returnString += "<" + Integer.toString(nextCoordinates.get(i)[ROW]) + "," + Integer.toString(nextCoordinates.get(i)[COL]) + "> ";
			}
		}
		return returnString;
	}

	// Testing move constructors 
	public static void main(String[] args){
		int[] coordinates = new int[]{4,4};
		
		Move removalMove = new Move(coordinates, GameState.PLAYER1);
		System.out.println(removalMove);
		
		Move removalMove2 = new Move(coordinates, GameState.PLAYER2);
		System.out.println(removalMove2);

		int[] otherCoordinates = new int[]{4,6};
		ArrayList<int[]> moves = new ArrayList<int[]>(1);
		moves.add(otherCoordinates);
		Move normalMove = new Move(1, coordinates, moves, GameState.PLAYER1);
		System.out.println(normalMove);

		int[] otherOtherCoordinates = new int[]{4,8};
		ArrayList<int[]> longMoves = new ArrayList<int[]>(2);
		longMoves.add(otherCoordinates);
		longMoves.add(otherOtherCoordinates);
		Move longMove = new Move(2, coordinates, longMoves, GameState.PLAYER2);
		System.out.println(longMove);

		return;
		//Move m = Move()
	}

}