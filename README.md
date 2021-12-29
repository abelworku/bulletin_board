# bulletin_board
An object-oriented client-server Java application, where users can store, access, and modify notes on an active server simultaneously, simulating a bulletin board. 

# Overview
- This Java program contains a server, and clients (one or multiple) that connect to the server. The client can interact in the server in many ways that mimic a bulletin board, with actions such as posting, retrieving, pinning, and clearing the board of notes.
- The bulletin board is represented by the server's storage dimensions, which defines how many notes it is able to store for the client from the client's requests. Each note occupies an x,y coordinate on the board, and the board has a width and height for the note to be placed in.  
- The server is able to service requests from multiple running clients; designed with a multithreaded implmentation, clients who establish a connection to the server (a local connection) communicate through a socket bound to the port number specified when the server is initialized. 

# Installation 
- This Java program can be run in many ways, and one common method is through an IDE, or integrated development environment, like Eclipse or IntelliJ. These instructions will describe using the Eclipse IDE to run this Java program. 
- Download and install the Eclipse IDE. 
- Import the project folder into the workspace. 

# Usage
- Start the application's server program by navigating to Server.java, and run it with a set of command-line arguments: [Port Number] [Board Width] [Board Height] [Default Note Colour]. These arguments will be parsed to set up a socket at the port number, build the bulletin board's (and server's) storage dimensions, and it will then wait for the socket to accept a connection at the port number (it will create a thread for the connecting client once the new connection is established). 
- Start the application's client program by navigating to, and running Client.java. A user interface will appear, giving the client a way to establish an initial connection to the server, and perform following actions such as posting notes, retrieving notes, pinning notes, and clearing the bulletin board of existing notes.   
- To exit the application, press the "Exit" button. 
