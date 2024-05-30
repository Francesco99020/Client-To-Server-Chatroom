import java.io.IOException;
import java.net.*;

public class Server{

    /*
     * This class allows multiple clients to connect to eachother in a group chat
     * TODO: Create a method for verifying users username and password. If either is invalid terminate the connection.
     * TODO: Allow users to send files and broadcast file to other users.
     */

    private ServerSocket ServerSocket;

    public Server(ServerSocket serverSocket){//instantiates server
        this.ServerSocket = serverSocket;
    }

    public void startServer(){
        System.out.println("Server starting up...");
        try{
            while(!ServerSocket.isClosed()){
                Socket socket = ServerSocket.accept();
    
                // Perform authentication
                ClientHandler clientHandler = new ClientHandler(socket);
                if (clientHandler.isAuthenticated()) {
                    // Announcement after successful authentication
                    System.out.println("A new client has connected");
                    Thread thread = new Thread(clientHandler);
                    thread.start();
                } else {
                    // Close connection if authentication fails
                    clientHandler.closeEverything();
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }    

    public void closeServerSocket(){//Closes server
        System.out.println("Server shuting down...");
        try{
            if(ServerSocket != null) ServerSocket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}