// GameState.java 
// Represents the state of the board / game 

import java.util.ArrayList;
import java.util.Arrays;

class GameState{

	final static public int[][] initialBoard = {{0,1,0,1,0,1,0,1},{1,0,1,0,1,0,1,0},{0,1,0,1,0,1,0,1},{1,0,1,2,2,0,1,0},{0,1,0,1,0,1,0,1},{1,0,1,0,1,0,1,0},{0,1,0,1,0,1,0,1},{1,0,1,0,1,0,1,0}};

	final static public int PLAYER1 = 0;
	final static public int PLAYER2 = 1;
	final static public char[] PLAYER_SYMBOL = {'X', 'O', '.'};
	final static public int[] OPPOSITE_PLAYER = {PLAYER2, PLAYER1};
	final int EMPTY = 2;
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

	// SETUP 

	// A constructor that generates an initial game state 
	public GameState(){
		board = setupInitialBoard();
		turn = PLAYER1; 
		playersHaveRemoved = new boolean[2];
		playersHaveRemoved[PLAYER1] = false;
		playersHaveRemoved[PLAYER2] = false;
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

	// CORE METHODS 

	// Returns an arraylist containing all possible moves from this gamestate
	public ArrayList<Move> getPossibleMoves(){
		ArrayList<Move> possibleMoves = new ArrayList<Move>();

		// If we have not removed a piece yet, we must return a piece
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
								System.out.println(Arrays.toString(coordinates.get(0)) + " " + Arrays.toString(coordinates.get(1)));
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

	// Determines whether a move is valid in the current gamestate
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
		// Initial coordinates must be on board 
		if (m.startRow() < 0 || m.startRow() >= BOARD_SIZE || m.startCol() < 0 || m.startCol() >= BOARD_SIZE){
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
			if (ithCoordinate[0] < 0 || ithCoordinate[0] >= BOARD_SIZE || ithCoordinate[1] < 0 || ithCoordinate[1] >= BOARD_SIZE){
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

	// ACCESSORS AND MODIFIERS 

	public void setBoard(int[][] _board){
		board = _board;
	}

	public void setRemoved(boolean[] _removed){
		playersHaveRemoved = _removed;
	}

	public void setTurn(int _turn){
		turn = _turn;
	}

	// STATIC UTILITY METHODS 

	public static int[] applyDirection(int[] coordinates, int[] direction){
		return new int[]{coordinates[0] + direction[0], coordinates[1] + direction[1]};
	}

	public static boolean coordinatesExist(int[] coordinates){
		return (coordinates[0] >= 0 && coordinates[0] < BOARD_SIZE && coordinates[1] >= 0 && coordinates[1] < BOARD_SIZE);
	}

	// DEBUGGING 

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

	public static void main(String[] args){
		
		GameState g = new GameState();
		System.out.println(g.displayBoard());
		
		//System.out.println(g.getPossibleMoves());

		/*
		int[] coordinates = new int[]{4,4};
		
		Move removalMove = new Move(coordinates, GameState.PLAYER1);
		System.out.println(removalMove);
		*/

		g.setBoard(initialBoard);
		g.setRemoved(new boolean[]{true, true});
		System.out.println(g.displayBoard());
		System.out.println(g.getPossibleMoves());

		g.setBoard(new int[][]{{0,1,0,1,0,1,0,1},{1,0,2,0,1,0,1,0},{0,1,0,1,0,1,0,1},{1,0,1,2,1,2,1,2},{0,1,0,1,0,1,0,1},{1,0,1,0,1,0,1,0},{0,1,0,1,0,1,0,1},{1,0,1,0,1,0,1,0}});
		System.out.println(g.displayBoard());
		System.out.println(g.getPossibleMoves());

		g.setTurn(PLAYER2);
		System.out.println(g.displayBoard());
		System.out.println(g.getPossibleMoves());
		/*

		int[] startCoordinates = new int[]{5,3};
		int[] destCoordinates = new int[]{3,3};
		ArrayList<int[]> coords = new ArrayList<int[]>(2);
		coords.add(startCoordinates);
		coords.add(destCoordinates);
		Move normalMove = new Move(1, coords, GameState.PLAYER1);

		System.out.println(g.isValid(normalMove));

		Move wrongPlayer = new Move(1, coords, GameState.PLAYER2);
		System.out.println(g.isValid(wrongPlayer));

		int[] altDestCoordinates = new int[]{4,4};
		coords.set(1, altDestCoordinates);
		Move diagonal = new Move(1, coords, GameState.PLAYER1);
		System.out.println(g.isValid(diagonal));
		*/ 
		/*

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

		System.out.println(g.isValid(removalMove));
		System.out.println(g.isValid(removalMove2));
		System.out.println(g.isValid(normalMove));
		System.out.println(g.isValid(longMove));

		*/ 

		return;
	}



}