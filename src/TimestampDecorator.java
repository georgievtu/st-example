import java.time.LocalDateTime;

// Adds timestamp
public class TimestampDecorator extends MessageDecorator {
    public TimestampDecorator(Message inner) {
        super(inner);
    }

    @Override
    public String getBody() {
        String ts = LocalDateTime.now().toString();
        return "[" + ts + "] " + inner.getBody();
    }
}
