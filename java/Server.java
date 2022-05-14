// Java IM Program, v0.1.1
// SERVER EXECUTABLE
//
// developed by BurntBread007, 5/14/2022

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    // Private class variables
    private final ServerSocket serverSocket;
    private static Scanner scanner = new Scanner(System.in);

    // Class constructor, connects the server.
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // MAIN SERVER METHOD
    public static void main(String[] args) throws IOException {
        System.out.println("\nEnter a 4-digit room code to host...");
        int port = scanner.nextInt();
        if ( port == 0) { port = 7; }
        System.out.println("Initializing server on port "+port+"...");

        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        System.out.println("\nServer start success!");
        System.out.println("\nHost Name: "+InetAddress.getLocalHost()+"\nHost Port: "+port);
        System.out.println("\n\n====================\nSERVER CHAT LOG\n====================");
        server.startServer();
    }

    // Starts a ClientHandler thread to each newly connected user.
    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {}
    }

    // Disconnects the server.
    public void closeServerSocket() {
        try {
            if(serverSocket != null) { serverSocket.close(); }
        } catch (IOException e) { e.printStackTrace(); }
    }
}

