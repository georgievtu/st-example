// Decorator base
public abstract class MessageDecorator extends Message {
    public MessageDecorator(Message inner) {
        super(inner.getFrom(), inner.getBody());
        this.inner = inner;
    }

    @Override
    public abstract String getBody();

    @Override
    public String toString() {
        return inner.getFrom() + ": " + getBody();
    }

    protected final Message inner;
}
