import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// Thread-safe singleton
public class BanManager {
    private BanManager() {
        banned = Collections.synchronizedSet(new HashSet<>());
    }

    public static synchronized BanManager getInstance() {
        return Holder.INSTANCE;
    }

    public void ban(String username) {
        banned.add(username.toLowerCase());
    }

    public void unban(String username) {
        banned.remove(username.toLowerCase());
    }

    public boolean isBanned(String username) {
        return banned.contains(username.toLowerCase());
    }

    public Set<String> listBanned() {
        synchronized (banned) {
            return new HashSet<>(banned);
        }
    }

    private final Set<String> banned;

    // Thread-safe static initializer
    private static class Holder {
        private static final BanManager INSTANCE = new BanManager();
    }
}
