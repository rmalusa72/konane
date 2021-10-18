// Move.java
// Represents one move 

import java.util.ArrayList;

class Move{

	final int REMOVAL_STEPS=0;
	final int ROW = 0;
	final int COL = 1;

	int steps; 			// The number of steps in the move (usually 1; 0 for a removal move)
	ArrayList<int[]> coordinates; // The coordinates at which we start & then those to which we move (for a normal move, at least one set but possibly more. For a removal move, empty)
	int player; 

	// CONSTRUCTORS 

	// Create a normal move 
	public Move(int _steps, ArrayList<int[]> _coordinates, int _player){
		if (_steps < 1){
			throw new IllegalArgumentException("Normal move must take at least one step");
		}
		if (_coordinates.size() != _steps+1){
			throw new IllegalArgumentException("Number of steps must match number of coordinates");
		}
		for(int i=0; i<_coordinates.size(); i++){
			if(_coordinates.get(i).length != 2){
				throw new IllegalArgumentException("Each coordinate must have an x and y component");
			}
		}
		if(!(_player==GameState.PLAYER1 || _player==GameState.PLAYER2)){
			throw new IllegalArgumentException("Player must be a valid player");
		}
		steps = _steps;
		coordinates = _coordinates;
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
		coordinates = new ArrayList<int[]>(1);
		coordinates.add(removalCoordinates);
		player = _player;
	}

	// ACCESSORS 
	public int steps(){
		return steps;
	}

	public int startRow(){
		return coordinates.get(0)[ROW];
	}

	public int startCol(){
		return coordinates.get(0)[COL];
	}

	public ArrayList<int[]> coordinates(){
		return coordinates;
	}

	public int[] nthStep(int n){
		return coordinates.get(n);
	}

	public int player(){
		return player; 
	}

	public boolean isRemoval(){
		return steps==REMOVAL_STEPS;
	}

	// DEBUGGING ETC 
	public String toString(){
		String returnString = GameState.PLAYER_SYMBOL[player] + " ";
		if (steps==REMOVAL_STEPS){
			// A removal move.
			returnString += "removes ";
		} else {
			// A normal move. 
			returnString += "moves ";
		}
		for (int i=0; i<steps+1; i++){
			returnString += "to ";
			returnString += "<" + Integer.toString(coordinates.get(i)[ROW]) + "," + Integer.toString(coordinates.get(i)[COL]) + "> ";
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
		moves.add(coordinates);
		moves.add(otherCoordinates);
		Move normalMove = new Move(1, moves, GameState.PLAYER1);
		System.out.println(normalMove);

		int[] otherOtherCoordinates = new int[]{4,8};
		ArrayList<int[]> longMoves = new ArrayList<int[]>(2);
		longMoves.add(coordinates);
		longMoves.add(otherCoordinates);
		longMoves.add(otherOtherCoordinates);
		Move longMove = new Move(2, longMoves, GameState.PLAYER2);
		System.out.println(longMove);

		return;
	}

}