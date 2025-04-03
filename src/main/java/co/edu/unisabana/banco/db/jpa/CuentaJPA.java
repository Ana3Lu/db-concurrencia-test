package co.edu.unisabana.banco.db.jpa;

import co.edu.unisabana.banco.db.orm.CuentaORM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaJPA extends JpaRepository<CuentaORM, Long> {
}
