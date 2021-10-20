// Move.java
// Represents one move 

import java.util.ArrayList;
import java.util.Arrays;

class Move{

	final int REMOVAL_STEPS=0;
	final int ROW = 0;
	final int COL = 1;

	int steps; 	// The number of steps in the move (0 for a removal move)
	ArrayList<int[]> coordinates; // The coordinates of the starting piece and each successive location
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

	public int[] startCoordinates(){
		return coordinates.get(0);
	}

	public int endRow(){
		return coordinates.get(steps)[ROW];
	}

	public int endCol(){
		return coordinates.get(steps)[COL];
	}

	public ArrayList<int[]> coordinates(){
		return coordinates;
	}

	public ArrayList<int[]> jumpedOver(){
		ArrayList<int[]> jumpedOverCoordinates = new ArrayList<int[]>(8);
		for(int i=1; i<steps+1; i++){
			jumpedOverCoordinates.add(new int[]{(coordinates.get(i)[0] + coordinates.get(i-1)[0])/2, (coordinates.get(i)[1] + coordinates.get(i-1)[1])/2});
		}
		return jumpedOverCoordinates;
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

	// Returns a string representation 
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
			if(i==0){
				returnString += "from ";
			} else {
				returnString += "to ";
			}
			returnString += "<" + Integer.toString(coordinates.get(i)[ROW]) + "," 
				+ Integer.toString(coordinates.get(i)[COL]) + "> ";
		}
		return returnString;
	}

	// Testing if run as main class
	public static void main(String[] args){
		int[] coordinates = new int[]{4,4};
		
		Move removalMove = new Move(coordinates, GameState.PLAYER1);
		System.out.println(removalMove);
		System.out.println("Jumped over ");
		System.out.println(removalMove.jumpedOver());
		
		Move removalMove2 = new Move(coordinates, GameState.PLAYER2);
		System.out.println(removalMove2);
		System.out.println("Jumped over ");
		System.out.println(removalMove2.jumpedOver());

		int[] otherCoordinates = new int[]{4,6};
		ArrayList<int[]> moves = new ArrayList<int[]>(1);
		moves.add(coordinates);
		moves.add(otherCoordinates);
		Move normalMove = new Move(1, moves, GameState.PLAYER1);
		System.out.println(normalMove);
		System.out.println("Jumped over ");
		System.out.println(Arrays.toString(normalMove.jumpedOver().get(0)));

		int[] otherOtherCoordinates = new int[]{4,8};
		ArrayList<int[]> longMoves = new ArrayList<int[]>(2);
		longMoves.add(coordinates);
		longMoves.add(otherCoordinates);
		longMoves.add(otherOtherCoordinates);
		Move longMove = new Move(2, longMoves, GameState.PLAYER2);
		System.out.println(longMove);
		System.out.println("Jumped over ");
		System.out.println(Arrays.toString(longMove.jumpedOver().get(0)));
		System.out.println(Arrays.toString(longMove.jumpedOver().get(1)));

		return;
	}

}