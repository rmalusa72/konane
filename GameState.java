// GameState.java 
// Represents the state of the board / game 

class GameState{

	final static public int PLAYER1 = 0;
	final static public int PLAYER2 = 1;
	final static public char[] PLAYER_SYMBOL = {'X', 'O'};
	final int EMPTY = 2;
	final int BOARD_SIZE = 8;

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

	public 

	public static void main(String[] args){
		
		GameState g = new GameState();
		System.out.println(g.displayBoard());

		return;
	}



}