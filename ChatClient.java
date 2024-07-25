import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MessageListener messageListener;

    public ChatClient(String serverAddress, int serverPort, MessageListener messageListener) throws IOException {
        this.socket = new Socket(serverAddress, serverPort);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.messageListener = messageListener;
    }

    public void startClient() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    messageListener.onMessageReceived(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public interface MessageListener {
        void onMessageReceived(String message);
    }
}
