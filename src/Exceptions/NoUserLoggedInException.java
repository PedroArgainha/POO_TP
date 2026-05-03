package Exceptions;

public class NoUserLoggedInException extends RuntimeException {
    public NoUserLoggedInException(String message) {
        super(message);
    }
}
