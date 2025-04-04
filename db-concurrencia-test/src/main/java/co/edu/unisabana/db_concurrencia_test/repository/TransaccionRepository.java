package co.edu.unisabana.db_concurrencia_test.repository;

import co.edu.unisabana.db_concurrencia_test.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {}
