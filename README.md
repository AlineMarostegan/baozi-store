# Baozi Store - API REST

API REST simples para controle de clientes, produtos e pedidos de uma loja de pão chinês (baozi).

**Tecnologias:** Java 17, Spring Boot 3, Spring Data JPA, MySQL, Maven, Postman.

## Como rodar
1. Ter o MySQL rodando na porta 3306.
2. Abrir `src/main/resources/application.properties` e colocar a senha do seu MySQL.
3. Rodar a classe `BaoziStoreApplication` (ou `mvn spring-boot:run`).
4. A API sobe em `http://localhost:8080`.

## Endpoints
| Método | Rota | O que faz |
|---|---|---|
| POST | /clientes, /produtos, /pedidos | Criar |
| GET | /clientes, /produtos, /pedidos | Listar todos |
| GET | /clientes/{id}, /produtos/{id}, /pedidos/{id} | Consultar por ID |
| PUT | /clientes/{id}, /produtos/{id}, /pedidos/{id} | Atualizar (opcional) |
| DELETE | /clientes/{id}, /produtos/{id}, /pedidos/{id} | Apagar |

## Estrutura
- `model` - entidades JPA (Cliente, Produto, Pedido)
- `repository` - interfaces Spring Data JPA
- `controller` - endpoints REST
