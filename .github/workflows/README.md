# 📋 GitHub Actions - Workflows

## 🔍 Workflows Disponíveis (Consolidados)

### 1. PR to Develop (`pr-to-develop.yml`)
- **Quando executa**: PRs para `develop`
- **O que faz**:
  - ✅ **Code Review Automatizado Completo**
    - Analisa código Kotlin automaticamente
    - Verifica documentação KDoc
    - Verifica tratamento de erros
    - Verifica padrões de código
    - Verifica nomenclatura e complexidade
    - Cria review automático no PR
  - ✅ **Build e Testes Unitários**
    - Compilação do projeto
    - Execução de testes unitários
    - Geração de relatório de cobertura
    - **Bloqueia merge** se cobertura < 90%
  - ✅ **Validação de Qualidade**
    - KTLint (formatação)
    - Detekt (análise estática)
    - Android Lint
    - Apenas informa (não bloqueia)

### 2. Merge to Master (`merge-to-master.yml`)
- **Quando executa**: PRs para `master`
- **O que faz**:
  - ✅ **Validação de Origem**
    - Valida que PR vem de `develop` (também configurar Branch Protection)
  - ✅ **Validação Rigorosa**
    - KTLint Check
    - Detekt Analysis
    - Build completo (debug + release)
    - Testes completos
    - Cobertura ≥ 90% (BLOQUEIA)
  - ✅ **CodeQL Security**
    - Análise de segurança (Kotlin/Java)
  - ✅ **Verificação de Aprovações**
    - Requer pelo menos uma aprovação
    - Verifica se todos os checks passaram

### 3. Code Quality (`code-quality.yml`)
- **Quando executa**: Pushes para `develop` ou `master` (não PRs)
- **O que faz**:
  - ✅ KTLint Check (formatação)
  - ✅ Detekt Analysis (análise estática)
  - ✅ Apenas informa (não bloqueia)
  - ✅ Gera relatórios HTML para download

## 📊 Fluxo de Validação

```
PR para develop
    ↓
┌─────────────────────────────────────┐
│ pr-to-develop.yml                   │
│  - Code Review Completo             │
│  - Build + Testes                   │
│  - Cobertura ≥ 90% (BLOQUEIA)       │
│  - KTLint + Detekt (informa)        │
└─────────────────────────────────────┘
    ↓
✅ Merge em develop

Push para develop/master
    ↓
┌─────────────────────────────────────┐
│ code-quality.yml                    │
│  - KTLint + Detekt (informa)        │
└─────────────────────────────────────┘

PR para master (deve vir de develop)
    ↓
┌─────────────────────────────────────┐
│ merge-to-master.yml                 │
│  - Validação rigorosa               │
│  - Build completo                   │
│  - Testes + Cobertura ≥ 90%        │
│  - CodeQL Security                  │
│  - Requer aprovações                │
└─────────────────────────────────────┘
    ↓
✅ Merge em master
```

## ✨ Melhorias Implementadas

- ✅ **Consolidação**: Reduzido de 6 para 3 workflows
- ✅ **Eliminação de redundâncias**: Cada validação roda apenas uma vez
- ✅ **Code Review completo**: Análise detalhada consolidada em `pr-to-develop.yml`
- ✅ **Separação de responsabilidades**: Workflows claros e específicos

## 🤖 Code Review Adicional: CodeRabbit

Além do code review automatizado nos workflows, você pode configurar o **CodeRabbit** para reviews adicionais com IA:

- 📝 **Guia rápido**: `.github/QUICK_START_CODERABBIT.md`
- 📚 **Configuração completa**: `.github/CODERABBIT_SETUP.md`
- ⚙️ **Configuração**: `.coderabbit.yaml` (já criado)

O CodeRabbit complementa o bot atual e oferece análises mais inteligentes usando IA.

## ⚠️ Troubleshooting

### Erro: "Task failed"
- Verifique os logs completos no GitHub
- Execute localmente: `./gradlew ktlintCheck detekt`
- Corrija problemas antes de fazer push

### Erro: "Gradle build failed"
- Verifique se o código compila localmente
- Execute: `./gradlew clean build`

### Code Review não aparece
- Verifique se o workflow executou
- Verifique permissões do GITHUB_TOKEN
- Veja logs do job "code-review"
