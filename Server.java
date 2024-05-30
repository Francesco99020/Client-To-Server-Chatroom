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

    public void startServer(){//Starts up server
        System.out.println("Server starting up...");
        try{
            while(!ServerSocket.isClosed()){
                Socket socket = ServerSocket.accept();
                //Add Security check here to verify user before allowing them to enter the chat room

                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }catch(Exception e){
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