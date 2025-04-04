package co.edu.unisabana.banco.controller;

import co.edu.unisabana.banco.db.orm.CuentaORM;
import co.edu.unisabana.banco.logica.CuentaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class CuentaController {

    private final CuentaService cuentaService;

    @PostMapping(path = "/cuenta")
    public CuentaORM crearCuenta(@RequestBody float montoInicial) {
        return cuentaService.crearCuenta(montoInicial);
    }

    @GetMapping(path = "/cuentas")
    public List<CuentaORM> obtenerCuentas() {
        return cuentaService.listarCuentas();
    }
}
