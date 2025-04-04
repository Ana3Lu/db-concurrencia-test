package co.edu.unisabana.banco.controller;

import co.edu.unisabana.banco.controller.dto.TransaccionDTO;
import co.edu.unisabana.banco.logica.TransaccionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class TransaccionController {

    private TransaccionService transaccionService;

    @PostMapping(path = "/transaccion")
    public ResponseEntity<String> realizarTransaccion(@RequestBody TransaccionDTO transaccion) {
        transaccionService.moverDinero(transaccion.origen_id(), transaccion.destino_id(), transaccion.monto());
        return ResponseEntity.ok("Transacción exitosa");
    }
}
