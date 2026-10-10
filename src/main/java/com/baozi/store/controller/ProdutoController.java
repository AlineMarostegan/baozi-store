package com.baozi.store.controller;

import com.baozi.store.model.Produto;
import com.baozi.store.repository.ProdutoRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    // POST /produtos -> criar
    @PostMapping
    public ResponseEntity<Produto> criar(@RequestBody Produto produto) {
        produto.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(produto));
    }

    // GET /produtos -> listar todos
    @GetMapping
    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    // GET /produtos/{id} -> consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /produtos/{id} -> atualizar (opcional)
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @RequestBody Produto novo) {
        return repository.findById(id).map(existente -> {
            existente.setNome(novo.getNome());
            existente.setPreco(novo.getPreco());
            existente.setEstoque(novo.getEstoque());
            return ResponseEntity.ok(repository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /produtos/{id} -> apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<?> apagar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Nao e possivel apagar: este produto esta em pedidos. Apague os pedidos primeiro.");
        }
    }
}
