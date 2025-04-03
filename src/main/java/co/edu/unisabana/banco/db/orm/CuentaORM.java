package co.edu.unisabana.banco.db.orm;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cuentas")
public class CuentaORM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private float monto;
}
