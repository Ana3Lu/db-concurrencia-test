package co.edu.unisabana.banco.logica;

import co.edu.unisabana.banco.db.jpa.CuentaJPA;
import co.edu.unisabana.banco.db.jpa.TransaccionJPA;
import co.edu.unisabana.banco.db.orm.CuentaORM;
import co.edu.unisabana.banco.db.orm.TransaccionORM;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class TransaccionService {

    private CuentaJPA cuentaJPA;
    private TransaccionJPA transaccionJPA;

    @Transactional
    public void moverDinero(Long origenId, Long destinoId, float monto) {
        CuentaORM origen = cuentaJPA.findById(origenId)
                .orElseThrow(() -> new RuntimeException("Cuenta origen no encontrada"));
        CuentaORM destino = cuentaJPA.findById(destinoId)
                .orElseThrow(() -> new RuntimeException("Cuenta destino no encontrada"));

        if (origen.getMonto() < monto) {
            throw new RuntimeException("Saldo insuficiente");
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
