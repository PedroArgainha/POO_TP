package Exceptions;

public class UserNaoregistadoException extends RuntimeException {
    public UserNaoregistadoException(String message) {
        super(message);
    }
}
