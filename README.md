# WorstMovie
Projeto para listar os vencedores da categoria Pior Filme

---

## ⚙️ Pré-requisitos

- Java 17+
- Maven 3.8+

---

## 🚀 Executando a aplicação localmente

1. Clone o repositório:

```bash
git clone https://github.com/erlibalbinot/WorstMovie.git
cd WorstMovie
```
2. Compile e rode a aplicação
```bash
mvn spring-boot:run
```
3. Acesse a api em
```bash
http://localhost:8080
```

---

## 📂 Endpoints principais

Ao iniciar a aplicação será lido o .csv que estiver na pasta 'src\main\resources' com o nome Movielist e os dados serão inseridos em uma base.
- Existem endpoints para receber arquivos .csv
```bash
POST /movies/csv
Content-Type: multipart/form-data
```

```bash
PUT /movies/csv
Content-Type: multipart/form-data
```

```bash
DELETE /movies/csv
Content-Type: multipart/form-data
```
Para os 3 endpoints o parâmetro é:

file → arquivo CSV com dados de filmes, no mesmo formato do arquivo da pasta resources.

- Endpoints para cadastro, atualização e deleção de 1 filme premiado:

```bash
POST /movies
Content-Type: application/json
Body:
{
    "year": 1980,
    "title": "Can't Stop the Music",
    "studios": "Associated Film Distribution",
    "producers": "Allan Carr",
    "winner": 1 
}
```

```bash
PUT /movies
Content-Type: application/json
Body:
{
    //"id": 1
    "year": 1980,
    "title": "Can't Stop the Music",
    "studios": "Associated Film Distribution",
    "producers": "Allan Carr",
    "winner": 1 
}
```

```bash
DELETE /movies/{id}
DELETE /movies/{title}/{studios}/{producers}
```

- Endpoints para buscas:

```bash
GET /movies/listmovies
//retorna todos os registros cadastrados
```

```bash
GET /movies/winners
//Retorna o produtor com maior intervalo entre dois prêmios consecutivos, e o que obteve dois
//prêmios mais rápido.

```

## Testes

Para rodar os testes do projeto:
```bash
mvn clean test
```
Para rodar os testes com relatório (JaCoCo)
```bash
mvn clean verify
// Abrir o relatório em: target/site/jacoco/index.html
```
