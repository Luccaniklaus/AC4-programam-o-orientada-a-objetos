package com.example.demo.service;

import com.example.demo.exception.RecursoNaoEncontradoException;
import com.example.demo.model.Brinquedo;
import com.example.demo.repository.BrinquedoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BrinquedoService {

    private final BrinquedoRepository repository;

    public BrinquedoService(BrinquedoRepository repository) {
        this.repository = repository;
    }

    public List<Brinquedo> listar(String nome, String tipo) {
        if (nome != null && !nome.isBlank()) {
            return repository.findByNomeContainingIgnoreCase(nome.trim());
        }
        if (tipo != null && !tipo.isBlank()) {
            return repository.findByTipoIgnoreCase(tipo.trim());
        }
        return repository.findAll();
    }

    public Brinquedo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Brinquedo com id " + id + " não encontrado."));
    }

    /** POST: o objeto entra em uma lista e, em seguida, é enviado ao banco (commit da transação). */
    @Transactional
    public Brinquedo criar(Brinquedo brinquedo) {
        brinquedo.setId(null); // garante INSERT, mesmo que o JSON traga um id
        List<Brinquedo> lista = new ArrayList<>();
        lista.add(brinquedo);
        return repository.saveAll(lista).get(0);
    }

    /** PUT: recupera o registro pelo id, altera os dados, coloca em uma lista e salva no banco. */
    @Transactional
    public Brinquedo atualizar(Long id, Brinquedo dados) {
        Brinquedo existente = buscarPorId(id);
        existente.setNome(dados.getNome());
        existente.setTipo(dados.getTipo());
        existente.setClassificacao(dados.getClassificacao());
        existente.setTamanho(dados.getTamanho());
        existente.setPreco(dados.getPreco());

        List<Brinquedo> lista = new ArrayList<>();
        lista.add(existente);
        return repository.saveAll(lista).get(0);
    }

    /** DELETE: exclusão pelo Id (404 se o id não existir). */
    @Transactional
    public void excluir(Long id) {
        Brinquedo existente = buscarPorId(id);
        repository.deleteById(existente.getId());
    }
}
