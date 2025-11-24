import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    public ChatServer(int port) {
        this.port = port;
        clients = Collections.synchronizedSet(new HashSet<>());
    }

    public void register(ClientHandler ch) {
        clients.add(ch);
    }

    public void unregister(ClientHandler ch) {
        clients.remove(ch);
    }

    public void broadcast(String raw) {
        synchronized (clients) {
            for (ClientHandler c : clients)
                c.sendRaw(raw);
        }
    }

    public void kickIfBanned(String username) {
        synchronized (clients) {
            for (ClientHandler c : new HashSet<>(clients)) {
                if (username.equalsIgnoreCase(c.getUsername())) {
                    c.sendRaw("ERROR: Banned!");
                    c.disconnect();
                }
            }
        }
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server listening on port: " + port);

        // Start admin console
        Thread admin = new Thread(new AdminConsole(this));
        admin.setDaemon(true);
        admin.start();
        while (running) {
            Socket s = serverSocket.accept();
            ClientHandler ch = new ClientHandler(s, this);
            // New thread for each client so they do not block the accept loop
            Thread t = new Thread(ch);
            t.start();
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null)
            serverSocket.close();
    }

    private final Set<ClientHandler> clients;
    private final int port;
    private ServerSocket serverSocket;
    private volatile boolean running = true;
}
