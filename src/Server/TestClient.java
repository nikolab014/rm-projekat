package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class TestClient {
	static int SERVER_PORT = 1234;

	public static void main(String[] args) throws IOException {

		InetAddress addr = InetAddress.getByName("127.0.0.1");
		Socket sock = new Socket(addr, SERVER_PORT);
		BufferedReader in = new BufferedReader(
								new InputStreamReader(
									sock.getInputStream()));
		PrintWriter out = new PrintWriter(
							new BufferedWriter(
								new OutputStreamWriter(
									sock.getOutputStream())), true);

		String data = "0123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";
		out.println(data);
		while(true) {
			Scanner scan = new Scanner(System.in);
			data = scan.nextLine();
			out.println(data);
		}
		//String response = in.readLine();
		//try {
		//	int c = Integer.parseInt(response);
		//	System.out.println("Suma unesenih brojeva je " + response);
		//}catch(Exception e) {
		//	System.out.println(response);
		//	System.out.println("Sumu unesenih brojeva nije moguce izracunati");
		//}
		//in.close();
		//.close();
		//sock.close();
	}

}