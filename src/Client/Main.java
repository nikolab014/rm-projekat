package Client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
 
public class Main extends Application {
	
	static int SERVER_PORT = 1234;
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	private InetAddress addr;
	private int ready_count = 0;
	private char[] board = new char[100];
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	for(int i =0; i<100;i++) {
    		board[i]='0';
    	}
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/Main.fxml"));
        VBox vbox = loader.<VBox>load();
        Scene scene = new Scene(vbox);
        Button contBtn = (Button) scene.lookup("#ContinueBttn");
        contBtn.setOnMouseClicked(e -> {
        	try {
				FXMLLoader loader1 = new FXMLLoader();
				loader1.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/Wait.fxml"));
				primaryStage.setScene(new Scene(loader1.<VBox>load()));
				this.setup();
				Runnable runnable = () -> {
					try {
						String instruction = in.readLine();
						if(instruction.equals("START")) {
							Platform.runLater(() -> {
								try {
									FXMLLoader loader2 = new FXMLLoader();
									loader2.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/ShipPlacement.fxml"));
									primaryStage.setScene(new Scene(loader2.<VBox>load()));
									placement(primaryStage);
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							});
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} 
				};
				Thread t = new Thread(runnable);
				t.start();
			} catch (IOException e1) {
				e1.printStackTrace();
			}	
        });
        primaryStage.setScene(scene);
        primaryStage.show();
        
        	
    }

    
    private void setup() {
    	try {
    		this.addr = InetAddress.getByName("127.0.0.1");
    		this.sock = new Socket(addr, SERVER_PORT);
    		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
    		this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())), true);
    	}catch (Exception e) {
    		System.exit(-1);
    	}
    }
    
    private void placement(Stage stage) {
    	ArrayList<Button> usedSpots = new ArrayList<Button>();
    	Scene scene = stage.getScene();
    	Button ready = (Button) scene.lookup("#ReadyBttn");
    	Button len5 = (Button) scene.lookup("#bttn5");
    	Button len4 = (Button) scene.lookup("#bttn4");
    	Button len3 = (Button) scene.lookup("#bttn3");
    	Button len2 = (Button) scene.lookup("#bttn2");
    	Button len1 = (Button) scene.lookup("#bttn1");
    	
    	this.disableAllButtons(scene);
    	len5.setOnMouseClicked(e -> {
    		len5.setDisable(true);
    		placementIncrement(4, scene, usedSpots);
    		ready_count++;
    	});
    	len4.setOnMouseClicked(e -> {
    		len4.setDisable(true);
    		placementIncrement(3, scene, usedSpots);
    		ready_count++;
    	});
    	len3.setOnMouseClicked(e -> {
    		len3.setDisable(true);
    		placementIncrement(2, scene, usedSpots);
    		ready_count++;
    	});
    	len2.setOnMouseClicked(e -> {
    		len2.setDisable(true);
    		placementIncrement(1, scene, usedSpots);
    		ready_count++;
    	});
    	len1.setOnMouseClicked(e -> {
    		len1.setDisable(true);
    		placementIncrement(0, scene, usedSpots);
    		ready_count++;
    	});
    	
    	ready.setOnMouseClicked(e -> {
    		if(ready_count==5) {
    			System.out.println(new String(board));
    			this.out.println(new String(board));
    			ready.setDisable(true);
    			
    			Runnable runnable =
				        () -> {
							try {
								String response = this.in.readLine();
								System.out.println(response);
				    			if(response.equals("STARTGAME")) {
				    				Platform.runLater(() ->{
				    					try {
				    						FXMLLoader loader4 = new FXMLLoader();
				    						loader4.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/Game.fxml"));
				    						stage.setScene(new Scene(loader4.<VBox>load()));
				    						this.gameplay(stage);
				    					} catch (Exception e2) {
				    						e2.printStackTrace();
				    					}
				    				});
				    			}else if(response.equals("WIN")) {
				    				Platform.runLater(()->{
			    						try {
			    							FXMLLoader loader = new FXMLLoader();
			    							loader.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/End.fxml"));
			    							stage.setScene(new Scene(loader.<VBox>load()));
			    							Scene scene1 = stage.getScene();
			    							Label label = (Label) scene1.lookup("#labelaWL");
			    							label.setText("YOU WIN!");
			    							Button bttnQuit = (Button) scene1.lookup("#Quitbttn");
			    							bttnQuit.setOnMouseClicked(e2->{
			    								System.exit(0);
			    							});
			    						} catch (Exception e3) {
			    							e3.printStackTrace();
			    						}
			    					});
				    			}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
				        	
				        };
				Thread t = new Thread(runnable);
				t.start();
    		}
    	});
    			
    }
    
    private void placementIncrement(int k, Scene scene, ArrayList<Button> usedSpots) {
    	this.disableAllButtons(scene);
		if(k==0) {
			for(int i = 0; i <=9; i++) {
				for(int j = 0; j<=9; j++) {
					Button bttn = (Button) scene.lookup("#bttn"+Integer.toString(i) + Integer.toString(j));
					if(!usedSpots.contains(bttn)) {
						bttn.setDisable(false);
						bttn.setOnMouseClicked(e->{
							usedSpots.add(bttn);
							char[] pom = bttn.getId().toCharArray();
							int x = Character.getNumericValue(pom[4]);
							int y = Character.getNumericValue(pom[5]);
							this.board[x*10+y]='2';
							bttn.setStyle("-fx-background-color: #24c7b7");
							this.disableAllButtons(scene);
						});
					}
				}
			}
			
			return;
		}
    	for(int i = 0; i <=9; i++) {
			for(int j = 0; j<=9; j++) {
				ArrayList<Button> plausibleButtons = new ArrayList<Button>();
				Button bttn = (Button) scene.lookup("#bttn"+Integer.toString(i) + Integer.toString(j));
				boolean enableButton = false;
				if(i-k>=0) {
					Button bttnCheck = null;
					boolean flag = true;
					for(int n = 0; n<=k; n++) {
						bttnCheck = (Button) scene.lookup("#bttn"+Integer.toString(i-n) + Integer.toString(j));
						if(usedSpots.contains(bttnCheck)) {
							flag = false;
							break;
						}
					}
					if(flag) {
						plausibleButtons.add(bttnCheck);
						enableButton = true;
					}
				}
				if(i+k<=9) {
					Button bttnCheck = null;
					boolean flag = true;
					for(int n = 0; n<=k; n++) {
						bttnCheck = (Button) scene.lookup("#bttn"+Integer.toString(i+n) + Integer.toString(j));
						if(usedSpots.contains(bttnCheck)) {
							flag = false;
							break;
						}
					}
					if(flag) {
						plausibleButtons.add(bttnCheck);
						enableButton = true;
					}
				}
				if(j-k>=0) {
					Button bttnCheck = null;
					boolean flag = true;
					for(int n = 0; n<=k; n++) {
						bttnCheck = (Button) scene.lookup("#bttn"+Integer.toString(i) + Integer.toString(j-n));
						if(usedSpots.contains(bttnCheck)) {
							flag = false;
							break;
						}
					}
					if(flag) {
						plausibleButtons.add(bttnCheck);
						enableButton = true;
					}
				}
				if(j+k<=9) {
					Button bttnCheck = null;;
					boolean flag = true;
					for(int n = 0; n<=k; n++) {
						bttnCheck = (Button) scene.lookup("#bttn"+Integer.toString(i) + Integer.toString(j+n));
						if(usedSpots.contains(bttnCheck)) {
							flag = false;
							break;
						}
					}
					if(flag) {
						plausibleButtons.add(bttnCheck);
						enableButton = true;
					}
				}
				if(enableButton) {
					bttn.setDisable(false);
					bttn.setOnMouseClicked(e -> {
						this.disableAllButtons(scene);
						for(Button b : plausibleButtons) {
							b.setDisable(false);
							b.setOnMouseClicked(e3 -> {
								char[] pom = bttn.getId().toCharArray();
								int x = Character.getNumericValue(pom[4]);
								int y =  Character.getNumericValue(pom[5]);
								char[] pom2 = b.getId().toCharArray();
								int x2 = Character.getNumericValue(pom2[4]);
								int y2 =  Character.getNumericValue(pom2[5]);
								
								if(x==x2) {
									if(y<y2) {
										for(int n = 0; n<k+1;n++) {
											Button bp = (Button) scene.lookup("#bttn"+Integer.toString(x) + Integer.toString(y+n));
											this.board[x*10+y+n]='2';
											usedSpots.add(bp);
											bp.setStyle("-fx-background-color: #24c7b7");
										}
									}
									else {
										for(int n = 0; n<k+1; n++) {
											Button bp = (Button) scene.lookup("#bttn"+Integer.toString(x) + Integer.toString(y2+n));
											this.board[x*10+y2+n]='2';
											usedSpots.add(bp);
											bp.setStyle("-fx-background-color: #24c7b7");
										}
									}
								}
								else {
									if(x<x2) {
										for(int n = 0; n<k+1;n++) {
											Button bp = (Button) scene.lookup("#bttn"+Integer.toString(x+n) + Integer.toString(y));
											this.board[(x+n)*10+y]='2';
											usedSpots.add(bp);
											bp.setStyle("-fx-background-color: #24c7b7");
										}
									}
									else {
										for(int n = 0; n<k+1; n++) {
											Button bp = (Button) scene.lookup("#bttn"+Integer.toString(x2+n) + Integer.toString(y));
											this.board[(x2+n)*10+y]='2';
											usedSpots.add(bp);
											bp.setStyle("-fx-background-color: #24c7b7");
										}
									}
								}
								this.disableAllButtons(scene);
							});
							
						}
					});
				}
			}
		}  	
    }

    private void gameplay(Stage stage) {
    	
    	Runnable runnable = () -> {
    		while(true) {
    			try {
    				String respCode = this.in.readLine();
    				if(respCode.startsWith("R3")) {
    					int x = Character.getNumericValue(respCode.toCharArray()[2]);
    					int y = Character.getNumericValue(respCode.toCharArray()[3]);
    					Platform.runLater(()->{
    						Scene scene = stage.getScene();
    						Button bttnHit = (Button) scene.lookup("#Hbttn"+Integer.toString(x)+Integer.toString(y));
    						bttnHit.setStyle("-fx-background-color: #ff0000");
    						bttnHit.setDisable(true);
    					});
    				}else if(respCode.startsWith("R1")) {
    					int x = Character.getNumericValue(respCode.toCharArray()[2]);
    					int y = Character.getNumericValue(respCode.toCharArray()[3]);
    					Platform.runLater(()->{
    						Scene scene = stage.getScene();
    						Button bttnHit = (Button) scene.lookup("#Hbttn"+Integer.toString(x)+Integer.toString(y));
    						bttnHit.setStyle("-fx-background-color: #0000ff");
    						bttnHit.setDisable(true);
    					});
    				}else if(respCode.startsWith("H")) {
    					int x = Character.getNumericValue(respCode.toCharArray()[1]);
    					int y = Character.getNumericValue(respCode.toCharArray()[2]);
    					Platform.runLater(()->{
    						Scene scene = stage.getScene();
    						Button bttnHit = (Button) scene.lookup("#Pbttn"+Integer.toString(x)+Integer.toString(y));
    						if(board[x*10+y]=='2') {
    							bttnHit.setStyle("-fx-background-color: #ff0000");
    						}else {
    							bttnHit.setStyle("-fx-background-color: #0000ff");
    						}
    					});
    				}else if(respCode.equals("WIN")) {
    					Platform.runLater(()->{
    						try {
    							FXMLLoader loader = new FXMLLoader();
    							loader.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/End.fxml"));
    							stage.setScene(new Scene(loader.<VBox>load()));
    							Scene scene = stage.getScene();
    							Label label = (Label) scene.lookup("#labelaWL");
    							label.setText("YOU WIN!");
    							Button bttnQuit = (Button) scene.lookup("#Quitbttn");
    							bttnQuit.setOnMouseClicked(e->{
    								System.exit(0);
    							});
    						} catch (Exception e) {
    							e.printStackTrace();
    						}
    					});
    				}else if(respCode.equals("LOSS")) {
    					Platform.runLater(()->{
    						try {
    							FXMLLoader loader = new FXMLLoader();
    							loader.setLocation(new URL("file:///C:/Users/nikol/eclipse-workspace/Battleship/src/resources/fxml/End.fxml"));
    							stage.setScene(new Scene(loader.<VBox>load()));
    							Scene scene = stage.getScene();
    							Label label = (Label) scene.lookup("#labelaWL");
    							label.setText("YOU LOSE!");
    							Button bttnQuit = (Button) scene.lookup("#Quitbttn");
    							bttnQuit.setOnMouseClicked(e->{
    								System.exit(0);
    							});
    						} catch (Exception e) {
    							e.printStackTrace();
    						}
    					});
    				}
    			}catch (Exception e) {
    				e.printStackTrace();	
    			}
    		}
    	};
    	
		Thread t = new Thread(runnable);
		t.start();
    	
    	Scene scene = stage.getScene();
    	for(int i = 0; i < 100; i++) {
    		if(board[i] == '2') {
    			Button bttn = (Button) scene.lookup("#Pbttn"+Integer.toString(i/10)+Integer.toString(i%10));
    			bttn.setStyle("-fx-background-color: #24c7b7");
    		}
    		Button bttn = (Button) scene.lookup("#Hbttn"+Integer.toString(i/10)+Integer.toString(i%10));
    		bttn.setDisable(false);
    		bttn.setOnMouseClicked(e -> {
    			this.out.println(bttn.getId().substring(5));
    			System.out.println(bttn.getId().substring(5));
    		});
    		Button quitBttn = (Button) scene.lookup("#QuitBttn");
    		quitBttn.setOnMouseClicked(e -> {
    			this.out.println("QUIT");
    		});
    	}
    }
    

    private void disableAllButtons(Scene scene) {
    	for(int i = 0; i < 10; i++)
    		for(int j = 0; j < 10; j++) {
    			Button bttn = (Button) scene.lookup("#bttn"+Integer.toString(i) + Integer.toString(j));
    			bttn.setDisable(true);
    		}
    }
    
}