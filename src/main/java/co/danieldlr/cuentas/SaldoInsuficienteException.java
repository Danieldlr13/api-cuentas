package co.danieldlr.cuentas;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long id) {
        super("Saldo insuficiente en la cuenta " + id);
    }
}
