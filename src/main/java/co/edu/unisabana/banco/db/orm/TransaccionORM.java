package co.edu.unisabana.banco.db.orm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transacciones")
public class TransaccionORM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "origen_id", nullable = false)
    private CuentaORM origen;

    @ManyToOne
    @JoinColumn(name = "destinoId", nullable = false)
    private CuentaORM destino;

    private float monto;
    private LocalDateTime timestamp;
}
