package com.example.demo.config;

import com.example.demo.model.Brinquedo;
import com.example.demo.repository.BrinquedoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/** Carrega alguns brinquedos de exemplo ao iniciar a aplicação (banco H2 em memória). */
@Component
public class DataLoader implements CommandLineRunner {

    private final BrinquedoRepository repository;

    public DataLoader(BrinquedoRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            repository.saveAll(List.of(
                new Brinquedo("Bola de Futebol Júnior", "Bola", 6, "N.4", new BigDecimal("89.90")),
                new Brinquedo("Patins Ajustável", "Patins", 8, "M", new BigDecimal("249.90")),
                new Brinquedo("Bicicleta Aro 16", "Bicicleta", 5, "Aro 16", new BigDecimal("599.00")),
                new Brinquedo("Kit Tênis de Mesa", "Raquete", 10, "Único", new BigDecimal("129.50"))
            ));
        }
    }
}
