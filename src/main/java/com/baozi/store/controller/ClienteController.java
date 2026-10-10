package com.baozi.store.controller;

import com.baozi.store.model.Cliente;
import com.baozi.store.repository.ClienteRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository repository;

    public ClienteController(ClienteRepository repository) {
        this.repository = repository;
    }

    // POST /clientes -> criar
    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        cliente.setId(null); // garante que e um cadastro novo
        if (cliente.getClienteDesde() == null) {
            cliente.setClienteDesde(LocalDate.now());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(cliente));
    }

    // GET /clientes -> listar todos
    @GetMapping
    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    // GET /clientes/{id} -> consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /clientes/{id} -> atualizar (opcional)
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @RequestBody Cliente novo) {
        return repository.findById(id).map(existente -> {
            existente.setNome(novo.getNome());
            if (novo.getClienteDesde() != null) {
                existente.setClienteDesde(novo.getClienteDesde());
            }
            return ResponseEntity.ok(repository.save(existente));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE /clientes/{id} -> apagar
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
                    .body("Nao e possivel apagar: este cliente possui pedidos. Apague os pedidos primeiro.");
        }
    }
}
