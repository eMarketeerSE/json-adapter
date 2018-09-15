package io.emarketeer.json;

public class JsonAdapterException extends RuntimeException {

    public JsonAdapterException(final String message) {
        super(message);
    }

    public JsonAdapterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
