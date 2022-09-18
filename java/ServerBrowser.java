// Java IM Program, v0.2.0
// MASTER-SERVER EXECUTABLE
//
// developed by BurntBread007

import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.BindException;
import java.util.InputMismatchException;

public class ServerBrowser {
    // Private class variables
    private final ServerSocket serverSocket;
    private static Scanner scanner = new Scanner(System.in);
    //public static int PORT = 0;

    // Class constructor, connects the server.
    public ServerBrowser(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // MAIN SERVER METHOD
    public static void main(String[] args) throws IOException {
        System.out.println("\n================================");
        System.out.println(  "| Java IM   v0.2.0 Pre-Release |");
        System.out.println(  "| Developed  by  BurntBread007 |");
        System.out.println(  "================================");

        int port = askPort();
        ServerBrowser serverBrowser = new ServerBrowser(connectServerSocket(port));
        
        // Assumes that serverSocket works successfully, prints the start of the server.
        System.out.println("\nServer start success!");
        System.out.println("\nHost Name: "+InetAddress.getLocalHost()+"\nHost Port: "+port);
        System.out.println("\n\n====================\n  SERVER BROWSER LOG\n====================");
        serverBrowser.startServer();
    }

    public static ServerSocket connectServerSocket(int port) {
        while(true) {
            try { 
                System.out.println("Initializing server on port "+port+"...");
                ServerSocket serverSocket = new ServerSocket(port);
                return serverSocket; 
            } 
            catch (BindException e) {
                System.out.println("\n!! ERROR | An application on this device is already using the port you entered. \nPlease enter another port number, or close other programs/servers and try again.");
                port = askPort(); 
            }
            catch (IOException e) {
                System.out.println("!! ERROR | The server could not be started. Please restart the program and try again.");
                port = askPort();  
            }
        }
    }

    // Gets a safe port number from the user. It checks 1. to see if it is a real integer, and 
    // 2. if the real integer is a valid port number. Loops until a valid number is given.
    public static int askPort() {
        int port = 0;
        while(true) {
            System.out.println("\nEnter a port number to host the server...");
            try { 
                port = scanner.nextInt();
                if((port > 65000)||(port < 1)) {
                    scanner.nextLine();
                    System.out.println("\n!! ERROR | Port number is too large or in incorrect format.\nPlease try a number between 1 and 65000."); 
                } else { return port; }
            }
            catch (InputMismatchException e) { 
                System.out.println("\n!! ERROR | Port number is too large or in incorrect format.\nPlease try a number between 1 and 65000."); 
                scanner.nextLine(); 
            }
        }
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
        } catch (IOException e) {System.out.println("!! ERROR | The server could not be started. Please restart the program and try again.");}
    }
    // Disconnects the server.
    public void closeServerSocket() {
        try {
            if(serverSocket != null) { serverSocket.close(); }
        } catch (IOException e) { e.printStackTrace(); }
    }

}