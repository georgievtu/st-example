import java.util.Set;

// Censors words
public class CensorDecorator extends MessageDecorator {
    public CensorDecorator(Message inner, Set<String> badWords) {
        super(inner);
        this.badWords = badWords;
    }

    @Override
    public String getBody() {
        String body = inner.getBody();
        for (String f : badWords)
            body = body.replaceAll(f, "***");
        return body;
    }

    private final Set<String> badWords;
}
