# 🧪 Como Testar GitHub Actions

Este guia mostra diferentes formas de testar suas GitHub Actions antes de fazer merge.

## 📋 Métodos de Teste

### 1. **Teste via Push/PR (Recomendado)**

A forma mais simples é fazer um push ou criar um PR:

```bash
# Criar uma branch de teste
git checkout -b test/github-actions

# Fazer uma pequena mudança (ou apenas commit vazio)
git commit --allow-empty -m "test: verificar GitHub Actions"

# Push para o repositório
git push origin test/github-actions

# Criar PR para develop
# No GitHub, crie um PR de test/github-actions para develop
```

**Vantagens:**
- ✅ Testa no ambiente real do GitHub
- ✅ Não requer configuração local
- ✅ Mostra resultados no GitHub

**Desvantagens:**
- ⚠️ Requer push para o repositório
- ⚠️ Pode poluir o histórico de branches

---

### 2. **Teste Local com Act (Ferramenta CLI)**

`act` permite executar GitHub Actions localmente usando Docker.

#### Instalação (macOS):

```bash
# Via Homebrew
brew install act

# Ou via script
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash
```

#### Uso Básico:

```bash
# Listar workflows disponíveis
act -l

# Executar um workflow específico
act pull_request -W .github/workflows/code-quality.yml

# Executar com eventos específicos
act pull_request \
  -e .github/workflows/pr-to-develop.yml \
  --workflows .github/workflows/code-quality.yml

# Executar apenas um job específico
act pull_request -j ktlint

# Executar com variáveis de ambiente
act pull_request -s GITHUB_TOKEN=seu_token_aqui
```

#### Exemplo Completo:

```bash
# Testar workflow de code quality
act pull_request \
  -W .github/workflows/code-quality.yml \
  --container-architecture linux/amd64

# Testar workflow de PR
act pull_request \
  -W .github/workflows/pr-to-develop.yml \
  --eventpath .github/workflows/pr-to-develop.yml
```

**Vantagens:**
- ✅ Testa localmente sem fazer push
- ✅ Feedback rápido
- ✅ Não polui o histórico

**Desvantagens:**
- ⚠️ Requer Docker instalado
- ⚠️ Pode ter diferenças com o ambiente real
- ⚠️ Alguns recursos podem não funcionar (secrets, etc)

---

### 3. **Adicionar workflow_dispatch para Execução Manual**

Você pode adicionar `workflow_dispatch` aos seus workflows para executá-los manualmente:

```yaml
on:
  pull_request:
    branches:
      - develop
  workflow_dispatch:  # Permite execução manual
    inputs:
      test_mode:
        description: 'Modo de teste'
        required: false
        default: 'normal'
```

Depois, você pode executar manualmente no GitHub:
1. Vá para **Actions** no GitHub
2. Selecione o workflow
3. Clique em **Run workflow**
4. Escolha a branch e clique em **Run workflow**

---

### 4. **Teste com Branch de Teste**

Crie uma branch específica para testes:

```bash
# Criar branch de teste
git checkout -b test/actions-validation

# Fazer uma mudança pequena
echo "# Test" >> TEST.md
git add TEST.md
git commit -m "test: validar GitHub Actions"

# Push
git push origin test/actions-validation

# Criar PR para develop
# No GitHub, crie PR e observe as actions executando
```

---

### 5. **Verificar Logs e Status**

#### No GitHub:

1. Vá para **Actions** no seu repositório
2. Clique no workflow que você quer verificar
3. Veja os logs de cada job e step
4. Verifique se há erros ou warnings

#### Via CLI (gh):

```bash
# Instalar GitHub CLI (se não tiver)
brew install gh

# Autenticar
gh auth login

# Ver workflows
gh workflow list

# Ver execuções recentes
gh run list

# Ver logs de uma execução específica
gh run view <run-id> --log

# Ver status de um PR
gh pr checks <pr-number>
```

---

## 🔍 Checklist de Validação

Ao testar, verifique:

- [ ] ✅ Workflow é triggerado corretamente (push/PR)
- [ ] ✅ Todos os jobs executam sem erros
- [ ] ✅ KTLint verifica o código corretamente
- [ ] ✅ Detekt analisa o código corretamente
- [ ] ✅ Relatórios são gerados e disponibilizados
- [ ] ✅ Artifacts são criados (se aplicável)
- [ ] ✅ Status checks aparecem no PR
- [ ] ✅ Mensagens de erro são claras (se houver falhas)

---

## 🐛 Troubleshooting

### Problema: Workflow não executa

**Solução:**
- Verifique se o evento está correto (`on:`)
- Verifique se a branch está correta
- Verifique se o arquivo está em `.github/workflows/`

### Problema: Erro de permissões

**Solução:**
- Verifique se `GITHUB_TOKEN` tem permissões necessárias
- Adicione `permissions:` no workflow se necessário

### Problema: Erro no Gradle

**Solução:**
- Verifique se `gradlew` tem permissão de execução
- Verifique se as versões do JDK estão corretas
- Verifique se o cache do Gradle está funcionando

### Problema: Act não funciona localmente

**Solução:**
- Verifique se Docker está rodando
- Use `act -P ubuntu-latest=catthehacker/ubuntu:act-latest` para imagens atualizadas
- Verifique se os secrets estão configurados

---

## 📝 Exemplo Prático: Testar Code Quality

```bash
# 1. Criar branch de teste
git checkout -b test/code-quality-actions

# 2. Fazer uma mudança pequena
echo "// Test" >> app/src/main/java/com/onboarding/mychallenge/MainActivity.kt
git add .
git commit -m "test: validar code quality actions"

# 3. Push
git push origin test/code-quality-actions

# 4. Criar PR no GitHub
gh pr create --base develop --head test/code-quality-actions --title "Test: Code Quality Actions"

# 5. Observar as actions executando
gh pr checks

# 6. Ver logs se necessário
gh run list
gh run view <run-id> --log
```

---

## 🎯 Dicas

1. **Use branches de teste**: Crie branches específicas para testar actions
2. **Commits vazios**: Use `git commit --allow-empty` para testar sem mudanças
3. **Verifique logs**: Sempre verifique os logs completos para entender erros
4. **Teste incrementalmente**: Teste um workflow por vez
5. **Use act para desenvolvimento**: Use `act` para testes rápidos durante desenvolvimento

---

## 📚 Recursos

- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Act - GitHub Actions Local Testing](https://github.com/nektos/act)
- [GitHub CLI Documentation](https://cli.github.com/manual/)

