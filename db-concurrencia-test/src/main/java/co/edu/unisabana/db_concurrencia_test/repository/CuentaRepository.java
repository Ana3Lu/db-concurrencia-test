package co.edu.unisabana.db_concurrencia_test.repository;

import co.edu.unisabana.db_concurrencia_test.model.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CuentaRepository extends JpaRepository<Cuenta, String> {}
