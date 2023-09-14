package Server;

import java.io.IOException;
import java.net.Socket;

public class BattleshipPlayer extends SocketAction implements Runnable{

	private BattleshipDaemon battleshipServer = null;
	public int [][] Board = new int[10][10];
	public boolean readyStatus = false;
	public BattleshipPlayer opponent = null;
	private volatile boolean wait;
	
	public BattleshipPlayer(BattleshipDaemon server, Socket clientSocket) throws IOException {
		super(clientSocket);
		battleshipServer = server;
		
	}

	public void run() {
		BattleshipGame game = battleshipServer.waitForGame(this);
		this.send("START");
		this.initBoard();
		
		wait = this.readyStatus && this.opponent.readyStatus;
		while(!wait) {
			wait = this.readyStatus && this.opponent.readyStatus;
		}
		this.send("STARTGAME");
		
		while (true) {
			if(this.exit)
				break;
			try {
				String instruction = this.receive();
				game.validInstruction(instruction, this);
			} catch (IOException e) {
				this.closeConnections();
			}
		}
	}

	private void initBoard() {
		try {
			String board = this.receive();
			char[] array = board.toCharArray();
			for(int i = 0; i < 100; i++) {
				this.Board[i/10][i%10] = Character.getNumericValue(array[i]);	
			}
			this.readyStatus = true;
			System.out.println(board); //Remove later
		} catch (IOException e) {
			this.readyStatus = true;
			this.opponent.send("WIN");
		}
	}
	
	
}
