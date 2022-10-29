package client;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

    public class Client {

        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
        private String username;

        public Client(Socket socket, String username) {
            try {
                this.socket = socket;
                this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.username = username;
            } catch (IOException e) {
                closeResources(socket, bufferedWriter, bufferedReader);
            }
        }

        public void sendMessage() {
            try {
                bufferedWriter.write(username);
                //bufferedWriter.newLine();
                bufferedWriter.flush();

                Scanner scanner = new Scanner(System.in);
                while (socket.isConnected()) {
                    String messageToSend = scanner.nextLine();
                    bufferedWriter.write(username + ": " + messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeResources(socket, bufferedWriter, bufferedReader);
            }
        }

        public void listenToMessage() {
            new Thread(() -> {
                String messageFromChannel;
                while (socket.isConnected()) {
                    try {
                        messageFromChannel = bufferedReader.readLine();
                        System.out.println(messageFromChannel);
                    } catch (IOException e) {
                        closeResources(socket, bufferedWriter, bufferedReader);
                    }
                }
            }).start();
        }

        public void closer(){

        }
        private void closeResources(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
            try {
                if (bufferedReader!=null) {
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
    }


