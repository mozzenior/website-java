package xyz.weichung.libs.mega;

public final class Error extends Exception {
    Error(String message) {
        super(message);
    }

    Error(String message, Throwable cause) {
        super(message, cause);
    }

    Error(Throwable cause) {
        super(cause);
    }

    static Error format(Throwable cause, String fmt, Object... args) {
        return new Error(String.format(fmt, args), cause);
    }

    static Error format(String fmt, Object... args) {
        return new Error(String.format(fmt, args));
    }
}
