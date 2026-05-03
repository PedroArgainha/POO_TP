package Exceptions;

public class DivisaoNaoExisteException extends RuntimeException {
    public DivisaoNaoExisteException(String message) {
        super(message);
    }
}
