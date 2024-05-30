import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    /*
     * This class is for clients who want to connect to server.
     * TODO: Clients must send username and password to server for verification before connecting to groupchat.
     * This class contains the following actions for users to preform when connected to server:
     *  1. Send messages
     *  2. Receive messages
     *  TODO: 3. Send Files
     *  TODO: 4. Receive Files
     */

    //Objects to handle send and receive data
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;   
    private String username;
    private String password;
    
    public Client(Socket socket, String username, String password){//instantiates Client
        try{
            this.password = password;
            this.username = username;
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(Exception e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage(){//Allows user to send a message for the server to broadcast
        try{
            bufferedWriter.write(password);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(Exception e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage(){//Listens for broadcasted messages from the server
        new Thread(new Runnable() {
            public void run(){
                String msgFromGroupChat;

                while(socket.isConnected()){
                    try{
                        msgFromGroupChat = bufferedReader.readLine();
                        System.out.println(msgFromGroupChat);
                    }catch(Exception e){
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {//Terminates client connection
        try{
            if(bufferedReader != null) bufferedReader.close();
            if(bufferedWriter != null) bufferedWriter.close();
            if(socket != null) socket.close();
            System.exit(0);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {

        Scanner scanner = new Scanner(System.in);
        //Make user need to send username and password for server to verify before allowing user to connect to group chat
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        System.out.println("Please enter password: ");
        String password = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username, password);
        client.listenForMessage();
        client.sendMessage();
    }
}
