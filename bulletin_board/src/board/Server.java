package board;

import java.net.*; 
import java.io.*;

public class Server { 
	
	static int boardWidth = 0, boardHeight = 0; 
	
	static Note [][][] board = new Note[boardWidth][boardHeight][50]; //A 3-dimensional array, or equivalently, an array of arrays (representing x and y index board) of arrays (possible overlapping notes)

	static String[] colours; 
	static String defaultColour;  
	
	static BufferedReader in;
	static PrintWriter out; 
	
	/**
	 * Main program to run the class Server - Responsible for parsing command line
	 * arguments, setting up socket at port number, building the board (3D array) of
	 * Note objects, and waiting for the socket to accept a connection on the port
	 * number (to which it will open up a new thread once a new connection is
	 * established)
	 * 
	 * @param args - Stores incoming command-line arguments in the form of a String
	 *             array
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
				
		int portNumber = Integer.parseInt(args[0]); 
		defaultColour = args[3]; 
		colours = new String[args.length - 3]; 
		
		// Get all the command-line colours (including the default colour) into an array 
		// of colours of type String 
		int j = 0;
		for (int i = 3; i < args.length; i++) {  
			colours[j] = args[i];
			j++; 
		}

		// Get the board width (array width) and board height (array height) from the 
		// command-line arguments 
		boardWidth = Integer.parseInt(args[1]); 
		boardHeight = Integer.parseInt(args[2]);
		
		// Specify the board's dimensions (array size) and notify the user (who started up the server) that the board has been created 
		board = new Note [boardWidth][boardHeight][50];
		System.out.println("Board has been created."); 
		
		// Create a socket bound to a portNumber (specified in command-line) that the 
		// server will constantly listen to (client must have this same port number if 
		// they are to connect) 
		ServerSocket serverSocket = new ServerSocket(portNumber); 

		// Notify user that server is running, after all proper command-line parsing is 
		// completed 
		System.out.println("Hello there, server here is now running and listening on port " + portNumber + "!\n");
		
		// For debugging, making sure all the input was received 
		System.out.println("It's the server here again, making sure this is your input: "); 
		System.out.println("Port number: " + portNumber); 
		System.out.println("Board width: " + boardWidth + ", Board height: " + boardHeight); 
		System.out.println("Default colour: " + defaultColour); 
		
		// Print a list of colours 
		System.out.println("Colours: ");
		for (int i = 0; i < colours.length; i++) {
			System.out.println(colours[i]);
		}
		
		try {
			while (true) { 
				new ServerThread(serverSocket.accept()).start();  
				
			} 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		serverSocket.close();  
	
	} 
	
	/**
	 * Define a class ServerThread responsible for handling thread implementation
	 * @author Abel Worku
	 *
	 */
	private static class ServerThread extends Thread {  
		
		private Socket socket; 
		
		/**
		 * - Constructor to define object which is an instance of class ServerThread, which implements the threading for the server: an object here represents a thread, and the thread's operation is defined in run() 
		 * - Create a ServerThread object, a "thread" for the server, with its attribute as a socket (so that it knows the connection to the client to which it sends to and receives stuff from) 
		 * @param socket 
		 */
		public ServerThread(Socket socket) { 
			this.socket = socket; 
		}
		
		/**
		 * Define the thread's operation
		 */
		public void run() {  
			try {
				//Create stream objects for sending data to the socket (to be sent to the client), and receiving data from the socket (to be read from the client) 
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);

				//Give client a list of available colours to use. 
				String coloursJoined = String.join(", ", colours);
				out.printf("Your available colours for note-making are %s. And your default colour is %s.\n", coloursJoined, defaultColour); //Newline \n added here because printf does not automatically add one like println 
				
				//Give client the board dimensions 
				out.println("The dimensions of the board are: (Width) " + boardWidth + ", by (Height) " + boardHeight); 
				
				//Constantly running loop which services the client until they stop connection to the server 
				while (true) { 					
					String input = in.readLine(); //Read from the socket using the BufferedReader stream object "in". It will wait here until new, not-previous input is received from the socket 
					//Input processing goes here, parsing input with if statements that see what the first argument (ie. type of request) will be 
					if (input.equals("Hello! Can you read me?")) { //Test connection for back and forth messaging 
						System.out.println("Client said: " + input); 
						out.println("Hello! Yes I can read you just fine!"); 
					} else if (input.contains("PIN")) {
						processPin(input); 
					} else if (input.contains("UNPIN")) {
						processUnpin(input); 
					} else if (input.contains("POST")) {
						processPost(input); 
					} else if (input.contains("GET")) {
						processGet(input); 
					} else if (input.contains("CLEAR")) {
						processClear(); 
					}
					
				} 
			} catch (IOException e) {
				System.out.println(e.getMessage());
			} catch (Exception e44) {
				System.err.println("Error");
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
			} 
		}
	} 
	
	public static void processGet(String getInput) {
		
		String color, contains, refersTo; 
		String[] split1 = getInput.split("="); 
		
		switch (split1.length) {
			
			case 1: 
				color = split1[0]; 
				for (int row = 0; row < boardWidth; row++) {
					for (int col = 0; col < boardHeight; col++) {
						for (int note = 0; note < 50; note++) {
							if (board[row][col][note] != null && board[row][col][note].getColour().equals(color)) {
								out.printf("Note: (Coordinates) x = %d, y = %d, (Note width) %d, (Note height) %d, (Note colour) %s, (Message) %s\n", board[row][col][note].getX(), board[row][col][note].getY(), board[row][col][note].getWidth(), board[row][col][note].getHeight(), board[row][col][note].getColour(), board[row][col][note].getMessage()); 
							}
						}
					}
				}
				out.println(""); 
				
			case 2: 
				color = split1[0]; 
				contains = split1[1]; 
				for (int row = 0; row < boardWidth; row++) {
					for (int col = 0; col < boardHeight; col++) {
						for (int note = 0; note < 50; note++) {
							if (board[row][col][note] != null && board[row][col][note].getColour().equals(color) && board[row][col][note].getMessage().contains(contains)) {
								out.printf("Note: (Coordinates) x = %d, y = %d, (Note width) %d, (Note height) %d, (Note colour) %s, (Message) %s\n", board[row][col][note].getX(), board[row][col][note].getY(), board[row][col][note].getWidth(), board[row][col][note].getHeight(), board[row][col][note].getColour(), board[row][col][note].getMessage()); 
							}
						}
					}
				}
				out.println(""); 
				
			case 3: 
				color = split1[0];
				contains = split1[1];
				refersTo = split1[2]; 
				for (int row = 0; row < boardWidth; row++) {
					for (int col = 0; col < boardHeight; col++) {
						for (int note = 0; note < 50; note++) {
							if (board[row][col][note] != null && board[row][col][note].getColour().equals(color) && board[row][col][note].getMessage().contains(contains)) {
								out.printf("Note: (Coordinates) x = %d, y = %d, (Note width) %d, (Note height) %d, (Note colour) %s, (Message) %s\n", board[row][col][note].getX(), board[row][col][note].getY(), board[row][col][note].getWidth(), board[row][col][note].getHeight(), board[row][col][note].getColour(), board[row][col][note].getMessage()); 
							}
						}
					}
				}
				out.println(""); 
		}
		
	}
	
	public static void processClear() { 
		//Server must forget all notes which are not pinned 
		
		for (int row = 0; row < boardWidth; row++) {
			for (int col = 0; col < boardHeight; col++) {
				for (int note = 0; note < 50; note++) {
					if (board[row][col][note] != null && board[row][col][note].getPinStatus() == false) {
						board[row][col][note] = null;
					}
				}
			}
		}
		out.println("Server has successfully cleared the board of all notes that were unpinned."); 

	}
	
	public static void processUnpin(String unpinInput) { 

		String[] split1 = unpinInput.split("=");
		String[] split2 = split1[1].split(","); 
		for (String string2 : split2) {
			System.out.println(string2); 
		}
		
		//Parsing input 
		int x = Integer.parseInt(split2[0]); 
		int y = Integer.parseInt(split2[1]); 
		
		for (int i = 0; i < 50; i++) {
			
			if (board[x][y][i] != null) {
				board[x][y][i].setPinStatus(false);
			}
			
		}
		
		out.printf("Server has successfully unpinned all (existing) notes at location %d, %d from the board.\n", x, y); 
		
	}

	
	public static void processPin(String pinInput) { 

		String[] split1 = pinInput.split("=");
		String[] split2 = split1[1].split(","); 
		for (String string : split2) {
			System.out.println(string); 
		}
		
		//Parsing input 
		int x = Integer.parseInt(split2[0]); 
		int y = Integer.parseInt(split2[1]); 
		
		for (int i = 0; i < 50; i++) {
			
			if (board[x][y][i] != null) {
				board[x][y][i].setPinStatus(true);
			}
			
		}
		out.printf("Server has successfully pinned all (existing) notes at location %d, %d onto the board.\n", x, y); 
		
	}

	
	public static void processPost(String postInput) {
		String[] split1 = postInput.split("="); 
		String[] split2 = split1[1].split(","); 
		for (String string : split2) {
			System.out.println(string);
		} 
		
		//Parsing input 
		int x = Integer.parseInt(split2[0]);
		int y = Integer.parseInt(split2[1]); 
		int noteWidth = Integer.parseInt(split2[2]); 
		int noteHeight = Integer.parseInt(split2[3]); 
		String colour = split2[4]; 

		String message = split2[5];
		
		Note note = new Note(x, y, noteWidth, noteHeight, colour, message, false);
		
		//Make sure all input was received 
		System.out.printf("Coordinates: x = %d, y = %d\n", note.getX(), note.getY()); 
		System.out.printf("Note width: %d, Note height: %d\n", note.getWidth(), note.getHeight());
		System.out.printf("Note colour: %s\n", note.getColour());  
		System.out.printf("Message: %s\n", note.getMessage()); 
		
		//Putting note into location in array at indices [x][y][0] 
		board[x][y][0] = note;
		System.out.printf("To check, the colour of the note on the board (not the note in-hand) at location x = %d and y = %d is: %s\n", board[x][y][0].getX(), board[x][y][0].getY(), board[x][y][0].getColour()); 
		out.println("Server has successfully posted the Note onto the board."); 		
	} 
	
	public static class Note {
		private int x, y, width, height;
		private String message, colour; 
		private boolean isPinned; 

		public Note(int x, int y, int width, int height, String colour, String message, boolean isPinned) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.message = message;
			this.colour = colour; 
			this.isPinned = false; 
		} 
		
		public String getColour() {
			return this.colour; 
		}
		
		public boolean getPinStatus() {
			return this.isPinned; 
		}

		public int getX() {
			return this.x;
		}

		public int getY() {
			return this.y;
		}

		public int getWidth() {
			return this.width;
		}

		public int getHeight() {
			return this.height;
		}

		public String getMessage() {
			return this.message;
		}

		public void setX(int x) {
			this.x = x;
		}

		public void setY(int y) {
			this.y = y;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public void setLength(int length) {
			this.height = length;
		}

		public void setMessage(String message) {
			this.message = message;
		} 
		
		public void setPinStatus(boolean pinStatus) {
			this.isPinned = pinStatus; 
		} 
		
		
		
	} 

	
}
