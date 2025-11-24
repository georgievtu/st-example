import java.util.regex.Pattern;

// Converts between raw Protocol strings and Message objects
// Example: "USER:name|BODY:Hello!"
public class MessageAdapter {
    public static Message fromRaw(String raw) {
        if (raw == null)
            return null;

        String user = "";
        String body = "";
        String[] parts = raw.split(Pattern.quote(Protocol.SEPARATOR));

        for (String p : parts) {
            String[] kv = p.split(Pattern.quote(Protocol.KEY_VALUE_SEPARATOR), 2);
            if (kv.length < 2)
                continue;
            String k = kv[0];
            String v = kv[1];
            if (k.equals("USER"))
                user = v;
            else if (k.equals("BODY"))
                body = v;
        }

        return new Message(user, body);
    }

    public static String toRaw(Message msg) {
        return "USER:" + msg.getFrom() + Protocol.SEPARATOR + "BODY:" + msg.getBody();
    }
}
