// Java IM Program, v0.1.5
// SERVER EXECUTABLE
//
// developed by BurntBread007

import java.io.*;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.BindException;
import java.util.InputMismatchException;
import java.net.UnknownHostException;

public class Server {
    // Private class variables
    private final ServerSocket serverSocket;
    private static Scanner scanner = new Scanner(System.in);
    public static int PORT = 0;

    // Class constructor, connects the server.
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // MAIN SERVER METHOD
    public static void main(String[] args) throws IOException {
        System.out.println("\n================================");
        System.out.println(  "| Java IM   v0.1.5 Pre-Release |");
        System.out.println(  "| Developed  by  BurntBread007 |");
        System.out.println(  "================================");

        int port = askPort();
        Server server = new Server(connectServerSocket(port));
        PORT = port;

        /*if(askIfPublic()) {
            Client masterServer = new Client();
        }*/
        
        // Assumes that serverSocket works successfully, prints the start of the server.
        System.out.println("\nServer start success!");
        System.out.println("\nHost Name: "+InetAddress.getLocalHost()+"\nHost Port: "+port);
        System.out.println("\n\n====================\n  SERVER CHAT LOG\n====================");
        server.startServer();
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

    // Similar to the askPort() function's method, it asks the server host if they want to add their
    // server to a public Server List.
    public static boolean askIfPublic() {
        String inp = "";
        while(true) {
            System.out.println("\nWould you want to list this server onto a public Server Browser? (Y/N)");
            try {
                inp = scanner.nextLine().trim().toUpperCase();
                if(inp.equals("Y")) { return true; }
                else { return false; }
            } catch (InputMismatchException e) { 
                System.out.println("!! ERROR | Input not recognized, please type only 'Y' for yes, or 'N' for no.");
                scanner.nextLine();
            }
        }
    }

    public static String askIp() throws IOException {
        String ip = "";
        boolean flag = false;
        InetAddress address;
        try {
            while(flag == false) {
                System.out.println("\nEnter the IP you wish to join... \n(Use \"localhost\" for your own computer.)");
                ip = scanner.nextLine();
                if(ip == "") { ip = "localhost"; }

                System.out.println("Establishing connection...");
                address = InetAddress.getByName(ip);
                flag = address.isReachable(8000);
                if(!flag) {
                    System.out.println("!! ERROR | Failed IP connection; possibly timed out or unreachable. Please try again, or type another IP.");
                } else { System.out.println("Successfully connected to IP."); }
            }
        } catch (UnknownHostException e) { System.out.println("!! ERROR | Unknown IP or host. Please try again."); askIp(); 
        } catch (InputMismatchException e) {System.out.println("!! ERROR | Invalid IP address or hostname. Please try again"); askIp(); }
        return ip;
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