import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    private static Scanner scanner = new Scanner(System.in);

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

    public void sendMessage() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            while(socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenForMessage() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat;
                while(socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if(bufferedReader != null)  { bufferedReader.close(); }
            if(bufferedWriter != null)  { bufferedWriter.close(); }
            if(socket != null)          { socket.close(); }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        System.out.println("\nEnter your username...");
        String username = scanner.nextLine();
        if(username == "") { username = "IM A SUSSY BAKA :)"; }

        String ip = askIp();
        int port = askPort();

        Socket socket = new Socket(ip, port);
        Client client = new Client(socket, username);
        System.out.println("\n\nConnected to room! Have fun ;)\n\n====================\n\tCHAT\n====================");
        client.listenForMessage();
        client.sendMessage();
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

                System.out.println("Pinging IP...");
                address = InetAddress.getByName(ip);
                flag = address.isReachable(8000);
                if(!flag) {
                    System.out.println("Failed IP connection; timed out or inaccessible. Please try again, or type another IP.");
                } else { System.out.println("Successfully connected to IP."); }
            }
        } catch (UnknownHostException e) { System.out.println("Not a known IP or host. Please try again."); askIp(); }
        return ip;
    }
    public static int askPort() {
        System.out.println("\nEnter a 4-digit room code to join...");
        int port = 0;
        port = scanner.nextInt();
        if( port == 0) { port = 7; }
        return port;
    }
}