package Model.Exceptions;

public class DispositivoNaoExisteException extends RuntimeException {
    public DispositivoNaoExisteException(String message) {
        super(message);
    }
}
