package board;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;
import java.io.*; 

public class Client extends JFrame implements ActionListener {

	Socket socket = new Socket(); 
	
	BufferedReader in;
	PrintWriter out; 
	
	boolean isConnected = false; //A boolean flag used for other buttons to determine connection status of client and prevent connection conflicts, ex. disconnecting with no connection, posting with no connection, etc.  
	
	JTextField ip = new JTextField();
	JTextField portNumber = new JTextField(); 
	JTextField textToServer = new JTextField();
	JTextField result = new JTextField();

	JLabel ip1 = new JLabel("IP Address (Use 'localhost' or '127.0.0.1' for a local connection): "); 
	JLabel portNumber1 = new JLabel("Port Number: ");
	JLabel textToServer1 = new JLabel("Text to send to server: ");
	JLabel result1 = new JLabel("Result: ");

	public Client() {
		setLayout(new GridLayout(8, 2));
		setSize(750, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JButton connect = new JButton("Connect");
		connect.addActionListener(this);
		JButton disconnect = new JButton("Disconnect");
		disconnect.addActionListener(this);
		JButton post = new JButton("Post");
		post.addActionListener(this);
		JButton pin = new JButton("Pin");
		pin.addActionListener(this);
		JButton unpin = new JButton("Unpin");
		unpin.addActionListener(this);
		JButton get = new JButton("Get");
		get.addActionListener(this); 
		JButton clear = new JButton("Clear");
		clear.addActionListener(this); 
		JButton exit = new JButton("Exit"); 
		exit.addActionListener(this); 

		add(ip1);
		add(ip);
		add(portNumber1);
		add(portNumber);
		add(textToServer1);
		add(textToServer);
		add(result1);
		add(result);
		add(connect);
		add(disconnect);
		add(post);
		add(pin);
		add(unpin);
		add(get); 
		add(clear); 
		add(exit); 

	}

	public void actionPerformed(ActionEvent e) {
				
		if (e.getActionCommand().equals("Connect")) {
			try {
				if (ip.getText().isEmpty() || portNumber.getText().isEmpty()) {
					JOptionPane.showMessageDialog(this, "Missing one or both of: ip and port number", "Connection unsuccessful" , JOptionPane.INFORMATION_MESSAGE);
				} else { 
					String serverAddress = ip.getText(); 
					int portNum = Integer.parseInt(portNumber.getText());
					
					socket = new Socket(serverAddress, portNum); 

					isConnected = true; 
					JOptionPane.showMessageDialog(this, "You are now connected to the server!", "Connection successful" , JOptionPane.INFORMATION_MESSAGE);
					
					in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
					out = new PrintWriter(socket.getOutputStream(), true); 
					
					System.out.println(in.readLine()); //Read in server's first message for the list of available colours.  
					System.out.println(in.readLine()); //Read in server's second message for the board dimensions 
					
					//Test connection for back and forth messaging 
					out.println("Hello! Can you read me?"); //Send message to server 
					System.out.println("Server said: " + in.readLine()); //Wait for a response from the server, and then print the message received by the socket (or rather the stream object "in" that handles any incoming input to the socket) 
					
				}
				
			} catch (Exception e1) {
				System.err.println("error"); 
			}
			
		} else if (e.getActionCommand().equals("Disconnect")) { 
			
			if (!isConnected) {
				JOptionPane.showMessageDialog(this, "Cannot disconnect: Must first be connected", "Disconnect unsuccessful" , JOptionPane.WARNING_MESSAGE);
			} else { 
				try { 
					socket.close(); 
					JOptionPane.showMessageDialog(this,  "Disconnect successful. Connection terminated.", "Disconnected", JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e2) {
					System.err.println("Couldn't close the socket");
				} 
			}

			
		} else if (e.getActionCommand().equals("Post")) {
			
			if (!isConnected) { 
				JOptionPane.showMessageDialog(this,  "Cannot post: Must first be connected", "Post unsuccessful", JOptionPane.INFORMATION_MESSAGE);
			} else {
				try {
					String inputString = textToServer.getText(); 
					
					String[] splits = inputString.split(" "); 
					int x = Integer.parseInt(splits[1]);
					int y = Integer.parseInt(splits[2]); 
					int noteWidth = Integer.parseInt(splits[3]); 
					int noteHeight = Integer.parseInt(splits[4]); 
					String colour = splits[5]; 
					
					String message = "";
					for (int i = 6; i < splits.length; i++) {
						message += splits[i] + " ";
					} 
					
					//Make sure all input was received 
					System.out.printf("Coordinates: %d, %d\n", x, y); 
					System.out.printf("Note width: %d, Note height: %d\n", noteWidth, noteHeight);
					System.out.printf("Note colour: %s\n", colour);
					System.out.printf("Message: %s\n", message); 
					
					out.printf("POST=%d,%d,%d,%d,%s,%s\n", x, y, noteWidth, noteHeight, colour, message); 
					result.setText("Server said: " + in.readLine());

				}
				catch (Exception e4) {
					System.err.println("Error");
				}
			}

		} else if (e.getActionCommand().equals("Pin")) {
			if (!isConnected) { 
				JOptionPane.showMessageDialog(this,  "Cannot pin: Must first be connected", "Pin unsuccessful", JOptionPane.INFORMATION_MESSAGE);
			} else {
				String pin = JOptionPane.showInputDialog("Please input coordinates in the format 'x,y': "); 
				try {
					String[] splits = pin.split(","); 
					int x = Integer.parseInt(splits[0]); 
					int y = Integer.parseInt(splits[1]); 
					
					out.printf("PIN=%d,%d\n", x, y); 
					result.setText("Server said: " + in.readLine());
					
				} catch (Exception e3) {
					System.err.println("Error"); 
				}
			}

		
		} else if (e.getActionCommand().equals("Unpin")) { 
			if (!isConnected) { 
				JOptionPane.showMessageDialog(this,  "Cannot unpin: Must first be connected", "Unpin unsuccessful", JOptionPane.INFORMATION_MESSAGE);
			} else {
				String unpin = JOptionPane.showInputDialog("Please input coordinates in the format 'x,y': ");
				try {
					String[] splits = unpin.split(","); 
					int x = Integer.parseInt(splits[0]);
					int y = Integer.parseInt(splits[1]); 
					
					out.printf("UNPIN=%d,%d\n", x, y); 
					result.setText("Server said: " + in.readLine()); 
					
				} catch (Exception e7) {
					System.err.println("Error"); 
					
				}
			}

		
		} else if (e.getActionCommand().equals("Get")) {
			String color, contains, refersTo; 
			if (!isConnected) {
				JOptionPane.showMessageDialog(this, "Cannot get: Must first be connected", "Get unsuccessful",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				try {
					String input = JOptionPane
							.showInputDialog("Use form: 'color=<color> contains=<data> refersTo=<string>'");
					String[] split1 = input.split(" ");
					switch (split1.length) {
					case 1:
						String[] split2 = split1[0].split("=");
						color = split2[1]; 
						
						out.printf("GET=%s\n", color); 

						while (!in.readLine().equals("")) {
							System.out.println("Server said: " + in.readLine());
						} 
						
					case 2:
						String[] split3 = split1[0].split("=");
						String[] split5 = split1[1].split("=");
						color = split3[1];
						contains = split5[1]; 
						
						out.printf("GET=%s,%s\n", color, contains); 
						
						while (!in.readLine().equals("")) {
							System.out.println("Server said: " + in.readLine());
						} 
						
					case 3:
						String[] split4 = split1[0].split("=");
						String[] split6 = split1[1].split("=");
						String[] split7 = split1[2].split("=");
						color = split4[1];
						contains = split6[1];
						refersTo = split7[1]; 
						
						out.printf("GET=%s,%s,%s\n", color, contains, refersTo); 
						
						while (!in.readLine().equals("")) {
							System.out.println("Server said: " + in.readLine());
						} 
					}
					
				} catch (Exception e6) {
					System.err.println("Error");
				}

			}
		} else if (e.getActionCommand().equals("Clear")) { 
			
			if (!isConnected) {
				JOptionPane.showMessageDialog(this, "Cannot clear: Must first be connected", "Clear unsuccessful" , JOptionPane.WARNING_MESSAGE);
			} else { 
				try { 
					out.println("CLEAR"); 
					result.setText("Server said: " + in.readLine()); 

				} catch (Exception e2) {
					System.err.println("Couldn't clear the board.");
				} 
			}
		} else if (e.getActionCommand().equals("exit")) {
			System.exit(0); 
		}
		
		
		
	}
	
	public class Note {
		private int x, y, width, height;
		private String message, colour; 
		private boolean isPinned; 

		public Note(int x, int y, int width, int height, String colour, String message) {
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
	} 
	
	public static void main(String[] args) throws Exception {
		// GUI setup
		Client client = new Client();
		client.setVisible(true); 
	}

}
