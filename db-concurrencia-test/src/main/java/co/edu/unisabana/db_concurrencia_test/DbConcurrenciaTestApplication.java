package co.edu.unisabana.db_concurrencia_test;

import co.edu.unisabana.db_concurrencia_test.service.TransferenciaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
public class DbConcurrenciaTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DbConcurrenciaTestApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(TransferenciaService servicio) {
        return args -> {
            ExecutorService executor = Executors.newFixedThreadPool(30);
            for (int i = 0; i < 30; i++) {
                executor.submit(() -> {
                    while (servicio.transferir("abc", "cbd", 5)) {
                        // sigue moviendo hasta acabar
                    }
                });
            }
        };
    }
}
