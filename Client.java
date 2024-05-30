import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private String password;
    
    public Client(Socket socket, String username, String password){
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

    public void sendMessage(){
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
                if (messageToSend == null || messageToSend.isEmpty()) {
                    continue; // Skip sending empty messages
                }
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(Exception e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            public void run() {
                String msgFromGroupChat;
    
                try {
                    while (socket.isConnected()) {
                        msgFromGroupChat = bufferedReader.readLine();
                        if (msgFromGroupChat != null) {
                            if (msgFromGroupChat.equals("Authentication failed. Please enter correct username and password.")) {
                                System.out.println("Authentication failed. Exiting...");
                                closeEverything(socket, bufferedReader, bufferedWriter);
                                return; // Exit the thread
                            }
                            System.out.println(msgFromGroupChat);
                        }
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }).start();
    }    

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try{
            if(bufferedReader != null) bufferedReader.close();
            if(bufferedWriter != null) bufferedWriter.close();
            if(socket != null) socket.close();
            System.exit(0); // Exit the client program
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        Scanner scanner = new Scanner(System.in);
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