import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) {
        ChatServer server = new ChatServer(1151);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
