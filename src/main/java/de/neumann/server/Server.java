package de.neumann.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.startListening();
    }

    private final int port;
    private boolean isRunning;
    private Socket remoteClientSocket;

    public Server(int port){
        this.port = port;
    }

    private void checkForCommand(String message){
        switch (message){
            case "/stop":
                this.stopServer();
                break;
            case "/info":
                System.out.println("[SERVER] Status: Server is running");
                break;
            default:
                System.out.println("[CLIENT] Message from Client: " + message);
        }
    }

    public void stopServer() {
        try{
            isRunning = false;
            PrintWriter printWriter = new PrintWriter(
                    new OutputStreamWriter(remoteClientSocket.getOutputStream()));
            printWriter.println("[SERVER] Server closed");
            printWriter.flush();
            printWriter.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void startListening(){
        new Thread(() -> {
            System.out.println("[SERVER] Starting server...");
            isRunning = true;
            while(isRunning){
                try {
                    ServerSocket serverSocket = new ServerSocket(port);
                    System.out.println("[SERVER] Waiting for connection...");
                    remoteClientSocket = serverSocket.accept();
                    System.out.println("[SERVER] Client connected with remote address " + remoteClientSocket.getRemoteSocketAddress());

                    Scanner scanner = new Scanner(
                            new BufferedReader(
                                    new InputStreamReader(remoteClientSocket.getInputStream())));

                    if(scanner.hasNextLine())
                        this.checkForCommand(scanner.nextLine());

                    // Closing connection
                    scanner.close();
                    remoteClientSocket.close();
                    serverSocket.close();

                } catch (IOException e) {
                    System.err.println("[SERVER] An error occurred. Shutting down...");
                    this.stopServer();
                    e.printStackTrace();
                }
            }
            System.out.println("[SERVER] Server closed");
        }).start();
    }
}
