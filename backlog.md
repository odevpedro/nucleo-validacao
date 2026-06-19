# Backlog — nucleo-validacao

> Registro vivo do progresso do projeto. Atualizado a cada mudanca de estado de uma funcionalidade.
> **Ultima atualizacao:** 2026-06-03

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
- `[x]` DTOs genéricos de request/response (GrupoConsistenciaRequest, GrupoConsistenciaResponseDTO, ConsistenciaResultadoDTO, etc.)
- `[x]` Enums (EstadoGrupo, EstadoConsistencia, ResultadoNegocio, TipoConsistencia, TipoParametro)
- `[x]` Records de configuracao YAML (ConfiguracaoGruposDTO, GrupoDefinicaoDTO, ConsistenciaDefinicaoDTO, etc.)
- `[x]` Records de metadata Oracle (ProcedureSignature, ProcedureParameter, ParameterMode)
- `[x]` Configuracoes-grupos.yaml com todos os 10 grupos (100-1000) e suas validacoes
- `[x]` GrupoConsistenciaYamlLoader — carga e cache de configuracao
- `[x]` BancoMetadataDiscovery — descoberta de procedures via DatabaseMetaData
- `[x]` ParametroValidator — validacao de obrigatoriedade, tipo, min, max, regex, valores permitidos
- `[x]` ProcedureExecutor — execucao de procedures via CallableStatement com binding e saida padrao
- `[x]` SqlErrorMapper — mapeamento de erros SQL para EstadoConsistencia (ORA-01031, timeout, etc.)
- `[x]` ResultadoGrupoConsolidador — regras de consolidacao do grupo
- `[x]` GrupoConsistenciaGateway — orquestrador principal do fluxo
- `[x]] ConsistenciaTrackingRepository — auditoria com REQUIRES_NEW
- `[x]` ComplianceAlertService — scheduler a cada 15 min para deteccao de anomalias
- `[x]` ComplianceRepository — consulta de falhas de autorizacao nos ultimos 7 dias
- `[x]` GrupoConsistenciaController — POST /api/grupos-validacao/executar
- `[x]` ComplianceController — GET /admin/compliance/alertas
- `[x]` Exceptions customizadas e GlobalExceptionHandler
- `[x]` Testes de integracao (sucesso total, falha validacao, tipo invalido, reproducao negocio, grupo inexistente)
- `[x]` Testes unitarios do ParametroValidator (obrigatoriedade, tipo, min, max, regex, valores permitidos)
- `[x]` README.md com diagrama, exemplos de curl, explicacao arquitetonica
- `[x]` docs/system-feature-flows.md
- `[x]` docs/data-model.md
