package co.edu.unisabana.db_concurrencia_test.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Cuenta {
    @Id
    private String id;
    private int monto;
}
