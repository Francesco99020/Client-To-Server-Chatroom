import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable{
    /*
     * This class is used to handle each client that enters the server.
     * This class allows the server to handle mutiple users at once by giving each client its own thread.
     */

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();//used to keep track of all clients currently connected to the server

    //Objects to handle send and receive data
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String passwordAttempt;

    public ClientHandler(Socket socket){//instantiates ClientHandler
        try{
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.passwordAttempt = bufferedReader.readLine();            
            
            //get password and salt from file
            String data;
        String[] dataArray = new String[] {""};
        try{
            File file = new File("Authentification.txt");
            Scanner scanner = new Scanner(file);
            data = scanner.nextLine();
            dataArray = data.split(",");
            scanner.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        String password = dataArray[1];
            //Check if password matches
            if(!SecureUtils.validatePassword(password, passwordAttempt))//close connection if password is wrong
                closeEveryThing(socket, bufferedReader, bufferedWriter);
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            for(ClientHandler clientHandler : clientHandlers){
                try{
                    System.out.println(clientUsername);
                    if(clientHandler.clientUsername.equals(clientUsername)){
                        bufferedWriter.write("Password Accepted\nWelcome to the chat.");
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }
                }catch(Exception e){
                    closeEveryThing(socket, bufferedReader, bufferedWriter);
                }
            }
            broadcastMessage("SERVER " + clientUsername + " has entered the chat");
        }catch(Exception e){
            closeEveryThing(socket, bufferedReader, bufferedWriter);
        }
    }
    private void closeEveryThing(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {//Terminates client connection
        removeClientHandler();
        try{
            if(bufferedReader != null) bufferedReader.close();
            if(bufferedWriter != null) bufferedWriter.close();
            if(socket != null) socket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void broadcastMessage(String messageToSend) {//broadcasts message to group chat (excluding the person who sent the message)
        for(ClientHandler clientHandler : clientHandlers){
            try{
                if(!clientHandler.clientUsername.equals(clientUsername)){
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            }catch(Exception e){
                closeEveryThing(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler(){//Removes client from clientHandlers array
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    @Override
    public void run() {//Overrides run() method so this class can implement runnable interface 
        String messageFromClient;

        while(socket.isConnected()){
            try{
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            }catch(Exception e){
                closeEveryThing(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }    
}