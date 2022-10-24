package ChatApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client1 implements Runnable{
    //The client class just need two thread. One receives all message from java while second one receives all commands
    //from console input
    private Socket client;private BufferedReader in;
    private PrintWriter out;
    private boolean done;
    @Override
    public void run() {
        try {
            client  = new Socket("localhost",9999);
            out = new PrintWriter(client.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while((inMessage =in.readLine())!=null){
                System.out.println(inMessage);
            }

        } catch (IOException e) {
            //throw new RuntimeException(e);
            shutDown();
        }
    }

    public void shutDown(){
        done = true;
        try {
            in.close();

        out.close();
        if(!client.isClosed()){
            client.close();
        }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class InputHandler implements  Runnable{

        @Override
        public void run() {
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {

                    String message = inReader.readLine();
                    if(message.equals("/quit")){
                        inReader.close();
                        shutDown();
                    } else{
                        out.println(message);
                    }
                }
            }catch (IOException e) {
                shutDown();
            }
        }
    }

    public static void main(String[] args) {
        Client1 client = new Client1();
        client.run();
    }
}
