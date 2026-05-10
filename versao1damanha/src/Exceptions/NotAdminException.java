package Exceptions;

public class NotAdminException extends RuntimeException {
    public NotAdminException(String message) {
        super("Não tem permissões de administrador para esta casa.");
    }
}
