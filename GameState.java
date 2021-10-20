// GameState.java 
// Represents the state of the board / game 

import java.util.ArrayList;
import java.util.Arrays;

class GameState{

	final static public int PLAYER1 = 0;
	final static public int PLAYER2 = 1;
	final int EMPTY = 2;

	final static public char[] PLAYER_SYMBOL = {'X', 'O', '.'};
	final static public int[] OPPOSITE_PLAYER = {PLAYER2, PLAYER1};
	
	final static public int BOARD_SIZE = 8;

	final int[] NORTH = {-2, 0};
	final int[] EAST = {0, 2};
	final int[] SOUTH = {2, 0};
	final int[] WEST = {0, -2};
	final int[][] DIRECTIONS = {NORTH, EAST, SOUTH, WEST};
	final int NUM_DIRECTIONS = 4;

	final boolean verbose = false; 

	/* POSSIBLE TODO
	- Maintain list of pieces of each side for faster move generation - unless that increases the memory requirements too much?
	- Maintain piece count 
	*/

	/* FOR SURE TODO
	Adapt printouts to increase coordinates by 1
	*/

	int[][] board;
	int turn;
	boolean[] playersHaveRemoved; 

	// A constructor that generates an initial game state 
	public GameState(){
		board = setupInitialBoard();
		turn = PLAYER1; 
		playersHaveRemoved = new boolean[2];
		playersHaveRemoved[PLAYER1] = false;
		playersHaveRemoved[PLAYER2] = false;
		return;
	}

	// A constructor that generates a deep copy of the provided game state 
	private GameState(GameState copied){
		board = new int[BOARD_SIZE][BOARD_SIZE];
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				board[i][j] = copied.board[i][j];
			}
		}
		turn = copied.turn;
		playersHaveRemoved = new boolean[2];
		playersHaveRemoved[PLAYER1] = copied.playersHaveRemoved[PLAYER1];
		playersHaveRemoved[PLAYER2] = copied.playersHaveRemoved[PLAYER2];
		return;
	}

	// Generates and returns an int array representing the initial board 
	public int[][] setupInitialBoard(){
		int[][] board = new int[BOARD_SIZE][BOARD_SIZE];
		for (int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				int parity = (i + j)%2;
				board[i][j] = parity;
			}
		}
		return board; 
	}

	// Returns true if the current player cannot make a move 
	public boolean isTerminal(){
		return getPossibleMoves().size() == 0;
	}

	// Returns an arraylist containing all possible moves from this gamestate
	public ArrayList<Move> getPossibleMoves(){
		ArrayList<Move> possibleMoves = new ArrayList<Move>();

		// If we have not removed a piece yet, we must remove a piece
		if(!playersHaveRemoved[turn]){
			// Iterate over our pieces 
			for(int i=0; i<BOARD_SIZE; i++){
				for(int j=0; j<BOARD_SIZE; j++){
					if(board[i][j] == turn){
						possibleMoves.add(new Move(new int[]{i,j}, turn));
					}
				}
			}
			return possibleMoves;
		}

		// Iterate over locations on board to find our pieces
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				// Our piece at i,j
				if(board[i][j] == turn){
					//Pick a direction
					for(int k=0; k<NUM_DIRECTIONS; k++){
						int[] direction = DIRECTIONS[k];

						// Construct single-step move in that direction 
						ArrayList<int[]> coordinates = new ArrayList<int[]>();
						coordinates.add(new int[]{i,j});
						int[] destination = applyDirection(new int[]{i,j}, direction);
						// Make sure coordinates exist 
						if (coordinatesExist(destination)){
							coordinates.add(destination);
							
							// Is that move valid?
							Move moveToTest = new Move(1, coordinates, turn);
							int steps = 2;
							while (isValid(moveToTest)){
								// If move is valid, add it to the possible moves 
								possibleMoves.add(moveToTest);

								// .. and construct a new move to test by taking another step in the same direction
								destination = applyDirection(destination, direction);
								if(!coordinatesExist(destination)){
									break;
								}
								coordinates = (ArrayList<int[]>)coordinates.clone();
								coordinates.add(destination);
								moveToTest = new Move(steps, coordinates, turn);
								steps++; 
							}
						}
					}
				}
			}
		}
		return possibleMoves;  
	}

	// Returns true if the move m can be made in the current gamestate 
	public boolean isValid(Move m){
		// Player must match current turn
		if (m.player() != turn){
			if(verbose){
				System.out.println("Move rejected: wrong player");
			}
			return false;
		}
		// If player has not yet removed a piece, they must remove a piece 
		if (!playersHaveRemoved[turn] && !m.isRemoval()){
			if(verbose){
				System.out.println("Move rejected: player must remove a piece first");
			}			
			return false; 
		}
		if(playersHaveRemoved[turn] && m.isRemoval()){
			if(verbose){
				System.out.println("Move rejected: player cannot remove multiple pieces");
			}			
			return false; 			
		}
		// Initial coordinates must be on board 
		if (!coordinatesExist(m.startCoordinates())){
			if(verbose){
				System.out.println("Move rejected: initial coordinates are off board");
			}			
			return false; 
		}
		// Initial coordinates must belong to player's own piece
		if(board[m.startRow()][m.startCol()] != turn){
			if(verbose){
				System.out.println("Move rejected: initial coordinates are not your piece");
			}		
			return false; 
		}

		// If it is a regular non-removal move, determine movement vector
		int[] vector = new int[]{-1,-1};
		if (!m.isRemoval()){
			int[] firstDestination = m.nthStep(1);
			vector[0] = firstDestination[0] - m.startRow();
			vector[1] = firstDestination[1] - m.startCol(); 

			if(!Arrays.equals(vector, NORTH) && !Arrays.equals(vector, EAST) && !Arrays.equals(vector, SOUTH) && !Arrays.equals(vector, WEST)){
				if(verbose){
					System.out.println("Move rejected: vector " + Integer.toString(vector[0]) + "," + Integer.toString(vector[1]) + " is invalid");
				}
				return false; 
			}
		}

		// If there are more coordinates... 
		for(int i=1; i<m.steps()+1; i++){
			int[] ithCoordinate = m.nthStep(i);
			// they must be on the board
			if (!coordinatesExist(ithCoordinate)){
				if(verbose){
					System.out.println("Move rejected: " + Integer.toString(i) + "th step is off the board");
				}		
				return false; 
			}
			// and empty 
			if (board[ithCoordinate[0]][ithCoordinate[1]] != EMPTY){
				if(verbose){
					System.out.println("Move rejected: " + Integer.toString(i) + "th step is not empty");
				}	
				return false;
			}
			// Any steps after the first must be in the same direction
			int[] lastCoordinate = m.nthStep(i-1);
			int[] newVector = new int[2];
			int[] between = new int[2];
			newVector[0] = ithCoordinate[0] - lastCoordinate[0];
			newVector[1] = ithCoordinate[1] - lastCoordinate[1];
			between[0] = (ithCoordinate[0] + lastCoordinate[0])/2;
			between[1] = (ithCoordinate[1] + lastCoordinate[1])/2;
			if(!Arrays.equals(newVector, vector)){
				if(verbose){
					System.out.println("Move rejected: " + Integer.toString(i) + "th step is in wrong direction");
				}	
				return false; 
			}

			// and the spaces between them must contain enemy pieces
			if(board[between[0]][between[1]] != OPPOSITE_PLAYER[turn]){
				if(verbose){
					System.out.println("Move rejected: " + Integer.toString(i) + "th step does not have an enemy piece between");
				}	
				return false; 
			}
		}
		return true;
	}

	// If the move m is valid, return a gamestate representing the result of applying that move to this state
	// Otherwise return null 
	public GameState applyMove(Move m){
		if(!isValid(m)){
			return null; 
		}

		// make a copy
		GameState result = new GameState(this);

		// If it is a removal, remove piece & update playersHaveRemoved 
		if(m.isRemoval()){
			result.board[m.startRow()][m.startCol()] = EMPTY;
			result.playersHaveRemoved[turn] = true;
		} else {
			// If it is a move(s), move jumping piece
			result.board[m.startRow()][m.startCol()] = EMPTY;
			result.board[m.endRow()][m.endCol()] = turn; 

			// And remove jumped-over pieces
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int i=0; i<jumpedOver.size(); i++){
				result.board[jumpedOver.get(i)[0]][jumpedOver.get(i)[1]] = EMPTY;
			}
		}

		// Change turn 
		result.turn = OPPOSITE_PLAYER[result.turn];

		return result;
	}

	// Static utility function: 
	// Returns the result of applying one step in the provided direction to the provided coordinates 
	public static int[] applyDirection(int[] coordinates, int[] direction){
		return new int[]{coordinates[0] + direction[0], coordinates[1] + direction[1]};
	}

	// Static utility function: 
	// Returns true if coordinates are within the board 
	public static boolean coordinatesExist(int[] coordinates){
		return (coordinates[0] >= 0 && coordinates[0] < BOARD_SIZE && coordinates[1] >= 0 && coordinates[1] < BOARD_SIZE);
	}

	// Generates a string representation of the board
	public String displayBoard(){
		String returnString = "\t1 2 3 4 5 6 7 8\n\n";
		for (int i=0; i<BOARD_SIZE; i++){
			returnString += Integer.toString(i+1) + "\t";
			for(int j=0; j<BOARD_SIZE; j++){
				returnString += PLAYER_SYMBOL[board[i][j]] + " ";
			}
			returnString+="\n";
		}
		return returnString;
	}

	// Generates a string representation of the game state 
	public String toString(){
		String returnString = "";
		returnString += "Turn: " + PLAYER_SYMBOL[turn] + "\n";
		returnString += displayBoard();
		return returnString;
	}

	public static void main(String[] args){
		
		GameState g = new GameState();
		System.out.println(g.toString());

		GameState currentState = g;
		while (!currentState.isTerminal()){
			System.out.println(currentState.getPossibleMoves());
			currentState = currentState.applyMove(currentState.getPossibleMoves().get(0));
			System.out.println(currentState.toString());
		}
		return;
	}
}