package client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the channel: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 6823);
        Client client = new Client(socket, username);
        client.listenToMessage();
        client.sendMessage();
    }
}
