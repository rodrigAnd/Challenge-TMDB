# 📋 GitHub Actions - Workflows

## 🔍 Workflows Disponíveis

### 1. Code Review Automatizado (`code-review.yml`)
- **Quando executa**: PRs para `develop` ou `master`
- **O que faz**: 
  - Analisa código Kotlin automaticamente
  - Verifica documentação KDoc
  - Verifica tratamento de erros
  - Verifica padrões de código
  - Cria review automático no PR

### 2. Code Quality (`code-quality.yml`)
- **Quando executa**: PRs e pushes para `develop` ou `master`
- **O que faz**:
  - Executa KTLint (formatação)
  - Executa Detekt (análise estática)
  - Não bloqueia merge (apenas informa)

### 3. PR to Develop (`pr-to-develop.yml`)
- **Quando executa**: PRs para `develop`
- **O que faz**:
  - Code Review Automatizado
  - Build e Testes
  - Validação de Qualidade
  - Verificação de Cobertura

### 4. Merge to Master (`merge-to-master.yml`)
- **Quando executa**: PRs para `master`
- **O que faz**:
  - Valida que PR vem de `develop`
  - Validação rigorosa
  - Requer aprovação

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
