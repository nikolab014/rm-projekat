package Server;

public class BattleshipGame {
	
	
	private BattleshipPlayer player1 = null;
	private BattleshipPlayer player2 = null;
	public BattleshipPlayer currentPlayer = null;

	public BattleshipGame(BattleshipPlayer playerWaiting, BattleshipPlayer p) {
		
		this.currentPlayer = playerWaiting;
		this.player1 = playerWaiting;
		this.player2 = p;
		this.player1.opponent = this.player2;
		this.player2.opponent = this.player1;
		
	}
	
	public boolean isValid() {
		if(this.player1.isConnected() && this.player2.isConnected())
			return true;
		return false;
	}
	
	//0 voda, 1 voda hit, 2 brod, 3 brod hit
	public void validInstruction(String instruction, BattleshipPlayer p) {
		if(instruction.equals("QUIT")) {
			p.send("LOSS");
			p.opponent.send("WIN");
			p.opponent.closeConnections();
			p.closeConnections();
			return;
		}
		synchronized(this) {
			if(this.currentPlayer == p) {
				int x = Character.getNumericValue(instruction.toCharArray()[0]);
				System.out.println(x);
				int y = Character.getNumericValue(instruction.toCharArray()[1]);
				System.out.println(y);
				int val = this.currentPlayer.opponent.Board[x][y];
				if(val == 0)
					val = 1;
				else if(val == 2)
					val = 3;
				this.currentPlayer.opponent.Board[x][y] = val;
				if(!gameStatus(this.currentPlayer.opponent.Board)) {
					this.currentPlayer.send("WIN");
					this.currentPlayer.opponent.send("LOSS");
					this.currentPlayer.closeConnections();
					this.currentPlayer.opponent.closeConnections();
					return;
				}
				this.currentPlayer.send("R"+Integer.toString(val)+Integer.toString(x)+Integer.toString(y));
				this.currentPlayer.opponent.send("H"+Integer.toString(x)+Integer.toString(y));
				if(this.currentPlayer == this.player1) {
					this.currentPlayer = this.player2;
					System.out.println("P1->P2");
				}
				else {
					this.currentPlayer = this.player1;
					System.out.println("P2->P1");
				}
			} 
		}
	}
	
	private static boolean gameStatus(int[][] board) {
		for(int i = 0; i < 10; i++)
			for(int j = 0; j < 10; j++)
				if(board[i][j] == 2)
					return true;	
		return false;
	}
	

}
