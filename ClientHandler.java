import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;
    private String passwordAttempt;
    private boolean isAuthenticated;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.passwordAttempt = bufferedReader.readLine();

            // Get stored password hash from file
            String data;
            String[] dataArray;
            try {
                File file = new File("Authentification.txt");
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.toURI().toURL().openStream()));
                data = fileReader.readLine();
                dataArray = data.split(",");
                fileReader.close();
            } catch (Exception e) {
                e.printStackTrace();
                closeEverything();
                return;
            }

            String storedPasswordHash = dataArray[1];
            // Check if password attempt matches stored password hash
            if (!SecureUtils.validatePassword(storedPasswordHash, passwordAttempt)) {
                // Send authentication failure message to client
                bufferedWriter.write("Authentication failed. Please enter correct username and password.");
                bufferedWriter.newLine();
                bufferedWriter.flush();
                closeEverything();
                isAuthenticated = false;
                return;
            }

            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            isAuthenticated = true;
            // Announcement after successful authentication
            broadcastMessage("SERVER " + clientUsername + " has entered the chat");

            // Welcome message to the authenticated client
            bufferedWriter.write("Password Accepted\nWelcome to the chat.");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (Exception e) {
            closeEverything();
            isAuthenticated = false;
        }
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (Exception e) {
                closeEverything();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (Exception e) {
                removeClientHandler();
                closeEverything();
                break;
            }
        }
    }
}