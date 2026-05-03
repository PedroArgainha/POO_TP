package Exceptions;

public class CasaNaoExisteException extends RuntimeException {
    public CasaNaoExisteException(String message) {
        super(message);
    }
}
