public class Message {
    public Message(String from, String body) {
        this.from = from;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return from + ": " + body;
    }

    private final String from;
    private final String body;
}
