package de.neumann.client;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        Client client = new Client("localhost",8000);
        client.sendMessage("Tag auch");
    }

    private final InetSocketAddress address;
    private Socket socket = new Socket();
    private boolean stayConnected = true;

    public Client(String hostname, int port){
        this.address = new InetSocketAddress(hostname, port);
    }

    private void waitForServermessages(){
        new Thread(() -> {
            while (stayConnected){
                try{
                    Scanner scanner = new Scanner(
                            new BufferedReader(
                                    new InputStreamReader(socket.getInputStream())));

                    if(scanner.hasNextLine()) {
                        String msg = scanner.nextLine();
                        if(msg.equals("[SERVER] Server closed"))
                            stayConnected = false;
                        System.out.println(msg);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void sendMessage(String message){
        try {
            System.out.println("[CLIENT] Connecting to server...");
            socket.connect(address, 5000);
            System.out.println("[CLIENT] Connected");

            System.out.println("[CLIENT] Sending message...");
            PrintWriter printWriter = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            printWriter.println(message);
            printWriter.flush();
            System.out.println("[CLIENT] Message sent");

            //waitForServermessages();

            // Closing connection

            printWriter.close();

        } catch (IOException e) {
            System.err.println("[CLIENT] Connection lost");
            e.printStackTrace();
        }
    }
}
