package com.example.demo.controller;

import com.example.demo.model.Brinquedo;
import com.example.demo.service.BrinquedoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * API REST de brinquedos (testada no Postman em http://localhost:8080/brinquedos).
 * GET: perfis USER e ADMIN | POST, PUT e DELETE: somente ADMIN.
 */
@RestController
@RequestMapping("/brinquedos")
public class BrinquedoRestController {

    private final BrinquedoService service;

    public BrinquedoRestController(BrinquedoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Brinquedo> listar(@RequestParam(name = "nome", required = false) String nome,
                                  @RequestParam(name = "tipo", required = false) String tipo) {
        return service.listar(nome, tipo);
    }

    @GetMapping("/{id}")
    public Brinquedo buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<Brinquedo> criar(@Valid @RequestBody Brinquedo brinquedo) {
        Brinquedo salvo = service.criar(brinquedo);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(salvo.getId()).toUri();
        return ResponseEntity.created(local).body(salvo);
    }

    @PutMapping("/{id}")
    public Brinquedo atualizar(@PathVariable("id") Long id, @Valid @RequestBody Brinquedo brinquedo) {
        return service.atualizar(id, brinquedo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
