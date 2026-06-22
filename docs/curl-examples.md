## Exemplos de curl

### Grupo 100 — Abertura de Conta Corrente

```bash
curl -X POST http://localhost:8081/api/nucleo-validacao/executar \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: test-100" \
  -d '{
    "idGrupoValidacao": 100,
    "parametros": [
      {"nome": "cpf_cnpj", "valor": "11111111111"},
      {"nome": "canal_origem", "valor": "APP"},
      {"nome": "biometria_hash", "valor": "hash1234567890abc"}
    ]
  }'
```

### Grupo 200 — Analise de Credito Pessoal

```bash
curl -X POST http://localhost:8081/api/nucleo-validacao/executar \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: test-200" \
  -d '{
    "idGrupoValidacao": 200,
    "parametros": [
      {"nome": "cod_cli", "valor": 1},
      {"nome": "valor_solicitado", "valor": 10000.00},
      {"nome": "qtd_parcelas", "valor": 24}
    ]
  }'
```

### Grupo 300 — Operacoes PIX

```bash
curl -X POST http://localhost:8081/api/nucleo-validacao/executar \
  -H "Content-Type: application/json" \
  -H "X-Correlation-Id: test-300" \
  -d '{
    "idGrupoValidacao": 300,
    "parametros": [
      {"nome": "agencia", "valor": "0001"},
      {"nome": "conta", "valor": "10000-0"},
      {"nome": "chave_destino", "valor": "22222222222"},
      {"nome": "valor", "valor": 100.00},
      {"nome": "id_solicitacao", "valor": "PIX-TEST-001"}
    ]
  }'
```
