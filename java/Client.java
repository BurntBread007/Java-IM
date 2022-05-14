// Java IM Program, v0.1.1
// CLIENT EXECUTABLE
//
// developed by BurntBread007, 5/14/2022

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
        System.out.println("\nEnter your username...");
        String username = scanner.nextLine();
        if(username == "") { username = "Anonymoose"; }
        //String ip = askIp();
        //int port = askPort();
        Socket socket = new Socket(askIp(), askPort());
        Client client = new Client(socket, username);
        System.out.println("\n\nConnected to room! Have fun ;)\n\n====================\n\tCHAT\n====================");
        client.listenForMessage();
        client.sendMessage();
    }

    // Methods for retrieving and sending messages to and from the connected server.
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
                bufferedWriter.write("["+time+"] " + username + " : " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    // Additionally, listenForMessage() wanrs user of server disconnect, whenever that may occur.
    public void listenForMessage() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat;
                while(socket.isConnected()) {
                    try {
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    } catch (IOException e) {
                        System.out.println("Server has closed. Please restart the program and find a new server to join.");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
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

    // Takes user input for the needed ip address and port numbers to connect witht the server.
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
                    System.out.println("Failed IP connection; possibly timed out or unreachable. Please try again, or type another IP.");
                } else { System.out.println("Successfully connected to IP."); }
            }
        } catch (UnknownHostException e) { System.out.println("Unknown IP or host. Please try again."); askIp(); }
        return ip;
    }
    public static int askPort() {
        System.out.println("\nEnter a 4-digit 'room code' to join...\n(Aka port number)");
        int port = 0;
        port = scanner.nextInt();
        if( port == 0) { port = 7; }
        return port;
    }
}