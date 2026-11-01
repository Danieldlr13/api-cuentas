package co.danieldlr.cuentas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    CuentaRepository repository;

    @InjectMocks
    CuentaService service;

    @Test
    void transferenciaCorrectaMueveElSaldo() {
        Cuenta origen = new Cuenta("Ana", new BigDecimal("100"));
        Cuenta destino = new Cuenta("Luis", new BigDecimal("50"));
        when(repository.findById(1L)).thenReturn(Optional.of(origen));
        when(repository.findById(2L)).thenReturn(Optional.of(destino));

        service.transferir(1L, 2L, new BigDecimal("30"));

        assertEquals(new BigDecimal("70"), origen.getSaldo());
        assertEquals(new BigDecimal("80"), destino.getSaldo());
    }

    @Test
    void saldoInsuficienteLanzaExcepcionYNoCambiaSaldos() {
        Cuenta origen = new Cuenta("Ana", new BigDecimal("10"));
        Cuenta destino = new Cuenta("Luis", new BigDecimal("50"));
        when(repository.findById(1L)).thenReturn(Optional.of(origen));
        when(repository.findById(2L)).thenReturn(Optional.of(destino));

        assertThrows(SaldoInsuficienteException.class,
                () -> service.transferir(1L, 2L, new BigDecimal("30")));

        assertEquals(new BigDecimal("10"), origen.getSaldo());
        assertEquals(new BigDecimal("50"), destino.getSaldo());
    }

    @Test
    void cuentaInexistenteLanzaExcepcion() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CuentaNoEncontradaException.class,
                () -> service.transferir(1L, 2L, new BigDecimal("30")));
    }

    @Test
    void montoNoPositivoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> service.transferir(1L, 2L, BigDecimal.ZERO));
    }
}
