package ChatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer implements Runnable{
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public MyServer(){
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
                server = new ServerSocket(9999);
                pool = Executors.newCachedThreadPool(); ///time 19:59
                while(!done) {
                    Socket client = server.accept();
                    ConnectionHandler handler = new ConnectionHandler(client);
                    connections.add(handler);
                    pool.execute(handler); //this makes the run() function to execute
            }
        } catch (IOException e) {
                shutDown();
        }
    }

    //to broadcast the message to all  connected handlers
    public void broadCast(String message){
        for(ConnectionHandler ch: connections){
            if(ch!=null){
                ch.sendMessage(message);
            }
        }
    }

    public void shutDown(){
        done = true;
        pool.shutdown();
        if(!server.isClosed()){
            try {
                server.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        for(ConnectionHandler ch : connections){
            ch.shutDown();
        }
    }

    class ConnectionHandler implements  Runnable{
        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String name;
        public ConnectionHandler(Socket client){
            this.client = client;
        }

        @Override
        public  void run(){
            try {
                out = new PrintWriter(client.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                out.println("Please enter your name");
                name = in.readLine();
                System.out.println(name + " has connected");

                broadCast(name +" joined the chat!");
                String message;
                while((message = in.readLine()) != null){
                    if(message.startsWith("/nick")){
                        String[] messageSplit = message.split(" ",2);

                        if(messageSplit.length ==2){
                            broadCast(name + " renamed theselves to "+ messageSplit[1]);
                            System.out.println(name + " renamed theselves to "+ messageSplit[1]);
                            name = messageSplit[1];
                            out.println("Successfully changed name to "+ name);
                        } else{
                            out.println("No name was provided");
                        }
                    }else if(message.startsWith("/quit")){
                        broadCast(name + " left the chat!");
                        shutDown();
                    }else{
                        broadCast(name+": "+ message);
                    }
                }
            } catch (IOException e) {
                    shutDown();
            }
        }

        //To send message to client via the handler
        public void sendMessage(String message){
            out.println(message);
        }

        public void shutDown(){
            try {
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        MyServer server = new MyServer();
        server.run();
    }
}
