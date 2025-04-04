package co.edu.unisabana.db_concurrencia_test.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String origen;
    private String destino;
    private int monto;

    private LocalDateTime timestamp = LocalDateTime.now();
}
