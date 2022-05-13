// Java IM Program, v0.1
// FOR USE WITH SERVER CLASS
//
// developed by BurntBread007, 5/13/2022

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{
    // Public ArrayLists. They have matching indexes per client, 
    // E.g. the index 2 has a matching user and username.
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> nameList = new ArrayList<>();

    // Private class variables
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    //Class constructor, connects client stats to ArrayLists, notifies the server and its members.
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            nameList.add(clientUsername);

            String joinMessage = "\nSERVER : " + clientUsername + " has entered the chat.\nThere are now "+ClientHandler.clientHandlers.size()+" users in chat.";
            broadcastEvent(joinMessage);
            System.out.println(joinMessage);
            printNameList();
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

    // Overridden method
    public void run() {
        String messageFromClient;
        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
                System.out.println(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Sends a received message out to everyone except the original sender.
    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler : clientHandlers) {
            try {
                if(!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    // Same as broadcastMessage(), though it sends the same message to every client.
    // This is used for listing usernames and number of users connected.
    public void broadcastEvent(String messageToSend) {
        for(ClientHandler x : clientHandlers) {
            try {
                x.bufferedWriter.write(messageToSend);
                x.bufferedWriter.newLine();
                x.bufferedWriter.flush();
            } catch (IOException e) {
                x.closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    // Returns the name of a given index in the clientHandlers ArrayList.
    public static String getName(int index) {
        ClientHandler user = clientHandlers.get(index);
        String username = user.clientUsername;
        return username;
    }

    // Removes a user from the clientHandler ArrayList.
    // Keeps the list updated for many connects and disconnects.
    public void removeClientHandler() {
        clientHandlers.remove(this);
        nameList.remove(clientUsername);

        String leaveMessage = "\nSERVER : " + clientUsername + " has left the chat.\n"+clientHandlers.size()+" users are left.";
        broadcastEvent(leaveMessage);
        System.out.println(leaveMessage);
        printNameList();
    }
    // First calls removeClientHandler(), then continues to disconnect the actual client.
    // Checks if each bufferedReader, bufferedWriter, and socket are disconnected; if not, disconnect.
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if(bufferedReader != null)  { bufferedReader.close(); }
            if(bufferedWriter != null)  { bufferedWriter.close(); }
            if(socket != null)          { socket.close(); }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    // Prints the nameList ArrayList.
    public void printNameList() {
        String names = ("Users online: "+nameList+"\n").toString();
        System.out.println(names);
        broadcastEvent(names);
    }
}