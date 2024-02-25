// Java IM Program, v0.1.8a
// SERVER EXECUTABLE
//
// developed by BurntBread007

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.BindException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Server {
    // Private class variables
    private final ServerSocket serverSocket;
    private final static Scanner stdin = new Scanner(System.in);
    final static String VERSION =    "v0.1.8a";
    final static String TEXT_PATH = "./text/";
    final static String JOIN_FILE =  TEXT_PATH+"JoinMessages.txt";
    final static String LEAVE_FILE = TEXT_PATH+"LeaveMessages.txt"; 
    static int PORT;
    static InetAddress IP;

    // Class constructor; connects the server.
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Main Server method; 
    public static void main (final String[] args) throws IOException {
        System.out.println("\n================================");
        System.out.println(  "| Java IM   v0.1.8a Pre-Release |");
        System.out.println(  "| Developed  by  BurntBread007 |");
        System.out.println(  "================================");

        // Asks for port, assings servre var, and assigns given IP and PORT to their corresponding constants.
        PORT = askPort();
        IP = InetAddress.getLocalHost();
        final Server server = new Server(connectServerSocket(PORT));
        
        // Assumes that serverSocket works successfully, prints the start of the server.
        System.out.println("\nServer start success!");
        System.out.println("\nHost Name: "+IP+"\nHost Port: "+PORT);
        System.out.println("\n\n====================\n  SERVER CHAT LOG\n====================");
        stdin.close();
        server.startServer();
    }
    // Gets a safe port number from the user.
    public static int askPort () {
        System.out.printf("%nEnter the hosted port number to join...%n");
        try {
            final int port = stdin.nextInt();
            final boolean inRange = (port <= 65535) && (port >= 1);
            return inRange ? port : askPort();
        }
        catch (InputMismatchException e) { return askPort(); }
    }
    // Connects server socket to given port.
    public static ServerSocket connectServerSocket (final int port) {
        try {
            System.out.printf("%nInitializing server on port %s...", port);
            return new ServerSocket(port);
        }
        catch (BindException e) { System.out.println("\n!! ERROR | An application on this device is already using the port you entered. \nPlease enter another port number, or close other programs/servers and try again."); }
        catch (IOException e) { System.out.println("!! ERROR | The server could not be started. Please restart the program and try again."); }
        return connectServerSocket(port);
    }
    // Starts a ClientHandler thread to each newly connected user.
    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                final ClientHandler clientHandler = new ClientHandler(socket);
                final Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {System.out.println("!! ERROR | The server could not be started. Please restart the program and try again.");}
    }
    // Disconnects the server.
    public void closeServerSocket() {
        try {
            if(serverSocket != null) { serverSocket.close(); }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
