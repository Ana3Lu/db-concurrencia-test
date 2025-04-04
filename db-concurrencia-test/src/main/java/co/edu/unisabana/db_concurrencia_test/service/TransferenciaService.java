package co.edu.unisabana.db_concurrencia_test.service;

import co.edu.unisabana.db_concurrencia_test.model.Cuenta;
import co.edu.unisabana.db_concurrencia_test.model.Transaccion;
import co.edu.unisabana.db_concurrencia_test.repository.CuentaRepository;
import co.edu.unisabana.db_concurrencia_test.repository.TransaccionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TransferenciaService {

    private final CuentaRepository cuentaRepo;
    private final TransaccionRepository transaccionRepo;

    public TransferenciaService(CuentaRepository cuentaRepo, TransaccionRepository transaccionRepo) {
        this.cuentaRepo = cuentaRepo;
        this.transaccionRepo = transaccionRepo;
    }

    @Transactional
    public boolean transferir(String origenId, String destinoId, int monto) {
        Cuenta origen = cuentaRepo.findById(origenId).orElse(null);
        Cuenta destino = cuentaRepo.findById(destinoId).orElse(null);

        if (origen == null || destino == null || origen.getMonto() < monto) {
            return false;
        }

        origen.setMonto(origen.getMonto() - monto);
        destino.setMonto(destino.getMonto() + monto);
        cuentaRepo.save(origen);
        cuentaRepo.save(destino);

        Transaccion tx = new Transaccion();
        tx.setOrigen(origenId);
        tx.setDestino(destinoId);
        tx.setMonto(monto);
        transaccionRepo.save(tx);

        return true;
    }
}
