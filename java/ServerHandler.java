// Java IM Program, v0.1.5
// FOR USE WITH SERVER CLASS
//
// developed by BurntBread007

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHandler /*implements Runnable*/{
    // Public ArrayLists. They have matching user data per index, 
    // E.g. the index 2 has a matching user object and username.
    public static ArrayList<ServerHandler> serverHandlers = new ArrayList<>();
    public static ArrayList<String> nameList = new ArrayList<>();

    // Private class variables
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String serverName;

    //Class constructor, connects client stats to ArrayLists, notifies the server and its members.
    public ServerHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.serverName = bufferedReader.readLine();

            serverHandlers.add(this);
            nameList.add(serverName);

        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

        // Removes a user from the clientHandler ArrayList.
    // Keeps the list updated for many connects and disconnects.
    public void removeClientHandler() {
        serverHandlers.remove(this);
        nameList.remove(serverName);
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

}