// Java IM Program, v0.1.6
// CLIENT EXECUTABLE
//
// developed by BurntBread007

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.time.LocalTime;

public class Client {

    //Private class variables
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private static Scanner scanner = new Scanner(System.in);

    // CLass constructor, connects private class variables with parameters.
    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // MAIN CLIENT METHOD
    public static void main(String[] args) throws UnknownHostException, IOException {
        System.out.println("\n================================");
        System.out.println(  "| Java IM   v0.1.6 Pre-Release |");
        System.out.println(  "| Developed  by  BurntBread007 |");
        System.out.println(  "================================");

        String username = askName();
        String ip = askIp();
        int port = askPort();

        Socket socket = connectSocket(ip, port);
        Client client = new Client(socket, username);

        System.out.println("\n\nConnected to room! Have fun! \n\n====================\n\tCHAT\n====================");
        client.listenForMessage();
        client.sendMessage();
    }

    // Obtains username from user, continues to loop until a valid username is given.
    public static String askName() {
        String username = "";
        try {
            System.out.println("\nEnter your username...");
            username = scanner.nextLine();
            if(username == "") { username = "Anonymoose"; }
        } catch (InputMismatchException e) { System.out.println("!! ERROR | Invalid name, possibly too long or contains invalid characters. Please try again."); askName(); }
        return username;
    }

    // Methods for retrieving and sending messages to and from the connected server.
    // As of v0.1.5, added a workaround to rename the username in both Client and ClientHandler classes when
    // the command is called in-chat.
    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            String messageToSend;
            String time;

            while(socket.isConnected()) {
                messageToSend = scanner.nextLine();
                time = ((LocalTime.now()).toString()).substring(0, 8);

                try {
                    String command = messageToSend.substring(0,7);
                    if(command.equals("/rename")) {
                        username = messageToSend.substring(8);
                        System.out.println(username);
                    }
                } catch (IndexOutOfBoundsException e ) {}

                bufferedWriter.write("["+time+"] " + username + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }
    // Additionally, listenForMessage() wanrs user of server disconnect, whenever that may occur.
    public void listenForMessage() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat;
                while(socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        if(msgFromGroupChat.equals(null)) {printServerCloseError(); break;}
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        printServerCloseError();
                        break;
                    } catch (NullPointerException e) {
                        printServerCloseError(); 
                        break;}
                }
            }
        }).start();
    }

    public void printServerCloseError(){
        System.out.println("\n!! ERROR | Server has closed, or lost connection. Please restart the program and find a new server to join.");
        closeEverything(socket, bufferedReader, bufferedWriter);
    }

    // Presumably closes all connections to the server.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if(bufferedReader != null)  { bufferedReader.close(); }
            if(bufferedWriter != null)  { bufferedWriter.close(); }
            if(socket != null)          { socket.close(); }
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

    // Takes user input for the required IP address and port numbers to connect with the server.
    public static String askIp() throws IOException {
        String ip = "";
        InetAddress address;
        try {
            do {
                System.out.println("\nEnter the IP you wish to join... \n(Use \"localhost\" for your own computer.)");
                ip = scanner.nextLine();
                if(ip == "") { ip = "localhost"; }

                System.out.println("Establishing connection...");
                address = InetAddress.getByName(ip);
                if(!address.isReachable(8000)) {
                    System.out.println("!! ERROR | Failed IP connection; possibly timed out or unreachable. Please try again, or type another IP.");
                } else { System.out.println("Successfully connected to IP."); break; }
            } while (true);
        } catch (UnknownHostException e) { System.out.println("!! ERROR | Unknown IP or host. Please try again."); askIp(); 
        } catch (InputMismatchException e) {System.out.println("!! ERROR | Invalid IP address or hostname. Please try again"); askIp(); }
        return ip;
    }
    // Gets a safe port number from the user. It checks 1. to see if it is a real integer, and 
    // 2. if the real integer is a valid port number. Loops until a valid number is given.
    public static int askPort() {
        int port = 0;
        while(true) {
            System.out.println("\nEnter the hosted port number to join...");
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
    // Assuming IP is correct, this tests connection to server on the given port.
    // If not, continue running this method until they enter a working port.
    public static Socket connectSocket(String ip, int port) {
        try { 
            Socket socket = new Socket(ip, port);
            return socket;
        } 
        catch (IOException e) {
            System.out.println("\n!! ERROR | Cound not find a server on this port. \nPlease try again.");
            port = askPort(); 
        }
        catch (IllegalArgumentException e) {
            System.out.println("\n!! ERROR | Port number is too large..\nPlease try a number between 1 and 65000.");
            port = askPort(); 
        }
        catch (InputMismatchException e) {
            System.out.println("\n!! ERROR | Port number is too large or in incorrect format.\nPlease try a number between 1 and 65000."); 
            port = askPort(); 
        }
        return connectSocket(ip, port);
    }
}