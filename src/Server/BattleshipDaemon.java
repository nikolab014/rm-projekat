package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BattleshipDaemon extends Thread{

	public static final int PORTNUM = 1234;
	private ServerSocket port;
	private BattleshipPlayer playerWaiting = null;
	private BattleshipGame thisGame = null;
	
	public void run() {
		  try {
			port = new ServerSocket(PORTNUM);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		  Socket clientSocket;
		  while (true) {
		    if (port == null) {
		      System.out.println("Sorry, the port disappeared.");
		      System.exit(1);
		    }
		    try {
		      clientSocket = port.accept();
		      new Thread(new BattleshipPlayer(this, clientSocket)).start();
		    }
		    catch (IOException e) {
		      System.out.println("Couldn't connect player: " + e);
		      System.exit(1);
		    }
		  }
		}
	
	public BattleshipGame waitForGame(BattleshipPlayer p){
		  try {
			  sleep(5000);
		  } catch (InterruptedException e1) {
		  }
		  synchronized(this) {
			  BattleshipGame retval = null;
			  if (playerWaiting == null) {
				  playerWaiting = p;
				  thisGame = null;    
				  while (playerWaiting != null) {
					  try {
						  wait();
					  }
					  catch (InterruptedException e) {
						  System.out.println("Error: " + e);
					  }
				  }
				  return thisGame;
			  }
			  else {
				  thisGame = new BattleshipGame(playerWaiting, p);
				  retval = thisGame;
				  playerWaiting = null;
				  notify();
				  return retval;
			  }
		  }
	    }
	
}
