// Java IM Program, v0.1.6
// FOR USE WITH SERVER CLASS
//
// developed by BurntBread007

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.lang.Math;

public class ClientHandler implements Runnable{
    // Public ArrayLists. They have matching user data per index, 
    // E.g. the index 2 has a matching user object and username.
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    public static ArrayList<String> nameList = new ArrayList<>();
    public static ArrayList<String> joinList = new ArrayList<>();
    public static ArrayList<String> leaveList = new ArrayList<>();

    // Private class variables
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    // Miscellaneos variables and constants used
    // Mostly for the commands, under the checkForCommand() function.
    private Random rand = new Random();
    //public static ArrayList<String> commandList = new ArrayList<>();

    //Class constructor, connects client stats to ArrayLists, notifies the server and its members.
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();

            clientHandlers.add(this);
            nameList.add(checkDuplicateName(clientUsername));

            // Opens Join Message file, reads all lines, adding each one to an ArrayList.
            try {
                String textFile = "text/JoinMessages.txt";
                InputStreamReader textStream = new InputStreamReader(getClass().getResourceAsStream(textFile));
                BufferedReader fileBuffer = new BufferedReader(textStream);
                String joinLine;
                while((joinLine = fileBuffer.readLine()) != null){
                    joinList.add(joinLine);
                }

                textFile = "text/LeaveMessages.txt";
                textStream = new InputStreamReader(getClass().getResourceAsStream(textFile));
                fileBuffer = new BufferedReader(textStream);
                String leaveLine;
                while((leaveLine = fileBuffer.readLine()) != null){
                    leaveList.add(leaveLine);
                }
            } catch (FileNotFoundException e) {
                joinList.add("joined.");
                leaveList.add("left."); 
                broadcastEvent("Quirky leave message file not found. Reverting to default."); 
            }

            String joinMessage = "\nSERVER : " + clientUsername + " " + readRandomLine(joinList) + "\nThere are now "+ClientHandler.clientHandlers.size()+" users in chat.";
            broadcastEvent(joinMessage);
            printNameList();
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

    // Checks if a new user joins with the same name of someone else. If so, append a number to end end,
    // depending on how many current users have the same name
    public static String checkDuplicateName(String username) {
        String newName = username;
        boolean runAgain = true;
        boolean isEqual = false;
        int counter = 1;
        while(runAgain) {
            if(isEqual == false) { runAgain = false; }
            for(int x = 0; x < nameList.size(); x++) {
                if(newName.equals(nameList.get(x))) {
                    System.out.println("# | New Username \""+newName+"\" DOES equal \""+ nameList.get(x)+"\"");
                    isEqual = true;
                    newName = username+" ("+(counter)+")";
                } else { System.out.println("# | New Username \""+newName+"\" does NOT equal \""+ nameList.get(x)+"\""); isEqual = false;}
                counter++;
            }
        }
        return newName;
    }

    // Checks every message sent from the user to determine several things.
    // 1. Check if first letter is the command key, "/"
    // 2. What is the name of the command they are requesting, command
    // 3. If there is a parameter sent after, afterCommand
    public void checkForCommand(String message) {
        try {
            String commandArg = "/";
            // Messages are received with the text, "[XX:XX:XX] USER : " before the actual message,
            // so this int is a starting index for where to read the actual message.
            int startOfMessage = message.indexOf(" : ")+4;

            if(message.substring(startOfMessage-1, startOfMessage).equals(commandArg)) {
                String command = message.substring(startOfMessage).toLowerCase()+" ";
                String afterCommand = command.substring(command.indexOf(" ")+1).trim();
                command = command.substring(0, command.indexOf(" ")).trim();
                // COMMAND LIST  IS HERE
                // ADD COMMANDS AS AN ELSE IF
                if     (command.equals("list"))     { 
                    broadcastEvent("There are "+nameList.size()+" users online."); 
                    printNameList(); }
                else if(command.equals("leave"))    { 
                    this.closeEverything(socket, bufferedReader, bufferedWriter); }
                else if(command.equals("fart"))     { 
                    broadcastEvent("I just farted harded,,, :flushed emoji: now go shit and piss all over the floor,.."); }
                else if(command.equals("version"))  { 
                    broadcastEvent("This server is running on "+Server.VERSION); }
                else if(command.equals("ip"))       { 
                    broadcastEvent("This server is running on IP "+Server.IP); }
                else if(command.equals("port"))     { 
                    broadcastEvent("This server is running on Port # "+Server.PORT+"\n");}
                else if(command.equals("credit") || command.equals("github")) { 
                    broadcastEvent("This program was brought to you by BurntBread007,\nfound on burntbread007.github.io");}
                else if(command.equals("help"))     { 
                    broadcastEvent("Shut up stupid you don't need help, just figure it out yourself ROFL. (Will update this later)"); }
                else if(command.equals("rename"))   { 
                    for(int x = 0; x < nameList.size(); x++) {
                        if(this.clientUsername.equals(nameList.get(x))) {
                            String newName = checkDuplicateName(afterCommand);
                            nameList.set(x, newName);
                            this.clientUsername = newName;
                            broadcastMessage("User \""+nameList.get(x)+"\" has changed their name to \""+afterCommand+"\".");
                            broadcastSingle("Name successfully changed to \""+afterCommand+"\"!"); 
                        }   }   }
                else if(command.equals("coinflip")) {
                    int coin = rand.nextInt(2);
                    if(coin == 0) { broadcastEvent("Coinflip results with Heads\n"); }
                    else { broadcastEvent("Coinflip results with Tails\n");}
                }
                else if(command.equals("kick"))     {
                    for(int x = 0; x < nameList.size(); x++) {
                        System.out.println("# | Comparing \""+afterCommand+"\" with \""+nameList.get(x)+"\"");
                        if(afterCommand.equals(nameList.get(x))) {
                            System.out.println("# | Names match. Closing connection with \""+nameList.get(x)+"\"");
                            clientHandlers.get(x).closeEverything(socket, bufferedReader, bufferedWriter);
                        } else { System.out.println("# | Names don't match. No connections closed."); }
                    }
                } else { broadcastSingle("Invalid Command!"); }
                broadcastEvent("");
            }
        } catch(StringIndexOutOfBoundsException e) { System.out.println("Oops! Error when reading for command.");}
    }

    // Overridden method
    public void run() {
        String messageFromClient;
        while(socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);
                broadcastMessage(messageFromClient);
                checkForCommand(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Sends a received message out to everyone except the original sender.
    // Used for sending received messages, a notification about the user, etc.
    public void broadcastMessage(String messageToSend) {
        for(ClientHandler clientHandler : clientHandlers) {
            try {
                if(!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
        }
    }
    // Same as broadcastMessage(), though it sends the same message to every client.
    // This is used for listing usernames, number of users connected, etc.
    public void broadcastEvent(String messageToSend) {
        for(ClientHandler x : clientHandlers) {
            try {
                x.bufferedWriter.write(messageToSend);
                x.bufferedWriter.newLine();
                x.bufferedWriter.flush();
                System.out.println(messageToSend);
            } catch (IOException e) { x.closeEverything(socket, bufferedReader, bufferedWriter); }
        }
    }
    //Same as broadcast functions above, but only sends to a single user.
    public void broadcastSingle(String messageToSend) {
        try {
            bufferedWriter.write(messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) { closeEverything(socket, bufferedReader, bufferedWriter); }
    }

    // Returns the name of a given index in the clientHandlers ArrayList.
    public static String getName(int index) {
        ClientHandler user = clientHandlers.get(index);
        String username = user.clientUsername;
        return username;
    }

    // Prints the nameList ArrayList.
    public void printNameList() {
        String names = ("Users online: "+nameList+"\n").toString();
        broadcastEvent(names);
    }

    // Finds a random line from the text file given, used for quirky joining and leaving messages per user.
    public static String readRandomLine(ArrayList<String> array) {
        String ret = null;
        int lines = array.size()-1;
        int min = 2;
        int randLine = (int)(Math.random()*((lines+1)-min+1)+min);
        ret = array.get(randLine);
        return ret;
    }

    // Removes a user from the clientHandler ArrayList.
    // Keeps the list updated for many connects and disconnects.
    public void removeClientHandler() {
        clientHandlers.remove(this);
        nameList.remove(clientUsername);

        String leaveMessage = "\nSERVER : " + clientUsername + " " + readRandomLine(leaveList) + "\n"+clientHandlers.size()+" users are left.";
        broadcastEvent(leaveMessage);
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
}