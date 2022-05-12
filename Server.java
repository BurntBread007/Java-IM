import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private final ServerSocket serverSocket;
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try {
            while(!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(socket);
                int counter = 0;

                Thread thread = new Thread(clientHandler);
                thread.start();
                System.out.println("SERVER : \"" + ClientHandler.getName(counter) + "\" HAS CONNECTED");
                counter++;
            }
        } catch (IOException e) {}
    }

    public void closeServerSocket() {
        try {
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\nEnter a 4-digit room code to host...");
        int port = scanner.nextInt();
        if ( port == 0) { port = 7; }
        scanner.close();

        ServerSocket serverSocket = new ServerSocket(port);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}