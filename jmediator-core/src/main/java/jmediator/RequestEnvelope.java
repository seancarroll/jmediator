package jmediator;

public class RequestEnvelope<T> {

    private final T payload;

    public RequestEnvelope(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

}
