package client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            spreadTheMessage("#CHANNEL: " + clientUsername + " just joined the channel!");
        } catch (IOException e) {
            closeResources(socket, bufferedWriter, bufferedReader);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                spreadTheMessage(messageFromClient);
            } catch (IOException e) {
                closeResources(socket, bufferedWriter, bufferedReader);
                break;
            }
        }
    }

    private void closeResources(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        clientHandlers.remove(this);
        spreadTheMessage("#CHANNEL: " + clientUsername + " just left the channel!");
        try {
            if (Objects.nonNull(bufferedReader)) {
                bufferedReader.close();
            }
            if (Objects.nonNull(bufferedWriter)) {
                bufferedWriter.close();
            }
            if (Objects.nonNull(socket)) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void spreadTheMessage(String message) {
        clientHandlers.forEach(clientHandler -> {
            if (!clientHandler.clientUsername.equals(clientUsername)) {
                try {
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                } catch (IOException e) {
                    closeResources(clientHandler.socket, clientHandler.bufferedWriter, clientHandler.bufferedReader);
                }
            }
        });
    }
}
