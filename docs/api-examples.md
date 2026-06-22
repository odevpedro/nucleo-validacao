## Exemplos de Requisicao / Resposta

### POST /api/nucleo-validacao/executar

Executa um grupo de validacoes.

**Request (200 — ANALISE_CREDITO_PESSOAL):**

```json
{
  "idGrupoValidacao": 200,
  "parametros": [
    { "nome": "cod_cli", "valor": 1 },
    { "nome": "valor_solicitado", "valor": 10000.00 },
    { "nome": "qtd_parcelas", "valor": 24 }
  ]
}
```

O `correlationId` e enviado via header `X-Correlation-Id`. Se ausente, um UUID e gerado automaticamente.

**Response (200 — Sucesso):**

```json
{
  "idGrupoSolicitacao": 90001,
  "idGrupoValidacao": 200,
  "nomeGrupoValidacao": "ANALISE_CREDITO_PESSOAL",
  "estadoGrupo": "FINALIZADO_SUCESSO",
  "resultadoNegocioGrupo": "APROVADO",
  "mensagemGrupoValidacao": "Grupo de validacoes executado com sucesso",
  "correlationId": "6e0a23e0-5b9a-43e1-bc44-b1ad7dfe11d22",
  "validacoes": [
    {
      "idValidacao": 201,
      "nomeValidacao": "calcular_score_interno",
      "procedureRef": "PK_SCORE.CALCULAR_SCORE_INTERNO",
      "tipo": "LEITURA",
      "estadoTecnico": "SUCESSO",
      "resultadoNegocio": "APROVADO",
      "tempoMs": 37,
      "mensagens": ["Score interno aprovado"],
      "payload": { "score": 850, "scoreMinimo": 600 }
    }
  ]
}
```

**Response (400 — Falha de validacao):**

```json
{
  "idGrupoValidacao": 200,
  "nomeGrupoValidacao": "ANALISE_CREDITO_PESSOAL",
  "estadoGrupo": "FALHA_VALIDACAO",
  "mensagem": "Parametros de entrada invalidos",
  "correlationId": "abc-123",
  "erros": [
    {
      "campo": "valor_solicitado",
      "mensagem": "Parametro obrigatorio nao informado",
      "valorRecebido": null
    }
  ]
}
```
