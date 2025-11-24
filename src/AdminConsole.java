import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AdminConsole implements Runnable {
    public AdminConsole(ChatServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Admin console running. Type 'help' for commands.");
        // Input command loop
        while (true) {
            System.out.print("admin> ");
            try {
                String line = br.readLine();
                String[] parts = line.trim().split("\\s+", 2);
                String cmd = parts[0].toLowerCase();

                switch (cmd) {
                    case "help":
                        System.out.println("Commands: ban <user>, unban <user>, listbans, quit");
                        break;
                    case "ban": {
                        if (parts.length < 2) {
                            System.out.println("Usage: ban < username > ");
                            continue;
                        }
                        String userName = parts[1].trim();

                        BanManager.getInstance().ban(userName);
                        server.kickIfBanned(userName);
                        System.out.println("Banned: " + userName);
                        break;
                    }
                    case "unban": {
                        if (parts.length < 2) {
                            System.out.println("Usage: unban < username > ");
                            continue;
                        }
                        String userName = parts[1].trim();

                        BanManager.getInstance().unban(userName);
                        System.out.println("Unbanned: " + userName);
                        break;
                    }
                    case "listbans":
                        System.out.println(BanManager.getInstance().listBanned());
                        break;
                    case "quit":
                        System.out.println("Shutting down server (admin requested).");
                        System.exit(0);
                    default:
                        System.out.println("Unknown command. Type 'help'.");
                        break;
                }
            } catch (IOException ex) {
                System.out.println("Admin console error: " + ex);
            }
        }
    }

    private final ChatServer server;
}
