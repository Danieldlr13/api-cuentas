package co.danieldlr.cuentas;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CuentaService {

    private final CuentaRepository repository;

    public CuentaService(CuentaRepository repository) {
        this.repository = repository;
    }

    public Cuenta crear(String titular, BigDecimal saldoInicial) {
        return repository.save(new Cuenta(titular, saldoInicial));
    }

    public List<Cuenta> listar() {
        return repository.findAll();
    }

    public Cuenta buscar(Long id) {
        return repository.findById(id).orElseThrow(() -> new CuentaNoEncontradaException(id));
    }

    @Transactional
    public void transferir(Long origenId, Long destinoId, BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        if (origenId.equals(destinoId)) {
            throw new IllegalArgumentException("La cuenta origen y la destino deben ser distintas");
        }

        Cuenta origen = buscar(origenId);
        Cuenta destino = buscar(destinoId);

        if (origen.getSaldo().compareTo(monto) < 0) {
            throw new SaldoInsuficienteException(origenId);
        }

        origen.setSaldo(origen.getSaldo().subtract(monto));
        destino.setSaldo(destino.getSaldo().add(monto));
        // No hace falta save(): al terminar la transacción JPA guarda los cambios de las entidades cargadas.
    }
}
