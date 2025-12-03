# 🔄 GitHub Actions Workflows

Este diretório contém os workflows do GitHub Actions para automação de CI/CD, code review e validações de qualidade de código.

## 📋 Workflows Disponíveis

### 1. `pr-to-develop.yml` - Validação de PRs para Develop

**Quando executa:**
- Quando um Pull Request é aberto, atualizado ou marcado como pronto para review direcionado à branch `develop`

**O que faz:**
- ✅ Code Review Automatizado usando CodeQL e análise estática
- ✅ Executa testes unitários
- ✅ Gera relatório de cobertura de testes
- ✅ Valida cobertura mínima de 90%
- ✅ Verifica qualidade de código (lint, formatação)
- ✅ Comenta no PR com sugestões de melhoria

**Status Checks:**
- `Code Review Automatizado`
- `Build e Testes Unitários`
- `Validação de Qualidade`

### 2. `merge-to-master.yml` - Validação Rigorosa para Master

**Quando executa:**
- Quando um Pull Request é aberto direcionado à branch `master`

**O que faz:**
- ✅ Valida que o PR vem da branch `develop`
- ✅ Build completo do projeto
- ✅ Executa todos os testes (debug e release)
- ✅ Valida cobertura mínima obrigatória de 90% (bloqueia merge se não atingir)
- ✅ Análise CodeQL completa
- ✅ Verifica aprovações do PR
- ✅ Verifica que todos os status checks estão passando
- ✅ Comenta no PR com status final

**Status Checks:**
- `Validação Rigorosa para Master`

**⚠️ Importante:** Este workflow **bloqueia o merge** se:
- Cobertura de testes < 90%
- Não houver aprovações no PR
- Algum status check falhar

### 3. `push-to-develop.yml` - Validação Contínua

**Quando executa:**
- Quando código é feito push diretamente na branch `develop`
- Pode ser executado manualmente via `workflow_dispatch`

**O que faz:**
- ✅ Build do projeto
- ✅ Executa testes unitários
- ✅ Gera relatório de cobertura (não bloqueia se < 90%)
- ✅ Upload de relatório para Codecov

**Status Checks:**
- `Validação Contínua`

## 🎯 Cobertura de Testes

### Requisitos:

- **Develop**: Recomendado 90%, mas não bloqueia push
- **Master**: **Obrigatório 90%** - bloqueia merge se não atingir

### Como verificar localmente:

```bash
# Executar testes e gerar relatório
./gradlew testDebugUnitTest jacocoTestReport

# Ver relatório HTML
open app/build/reports/jacoco/jacocoTestReport/index.html
```

### Relatórios:

- **XML**: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
- **HTML**: `app/build/reports/jacoco/jacocoTestReport/index.html`
- **Codecov**: Upload automático nos workflows

## 🤖 Code Review Automatizado

O code review automatizado verifica:

1. **Documentação KDoc**: Verifica se classes e funções públicas têm documentação
2. **Tratamento de Erros**: Verifica funções suspensas têm tratamento adequado
3. **Logging**: Sugere melhorias no uso de Log
4. **CodeQL**: Análise estática de segurança e qualidade
5. **Padrões de Código**: Verifica conformidade com boas práticas

### Como funciona:

- Analisa arquivos Kotlin modificados no PR
- Cria comentários no PR com sugestões
- Não bloqueia merge, mas fornece feedback valioso

## 🔧 Configuração

### Pré-requisitos:

1. **Secrets do GitHub** (se necessário):
   - `CODECOV_TOKEN` (opcional, para Codecov)

2. **Branch Protection Rules**:
   - Configure conforme `.github/BRANCH_PROTECTION.md`

3. **CODEOWNERS**:
   - Configure `.github/CODEOWNERS` com os responsáveis

### Personalização:

Para ajustar os workflows:

1. **Cobertura mínima**: Edite `app/build.gradle.kts` (linha 125)
2. **Status checks**: Edite as proteções de branch no GitHub
3. **Code review**: Edite a seção `code-review` nos workflows

## 📊 Monitoramento

### Artifacts Gerados:

- `coverage-report`: Relatório HTML de cobertura (disponível por 90 dias)
- `coverage-report-master`: Relatório para PRs em master

### Badges (opcional):

Adicione ao README.md:

```markdown
![CI](https://github.com/seu-usuario/seu-repo/workflows/PR%20to%20Develop%20-%20Validação%20e%20Code%20Review/badge.svg)
![Coverage](https://codecov.io/gh/seu-usuario/seu-repo/branch/develop/graph/badge.svg)
```

## 🐛 Troubleshooting

### Workflow não executa:

1. Verifique se o arquivo está em `.github/workflows/`
2. Verifique a sintaxe YAML
3. Verifique os triggers (`on:`)

### Cobertura não é calculada:

1. Verifique se os testes estão executando: `./gradlew testDebugUnitTest`
2. Verifique se o relatório é gerado: `./gradlew jacocoTestReport`
3. Verifique os logs do workflow no GitHub Actions

### Code review não funciona:

1. Verifique permissões do `GITHUB_TOKEN`
2. Verifique se há arquivos Kotlin modificados
3. Verifique os logs do workflow

## 📚 Recursos

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/)
- [CodeQL Documentation](https://codeql.github.com/docs/)
- [Codecov Documentation](https://docs.codecov.com/)

## 🤝 Contribuindo

Ao adicionar novos workflows ou modificar existentes:

1. Teste localmente primeiro
2. Documente mudanças neste README
3. Mantenha compatibilidade com branches protegidas
4. Siga os padrões de nomenclatura existentes

