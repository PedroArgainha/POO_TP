package Exceptions;

public class PasswordIncorretaException extends RuntimeException {
    public PasswordIncorretaException(String message) {
        super(message);
    }
}
