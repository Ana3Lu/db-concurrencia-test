package co.edu.unisabana.banco.db.jpa;

import co.edu.unisabana.banco.db.orm.TransaccionORM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionJPA extends JpaRepository<TransaccionORM, Long> {
}
