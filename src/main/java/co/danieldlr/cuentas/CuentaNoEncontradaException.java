package co.danieldlr.cuentas;

public class CuentaNoEncontradaException extends RuntimeException {

    public CuentaNoEncontradaException(Long id) {
        super("No existe la cuenta " + id);
    }
}
