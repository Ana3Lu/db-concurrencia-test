package co.edu.unisabana.banco.logica;

import co.edu.unisabana.banco.db.jpa.CuentaJPA;
import co.edu.unisabana.banco.db.jpa.TransaccionJPA;
import co.edu.unisabana.banco.db.orm.CuentaORM;
import co.edu.unisabana.banco.db.orm.TransaccionORM;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;


import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class TransaccionService {

    private CuentaJPA cuentaJPA;
    private TransaccionJPA transaccionJPA;

    @Retryable(
            value = OptimisticLockException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 100) // Espera 100ms antes de reintentar
    )
    @Transactional
    public void moverDinero(Long origenId, Long destinoId, float monto) {
        CuentaORM origen = cuentaJPA.findById(origenId)
                .orElseThrow(() -> new NoSuchElementException("Cuenta origen no encontrada"));
        CuentaORM destino = cuentaJPA.findById(destinoId)
                .orElseThrow(() -> new NoSuchElementException("Cuenta destino no encontrada"));

        if (origen.getMonto() < monto) {
            throw new IllegalStateException("Monto insuficiente en la cuenta origen");
        }

        origen.setMonto(origen.getMonto() - monto);
        destino.setMonto(destino.getMonto() + monto);

        cuentaJPA.save(origen);
        cuentaJPA.save(destino);

        TransaccionORM transaccion = new TransaccionORM();
        transaccion.setOrigen(origen);
        transaccion.setDestino(destino);
        transaccion.setMonto(monto);
        transaccion.setTimestamp(LocalDateTime.now());

        transaccionJPA.save(transaccion);
    }
}
