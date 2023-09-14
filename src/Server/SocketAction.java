package Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketAction {
  
  public BufferedReader inStream = null; 
  protected PrintWriter outStream = null;
  protected Socket socket = null;
  public boolean linkgone = false;
  protected boolean exit = false;

  public SocketAction(Socket paramSocket) throws IOException {
    this.socket = paramSocket;
    if (paramSocket != null) {
      this.inStream = new  BufferedReader(new InputStreamReader(paramSocket.getInputStream()));
      this.outStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(paramSocket.getOutputStream())), true);
    }
  }

  public void send(String string) {
    if (this.outStream == null)
      return;
    this.outStream.println(string);
  }

  public String receive() throws IOException {
    String str = null;
    if (this.inStream != null)
      str = this.inStream.readLine();
    return str;
  }

  public void closeConnections() {
    try {
      this.exit = true;
      if (this.inStream != null)
        this.inStream.close();
      if (this.outStream != null)
        this.outStream.close();
      this.inStream = null;
      this.outStream = null;
      if (this.socket != null) {
        this.socket.close();
        this.socket = null;
      }
    }
    catch (Exception localException) {
    	localException.printStackTrace();
    }
    this.linkgone = true;
  }

  public boolean isConnected() {
	
    return (this.inStream != null) && (this.outStream != null) && (this.socket != null);
    
  }

  protected void finalize() {
    if (this.socket != null) {
      try {
        this.socket.close();
      }
      catch (IOException localIOException) {
      }
      this.socket = null;
    }
  }
}