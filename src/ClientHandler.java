import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ClientHandler implements Runnable {
    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void sendRaw(String raw) {
        out.println(raw);
    }

    public String getUsername() {
        return username;
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // First message expected: USER:<name>
            String login = in.readLine();
            Message m = MessageAdapter.fromRaw(login);
            username = m != null ? m.getFrom() : null;

            if (username == null || username.trim().isEmpty()) {
                out.println("ERROR: No username");
                socket.close();
                return;
            }

            if (BanManager.getInstance().isBanned(username)) {
                out.println("ERROR: Banned!");
                socket.close();
                return;
            }

            server.register(this);
            server.broadcast(MessageAdapter.toRaw(new Message("SERVER", username + " joined")));

            String line;
            while ((line = in.readLine()) != null) {
                Message incoming = MessageAdapter.fromRaw(line);
                if (incoming == null)
                    continue;

                // Decorator usage: timestamp and censor
                Set<String> forbidden = new HashSet<>();
                forbidden.add("badword");
                Message decorated = new TimestampDecorator(new CensorDecorator(incoming, forbidden));
                server.broadcast(MessageAdapter.toRaw(decorated));

                // Server-side check: if user got banned
                if (BanManager.getInstance().isBanned(username)) {
                    out.println("ERROR: Banned!");
                    break;
                }
            }
        } catch (IOException ex) {
            // Ignored
        } finally {
            server.unregister(this);
            server.broadcast(MessageAdapter.toRaw(new Message("SERVER", (username == null ? "unknown" : username) + " left")));
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private final Socket socket;
    private final ChatServer server;
    private volatile String username;
    private BufferedReader in;
    private PrintWriter out;
}
