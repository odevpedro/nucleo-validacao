# Backlog — nucleo-validacao

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado de uma funcionalidade.
> **Ultima atualizacao:** 2026-06-19

---

## Sobre o Projeto

Gateway generico para execucao de grupos de validacoes bancarias via PL/SQL, com auditoria transacional independente e configuracao declarativa em YAML.

**Versao atual:** `1.0.0`
**Stack principal:** Java 17, Spring Boot 3.x, Oracle Database Free, JDBC, Docker Compose

---

## Legenda

| Simbolo | Significado |
|---------|-------------|
| `[ ]`   | Pendente |
| `[~]`   | Em andamento |
| `[x]`   | Concluido |

---

## Concluidas

- `[x]` Estrutura Maven com Spring Boot 3.x, Spring Web, Spring JDBC, Lombok, Jackson, Testcontainers
- `[x]` Docker Compose com Oracle Database Free
- `[x]` Scripts SQL de criacao de usuarios, tabelas de negocio, tabelas de auditoria
- `[x]` Packages PL/SQL dos 10 dominios bancarios (credito, onboarding, PIX, boletos, cartao, compliance, recuperacao, investimentos, garantias, fechamento)
- `[x]` Grants para APP_VALIDACAO, APP_READONLY, APP_READWRITE, APP_RESTRICTED
- `[x]` Dados mockados (clientes, contas, contratos, chaves PIX, propostas, boletos)
- `[x]` DTOs genéricos de request/response (ValidacaoRequest, ValidacaoResponseDTO, ValidacaoResultadoDTO, etc.)
- `[x]` Enums (EstadoValidacao, EstadoExecucao, ResultadoNegocio, TipoValidacao, TipoParametro)
- `[x]` Records de configuracao YAML (ConfiguracaoValidacaoDTO, GrupoValidacaoDTO, ValidacaoDefinicaoDTO, etc.)
- `[x]` Records de metadata Oracle (ProcedureSignature, ProcedureParameter, ParameterMode)
- `[x]` configuracoes-validacao.yaml com todos os 10 grupos (100-1000) e suas validacoes
- `[x]` NucleoValidacaoYamlLoader — carga e cache de configuracao
- `[x]` BancoMetadataDiscovery — descoberta de procedures via DatabaseMetaData
- `[x]` ParametroValidator — validacao de obrigatoriedade, tipo, min, max, regex, valores permitidos
- `[x]` ProcedureExecutor — execucao de procedures via CallableStatement com binding e saida padrao
- `[x]` SqlErrorMapper — mapeamento de erros SQL para EstadoExecucao (ORA-01031, timeout, etc.)
- `[x]` ResultadoValidacaoConsolidador — regras de consolidacao do grupo
- `[x]` NucleoValidacaoGateway — orquestrador principal do fluxo
- `[x]` ValidacaoTrackingRepository — auditoria com REQUIRES_NEW
- `[x]` ComplianceAlertService — scheduler a cada 15 min para deteccao de anomalias
- `[x]` ComplianceRepository — consulta de falhas de autorizacao nos ultimos 7 dias
- `[x]` NucleoValidacaoController — POST /api/nucleo-validacao/executar
- `[x]` ComplianceController — GET /admin/compliance/alertas
- `[x]` Exceptions customizadas e GlobalExceptionHandler
- `[x]` Testes de integracao (sucesso total, falha validacao, tipo invalido, reproducao negocio, grupo inexistente)
- `[x]` Testes unitarios do ParametroValidator (obrigatoriedade, tipo, min, max, regex, valores permitidos)
- `[x]` README.md com diagrama, exemplos de curl, explicacao arquitetonica
- `[x]` docs/system-feature-flows.md
- `[x]` docs/data-model.md
- `[x]` Remover `correlationId` do body da request e mover para header `X-Correlation-Id`
- `[x]` Renomear `grupo-consistencia` para `nucleo-validacao` (projeto, pacotes, configs, docs)
- `[x]` Renomear `ID_CONSISTENCIA_DEF` / `NOME_CONSISTENCIA` para `ID_EXECUCAO_DEF` / `NOME_EXECUCAO` em RASTRO_EXECUCAO
- `[x]` Renomear chave YAML de `consistencias:` para `validacoes:`
