// Java IM Program, v0.1.8a
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

    //Local variables
    private Socket socket;
    private BufferedReader bfrRead;
    private BufferedWriter bfrWrite;
    private String username;
    private final static Scanner stdin = new Scanner(System.in);
    final static String ERR = "!! ERROR | ";

    // Class constructor, connects private class variables with parameters.
    public Client (final Socket socket, final String username) {
        try {
            this.socket = socket;
            this.bfrWrite = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bfrRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bfrRead, bfrWrite);
        }
    }

    // Main Client method; gets values needed and starts message listeners.
    public static void main ( String[] args) throws UnknownHostException, IOException {
        System.out.println("\n================================");
        System.out.println(  "| Java IM  v0.1.8a Pre-Release |");
        System.out.println(  "| Developed  by  BurntBread007 |");
        System.out.println(  "================================");

        final String username = askName();
        final String ip =       askIp();
        final int    port =     askPort();
        final Socket socket =   connectSocket(ip, port);
        final Client client = new Client(socket, username);

        System.out.printf("%n%nConnected to room! Have fun! %n%n====================%n\tCHAT%n====================%n");
        client.listenForMessage();
        client.sendMessage();
    }

    // Obtains username from user, continues to loop until a valid username is given.
    public static String askName() {
        System.out.printf("%nEnter your username...%n");
        try {
            String username = stdin.nextLine();
            return username.equals("") ? "Anonymoose" : username;
        } catch (InputMismatchException e) { System.out.printf("%sInvalid name. Please try again.%n", ERR); return askName(); }
    }
    public static String askIp () throws IOException {
        System.out.printf("%nEnter the IP you wish to join... %n(Use \"localhost\" for your own computer.)%n");
        try {
            // Determine IP
            String ip = stdin.nextLine();
            ip = ip.equals("") ? "localhost" : ip;

            // Check if reachable
            System.out.println("Establishing connection...");
            final InetAddress address = InetAddress.getByName(ip);
            final boolean reach = address.isReachable(8000);

            // Determine what to return
            if (reach) System.out.printf("%sFailed IP connection; possibly timed out or unreachable. Please try again, or type another IP.%n", ERR);
            return reach ? ip : askIp();
        } catch (UnknownHostException e) { System.out.printf("%sUnknown IP or host. Please try again.%n", ERR);
        } catch (InputMismatchException e) { System.out.printf("%sInvalid IP address or hostname. Please try again.%n", ERR); }
        return askIp();
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

    // Methods for retrieving and sending messages to and from the connected server.
    // As of v0.1.5, added a workaround to rename the username in both Client and ClientHandler classes when
    // the command is called in-chat.
    public void sendMessage() {
        try {
            bfrWrite.write(username);
            bfrWrite.newLine();
            bfrWrite.flush();
            String messageToSend;
            String time;

            while (socket.isConnected()) {
                messageToSend = stdin.nextLine();
                time = ((LocalTime.now()).toString()).substring(0, 8);

                try {
                    String command = messageToSend.substring(0,7);
                    if (command.equals("/rename")) {
                        username = messageToSend.substring(8);
                        System.out.println(username);
                    }
                } catch (IndexOutOfBoundsException e ) {}

                bfrWrite.write(String.format("[%s] %s : %s", time, username, messageToSend));
                bfrWrite.newLine();
                bfrWrite.flush();
            }
        } catch (IOException e) { closeEverything(socket, bfrRead, bfrWrite); }
    }
    // Additionally, listenForMessage() wanrs user of server disconnect, whenever that may occur.
    public void listenForMessage() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat;
                while (socket.isConnected()) {
                    try {
                        msgFromGroupChat = bfrRead.readLine();
                        if (msgFromGroupChat.equals(null)) { printServerCloseError(); break; }
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        printServerCloseError();
                        break;
                    } catch (NullPointerException e) {
                        printServerCloseError(); 
                        break;
                    }
                }
            }
        }).start();
    }
    // Assuming IP is correct, this tests connection to server on the given port.
    // If not, continue running this method until they enter a working port.
    public static Socket connectSocket (final String ip, int port) {
        try { return new Socket(ip, port); }
        catch (IOException e)              { System.out.printf("%n%sCould not find a server on this port. %nPlease try again.%n", ERR); }
        catch (IllegalArgumentException e) { System.out.printf("%n%sPort number is too large..%nPlease try a number between 1 and 65000.%n", ERR); }
        catch (InputMismatchException e)   { System.out.printf("%n%sPort number is too large or in incorrect format.%nPlease try a number between 1 and 65000.%n", ERR);  }
        return connectSocket(ip,  askPort());
    }
    public void printServerCloseError(){
        System.out.printf("%n%sServer has closed, or lost connection. Please restart the program and find a new server to join.%n");
        closeEverything(socket, bfrRead, bfrWrite);
    }

    // Presumably closes all connections to the server.
    public void closeEverything (final Socket socket, final BufferedReader bfrRead, final BufferedWriter bfrWrite) {
        try {
            if (bfrRead != null)  { bfrRead.close(); }
            if (bfrWrite != null) { bfrWrite.close(); }
            if (socket != null)   { socket.close(); }
        } catch (IOException e) { closeEverything(socket, bfrRead, bfrWrite); }
    }
}
