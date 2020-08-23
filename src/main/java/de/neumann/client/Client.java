package de.neumann.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Client {
    public static void main(String[] args) {
        Client client = new Client("localhost",8000);
        client.sendMessage("/stop");
    }

    private final InetSocketAddress address;

    public Client(String hostname, int port){
        this.address = new InetSocketAddress(hostname, port);
    }

    public void sendMessage(String message){
        try {
            System.out.println("[CLIENT] Connecting to server...");
            Socket socket = new Socket();
            socket.connect(address, 5000);
            System.out.println("[CLIENT] Connected");

            System.out.println("[CLIENT] Sending message...");
            PrintWriter printWriter = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream()));
            printWriter.println(message);
            printWriter.flush();
            System.out.println("[CLIENT] Message sent");

            // Closing connection

            printWriter.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("[CLIENT] Connection lost");
            e.printStackTrace();
        }

    }
}
