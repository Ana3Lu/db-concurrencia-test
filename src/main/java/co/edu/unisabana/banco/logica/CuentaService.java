package co.edu.unisabana.banco.logica;

import co.edu.unisabana.banco.db.jpa.CuentaJPA;
import co.edu.unisabana.banco.db.orm.CuentaORM;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CuentaService {

    private final CuentaJPA cuentaJPA;

    public CuentaORM crearCuenta(float montoInicial) {
        CuentaORM cuenta = new CuentaORM();
        cuenta.setMonto(montoInicial);
        return cuentaJPA.save(cuenta);
    }

    public List<CuentaORM> listarCuentas() {
        return cuentaJPA.findAll();
    }
}
