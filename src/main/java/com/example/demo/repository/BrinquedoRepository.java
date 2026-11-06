package com.example.demo.repository;

import com.example.demo.model.Brinquedo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository da AC4: JpaRepository<Brinquedo, Long>.
 * Brinquedo = entidade gerenciada; Long = tipo da chave primária (Id).
 * O Spring Data cria a implementação em tempo de execução; não é preciso escrever SQL.
 */
public interface BrinquedoRepository extends JpaRepository<Brinquedo, Long> {

    // Consulta derivada do nome do método: ... WHERE UPPER(nome) LIKE UPPER('%valor%')
    List<Brinquedo> findByNomeContainingIgnoreCase(String nome);

    // Consulta derivada: ... WHERE UPPER(tipo) = UPPER(valor)
    List<Brinquedo> findByTipoIgnoreCase(String tipo);
}
