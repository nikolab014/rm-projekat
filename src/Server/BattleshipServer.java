package Server;

public class BattleshipServer {

	public static void main(String[] args) {
		System.out.println("Server up and running...");
	    new BattleshipDaemon().start();
	}

}
