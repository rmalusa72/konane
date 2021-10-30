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

	// Return winner
	public int winner(){
		if(isTerminal()){
			return OPPOSITE_PLAYER[turn];
		}
		return EMPTY; 
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

	// Returns a list of moves the non-current player could make, were it their turn
	public ArrayList<Move> getPossibleOtherMoves(){
		turn = OPPOSITE_PLAYER[turn];
		ArrayList<Move> enemyMoves = getPossibleMoves();
		turn = OPPOSITE_PLAYER[turn];
		return enemyMoves;
	}

	// Given a list of moves derived from PossibleMoves, returns a pair of boolean arrays: 
	// pieceInfo[0], endangered, is TRUE for pieces that can be jumped over, FALSE for pieces that cannot 
	//    (false in empty spaces)
	// pieceInfo[1], movable, is TRUE for pieces that have moves, FALSE for pieces that do not 
	//    (false in empty spaces)
	public boolean[][][] pieceInfo(){
		
		// Make combined move list 
		ArrayList<Move> moves = getPossibleMoves();
		ArrayList<Move> enemyMoves = getPossibleMoves();

		return pieceInfo(moves, enemyMoves);

	}

	// As above, but can be provided with moves already generated isntead of re-calculating them
	public boolean[][][] pieceInfo(ArrayList<Move> _ourMoves, ArrayList<Move> _enemyMoves){
		
		boolean[][] endangered = new boolean[BOARD_SIZE][BOARD_SIZE];
		boolean[][] movable = new boolean[BOARD_SIZE][BOARD_SIZE];

		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				endangered[i][j] = false;
				movable[i][j] = false;
			}
		}

		// Make combined move list 
		ArrayList<Move> moves = new ArrayList<Move>(64);
		for(int i=0; i<_ourMoves.size(); i++){
			moves.add(_ourMoves.get(i));
		}
		for(int i=0; i<_enemyMoves.size();i++){
			moves.add(_enemyMoves.get(i));
		}

		for(int i=0; i<moves.size(); i++){
			
			Move m = moves.get(i);

			// Set start of move to movable 
			movable[m.startRow()][m.startCol()] = true;

			// Set jumped-over pieces to endangered
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				endangered[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]] = true;
			}

		}

		return new boolean[][][]{endangered, movable};
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
		result.applyMoveInPlace(m);

		return result;
	}

	// If the move m is valid, apply it to the current gamestate (without generating a new one)
	// and return true. Otherwise return false 
	public boolean applyMoveInPlace(Move m){
		if(!isValid(m)){
			return false;
		}
		// If it is a removal, remove piece & update playersHaveRemoved 
		if(m.isRemoval()){
			board[m.startRow()][m.startCol()] = EMPTY;
			playersHaveRemoved[turn] = true;
		} else {
			// If it is a move(s), move jumping piece
			board[m.startRow()][m.startCol()] = EMPTY;
			board[m.endRow()][m.endCol()] = turn; 

			// And remove jumped-over pieces
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int i=0; i<jumpedOver.size(); i++){
				board[jumpedOver.get(i)[0]][jumpedOver.get(i)[1]] = EMPTY;
			}
		}
		// Change turn 
		turn = OPPOSITE_PLAYER[turn];

		return true; 
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

	// Return current turn
	public int turn(){
		return turn;
	}

	// Returns num pieces possessed by a certain player 
	public int numPieces(int player){
		int count = 0;
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				if(board[i][j] == player){
					count++;
				}
			}
		}
		return count;
	}

	// Return num moves 
	public int numMoves(int player){
		if(player==turn){
			return getPossibleMoves().size();
		} else {
			return getPossibleOtherMoves().size();
		}
	}

	// Returns an array containing the requested player's safe moves count in entry 0 and
	// the other player's safe moves count in entry 1
	public int[] numSafeMoves(int player){
		return numSafeMoves(player, getPossibleMoves(), getPossibleOtherMoves());
	}

	// As above, but with moves pre-calculated 
	public int[] numSafeMoves(int player, ArrayList<Move> _moves, ArrayList<Move> _enemyMoves){
		
		int[] safeMoveCount = new int[]{0,0};

		// Get move lists
		ArrayList<Move> moves = _moves;
		ArrayList<Move> enemyMoves = _enemyMoves;

		boolean[][][] pieceInfo = pieceInfo(moves, enemyMoves);
		boolean[][] endangered = pieceInfo[0];
		boolean[][] movable = pieceInfo[1];

		// Increment safe move count[0] by 1 for each move we have 
		// where start piece is not endangered and jumped over pieces cannot move
		for(int i=0; i<moves.size(); i++){
			Move m = moves.get(i);
			if(endangered[m.startRow()][m.startCol()]){
				continue;
			}
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				if(movable[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]]){
					continue;
				}
			}
			safeMoveCount[0]++;
		}

		// Increment safe move count[0] by 1 for each move we have 
		// where start piece is not endangered and jumped over pieces cannot move
		for(int i=0; i<enemyMoves.size(); i++){
			Move m = enemyMoves.get(i);
			if(endangered[m.startRow()][m.startCol()]){
				continue;
			}
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				if(movable[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]]){
					continue;
				}
			}
			safeMoveCount[1]++;
		}

		if(turn == player){
			return safeMoveCount;
		} else {
			return new int[]{safeMoveCount[1], safeMoveCount[0]};
		}		
	}



	// Returns an array containing number of the requested player's pieces which can 
	// move, are not endangered, and which can move over pieces which cannot move 
	// Ditto for the other player's in entry 1
	public int[] numSafeSquares(int player){
		return numSafeSquares(player, getPossibleMoves(), getPossibleOtherMoves());
	}

	// As above, but with moves precomputed 
	public int[] numSafeSquares(int player, ArrayList<Move> _moves, ArrayList<Move> _enemyMoves){
		// Get move lists
		ArrayList<Move> moves = _moves;
		ArrayList<Move> enemyMoves = _enemyMoves;

		boolean[][][] pieceInfo = pieceInfo(moves, enemyMoves);
		boolean[][] endangered = pieceInfo[0];
		boolean[][] movable = pieceInfo[1];

		int[][] safeMoveAt = new int[BOARD_SIZE][BOARD_SIZE];
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				safeMoveAt[i][j] = EMPTY;
			}
		}

		// Increment safe move count[0] by 1 for each move we have 
		// where start piece is not endangered and jumped over pieces cannot move
		for(int i=0; i<moves.size(); i++){
			Move m = moves.get(i);
			if(endangered[m.startRow()][m.startCol()]){
				continue;
			}
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				if(movable[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]]){
					continue;
				}
			}
			safeMoveAt[m.startRow()][m.startCol()] = turn; 
		}


		// Increment safe move count[0] by 1 for each move we have 
		// where start piece is not endangered and jumped over pieces cannot move
		for(int i=0; i<enemyMoves.size(); i++){
			Move m = enemyMoves.get(i);
			if(endangered[m.startRow()][m.startCol()]){
				continue;
			}
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				if(movable[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]]){
					continue;
				}
			}
			safeMoveAt[m.startRow()][m.startCol()] = OPPOSITE_PLAYER[turn]; 
		}

		int[] safeMoveCount = new int[]{0,0};
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				if(safeMoveAt[i][j] == turn){
					safeMoveCount[0]++;
				}
				if(safeMoveAt[i][j] == OPPOSITE_PLAYER[turn]){
					safeMoveCount[1]++; 
				}
			}
		}

		if(turn == player){
			return safeMoveCount;
		} else {
			return new int[]{safeMoveCount[1], safeMoveCount[0]};
		}
	}


	// Returns an array containing number of the requested player's pieces which can 
	// move over pieces which cannot move 
	// Ditto for the other player's in entry 1
	public int[] numSafeSquares2(int player){
		return numSafeSquares2(player, getPossibleMoves(), getPossibleOtherMoves());
	}

	public int[] numSafeSquares2(int player, ArrayList<Move> _moves, ArrayList<Move> _enemyMoves){
		// Get move lists
		ArrayList<Move> moves = _moves;
		ArrayList<Move> enemyMoves = _enemyMoves;

		boolean[][][] pieceInfo = pieceInfo(moves, enemyMoves);
		boolean[][] endangered = pieceInfo[0];
		boolean[][] movable = pieceInfo[1];

		int[][] safeMoveAt = new int[BOARD_SIZE][BOARD_SIZE];
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				safeMoveAt[i][j] = EMPTY;
			}
		}

		// Increment safe move count[0] by 1 for each move we have 
		// where jumped over pieces cannot move
		for(int i=0; i<moves.size(); i++){
			Move m = moves.get(i);
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				if(movable[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]]){
					continue;
				}
			}
			safeMoveAt[m.startRow()][m.startCol()] = turn; 
		}


		// Increment safe move count[0] by 1 for each move we have 
		// where jumped over pieces cannot move
		for(int i=0; i<enemyMoves.size(); i++){
			Move m = enemyMoves.get(i);
			ArrayList<int[]> jumpedOver = m.jumpedOver();
			for(int j=0; j<jumpedOver.size(); j++){
				if(movable[jumpedOver.get(j)[0]][jumpedOver.get(j)[1]]){
					continue;
				}
			}
			safeMoveAt[m.startRow()][m.startCol()] = OPPOSITE_PLAYER[turn]; 
		}

		int[] safeMoveCount = new int[]{0,0};
		for(int i=0; i<BOARD_SIZE; i++){
			for(int j=0; j<BOARD_SIZE; j++){
				if(safeMoveAt[i][j] == turn){
					safeMoveCount[0]++;
				}
				if(safeMoveAt[i][j] == OPPOSITE_PLAYER[turn]){
					safeMoveCount[1]++; 
				}
			}
		}

		if(turn == player){
			return safeMoveCount;
		} else {
			return new int[]{safeMoveCount[1], safeMoveCount[0]};
		}
	}

	// Return the provided player's evaluation of this gamestate 
	// 2*the move difference + the number of safe moves they have 
	public int complexScore1(int player){
		ArrayList<Move> curMoves = getPossibleMoves();
		ArrayList<Move> nextMoves = getPossibleOtherMoves();
		int[] safeMoves = numSafeMoves(player, curMoves, nextMoves);

		if(player == turn){
			return 2*curMoves.size() - 2*nextMoves.size() + safeMoves[0];
		} else {
			return 2*nextMoves.size() - 2*curMoves.size() + safeMoves[0];
		}
	}

	// Return the provided player's evaluation of this gamestate 
	// 2*the move difference + the number of safe squares they have 
	public int complexScore2(int player){
		ArrayList<Move> curMoves = getPossibleMoves();
		ArrayList<Move> nextMoves = getPossibleOtherMoves();
		int[] safeMoves = numSafeSquares(player, curMoves, nextMoves);

		if(player == turn){
			return 2*curMoves.size() - 2*nextMoves.size() + safeMoves[0];
		} else {
			return 2*nextMoves.size() - 2*curMoves.size() + safeMoves[0];
		}
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

	public String printInfo(){
		String returnString = "";
		boolean[][][] info = pieceInfo();
		returnString += "Endangered\n";
		returnString += "\t1 2 3 4 5 6 7 8\n\n";
		for (int i=0; i<BOARD_SIZE; i++){
			returnString += Integer.toString(i+1) + "\t";
			for(int j=0; j<BOARD_SIZE; j++){
				if(info[0][i][j]){
					returnString +=  "1 ";
				} else {
					returnString +=  "0 ";
				}
				
			}
			returnString+="\n";
		}

		returnString += "Movable\n";
		returnString += "\t1 2 3 4 5 6 7 8\n\n";
		for (int i=0; i<BOARD_SIZE; i++){
			returnString += Integer.toString(i+1) + "\t";
			for(int j=0; j<BOARD_SIZE; j++){
				if(info[1][i][j]){
					returnString +=  "1 ";
				} else {
					returnString +=  "0 ";
				}
				
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
			currentState.applyMoveInPlace(currentState.getPossibleMoves().get(0));
			//currentState = currentState.applyMove(currentState.getPossibleMoves().get(0));
			System.out.println(currentState.toString());
		}
		System.out.println("Winner: " + PLAYER_SYMBOL[currentState.winner()]);
		return;
	}
}