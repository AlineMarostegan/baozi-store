package com.baozi.store.controller;

import com.baozi.store.model.Cliente;
import com.baozi.store.model.Pedido;
import com.baozi.store.model.Produto;
import com.baozi.store.repository.ClienteRepository;
import com.baozi.store.repository.PedidoRepository;
import com.baozi.store.repository.ProdutoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    // POST /pedidos -> criar
    // JSON: { "cliente": {"id": 1}, "produto": {"id": 1}, "quantidade": 5 }
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        ResponseEntity<String> erro = preencherEValidar(pedido);
        if (erro != null) {
            return erro;
        }
        pedido.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoRepository.save(pedido));
    }

    // GET /pedidos -> listar todos
    @GetMapping
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    // GET /pedidos/{id} -> consultar por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /pedidos/{id} -> atualizar (opcional)
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pedido novo) {
        Optional<Pedido> existente = pedidoRepository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ResponseEntity<String> erro = preencherEValidar(novo);
        if (erro != null) {
            return erro;
        }
        Pedido pedido = existente.get();
        pedido.setCliente(novo.getCliente());
        pedido.setProduto(novo.getProduto());
        pedido.setQuantidade(novo.getQuantidade());
        return ResponseEntity.ok(pedidoRepository.save(pedido));
    }

    // DELETE /pedidos/{id} -> apagar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Confere se cliente e produto existem e troca pelos dados completos do banco
    // (assim a resposta mostra nome do cliente, nome do produto etc.)
    private ResponseEntity<String> preencherEValidar(Pedido pedido) {
        if (pedido.getCliente() == null || pedido.getCliente().getId() == null) {
            return ResponseEntity.badRequest().body("Informe o cliente: \"cliente\": {\"id\": 1}");
        }
        if (pedido.getProduto() == null || pedido.getProduto().getId() == null) {
            return ResponseEntity.badRequest().body("Informe o produto: \"produto\": {\"id\": 1}");
        }
        if (pedido.getQuantidade() == null || pedido.getQuantidade() <= 0) {
            return ResponseEntity.badRequest().body("A quantidade deve ser maior que zero.");
        }
        Optional<Cliente> cliente = clienteRepository.findById(pedido.getCliente().getId());
        if (cliente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente nao encontrado.");
        }
        Optional<Produto> produto = produtoRepository.findById(pedido.getProduto().getId());
        if (produto.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto nao encontrado.");
        }
        pedido.setCliente(cliente.get());
        pedido.setProduto(produto.get());
        return null;
    }
}
