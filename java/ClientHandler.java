// Java IM Program, v0.1.3
// FOR USE WITH SERVER CLASS
//
// developed by BurntBread007

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.lang.Math;

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
            
            // Checks if a new user joins with the same name of someone else. If so, append a number to end end,
            // depending on how many current users have the same name
            clientHandlers.add(this);

            nameList.add(checkDuplicateName(clientUsername));

            String fileName = ".\\txt\\JoinMessages.txt";
            int lines = getNumLines(fileName);
            int min = 2;
            int randLine = (int)(Math.random()*((lines+1)-min+1)+min);

            String joinMessage = "\nSERVER : " + clientUsername + " " + readRandomLine(randLine, fileName) + "\nThere are now "+ClientHandler.clientHandlers.size()+" users in chat.";
            broadcastEvent(joinMessage);
            System.out.println(joinMessage);
            printNameList();
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

    public static String checkDuplicateName(String username) {
        String newName = username;
        boolean isOriginal = true;
        boolean isEqual = false;
        int counter = 1;
        while(isOriginal) {
            if(isEqual == false) { isOriginal = false; }
            for(int x = 0; x < nameList.size(); x++) {
                if(newName.equals(nameList.get(x))) {
                    System.out.println("New Username \""+newName+"\" DOES equal "+ nameList.get(x));
                    isEqual = true;
                    newName = username+" ("+(counter)+")";
                    //break;
                } else { System.out.println("New Username \""+newName+"\" does NOT equal "+ nameList.get(x)); isEqual = false;}
                counter++;
            }
        }
        return newName;
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

        String fileName = ".\\txt\\LeaveMessages.txt";
        int lines = getNumLines(fileName);
        int min = 2;
        int randLine = (int)(Math.random()*((lines+1)-min+1)+min);

        String leaveMessage = "\nSERVER : " + clientUsername + " " + readRandomLine(randLine, fileName) + "\n"+clientHandlers.size()+" users are left.";
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

    public static String readRandomLine(int randLine, String fileName) {
        String ret = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            for (int i = 0; i < randLine - 1; i++) { ret = reader.readLine(); }
            reader.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        return ret;
    }

    public static int getNumLines(String fileName) {
        int lines = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            lines = (int)(reader.lines().count());
            reader.close();
        }
        catch (FileNotFoundException e) {}
        catch (IOException e) {}
        return lines;
    }
}