# 🌿 Fluxo Git - Git Flow

Este projeto utiliza o padrão **Git Flow** para gerenciamento de branches.

## 📋 Estrutura de Branches

```
master (produção)
  ↑
develop (desenvolvimento)
  ↑
feature/* (features individuais)
```

## 🔄 Fluxo de Trabalho

### 1. Criar Feature Branch

```bash
# Criar branch a partir da develop
git checkout develop
git pull origin develop
git checkout -b feature/minha-feature

# Ou usar o script auxiliar
./scripts/create-feature-branch.sh minha-feature
```

### 2. Desenvolver na Feature Branch

```bash
# Fazer commits normalmente
git add .
git commit -m "feat: adiciona nova funcionalidade"

# Push para o remoto
git push origin feature/minha-feature
```

### 3. Criar Pull Request para Develop

1. Vá para o GitHub
2. Crie um PR de `feature/minha-feature` → `develop`
3. As GitHub Actions executarão automaticamente:
   - ✅ Code Review Automatizado
   - ✅ Build e Testes Unitários
   - ✅ Validação de Qualidade (KTLint, Detekt)
   - ✅ Verificação de Cobertura de Testes (90% mínimo)

4. Aguarde aprovação e merge

### 4. Merge para Master (apenas da develop)

```bash
# Criar PR de develop → master
# No GitHub, crie um PR de develop para master
```

**⚠️ IMPORTANTE:** A branch `master` está protegida e só aceita merges da `develop`!

## 🔒 Proteções de Branch

### Branch `master`
- ✅ **Bloqueada** para commits diretos
- ✅ **Requer PR** para merge
- ✅ **Requer aprovação** de review
- ✅ **Só aceita merges** da branch `develop`
- ✅ **Requer** que todos os checks passem
- ✅ **Requer** resolução de conversas

### Branch `develop`
- ✅ Permite PRs de feature branches
- ✅ Requer que checks passem
- ✅ Code review automático executado

## 🚀 Scripts Auxiliares

### Criar Feature Branch

```bash
./scripts/create-feature-branch.sh nome-da-feature
```

### Configurar Branch Protection

```bash
./.github/setup-branch-protection.sh
```

**Requisitos:**
- GitHub CLI (`gh`) instalado
- Autenticado no GitHub (`gh auth login`)

## 📝 Convenções de Commit

Seguir o padrão [Conventional Commits](https://www.conventionalcommits.org/):

- `feat:` Nova funcionalidade
- `fix:` Correção de bug
- `docs:` Documentação
- `style:` Formatação (não afeta código)
- `refactor:` Refatoração
- `test:` Testes
- `chore:` Tarefas de manutenção

Exemplos:
```bash
git commit -m "feat: adiciona tela de detalhes do filme"
git commit -m "fix: corrige crash ao carregar imagens"
git commit -m "test: adiciona testes para MovieRepository"
```

## ✅ Checklist para PR

Antes de criar um PR, certifique-se:

- [ ] ✅ Código segue os padrões (KTLint, Detekt)
- [ ] ✅ Testes unitários passam
- [ ] ✅ Cobertura de testes ≥ 90%
- [ ] ✅ Documentação atualizada (se necessário)
- [ ] ✅ Commits seguem convenção
- [ ] ✅ Branch está atualizada com develop

## 🔍 Verificar Status

```bash
# Ver branches locais
git branch

# Ver branches remotas
git branch -r

# Ver todas as branches
git branch -a

# Verificar status do repositório
git status

# Verificar diferenças com develop
git diff develop
```

## 🐛 Troubleshooting

### Erro: "master está protegida"

**Solução:** Certifique-se de que o PR vem da `develop`:
```bash
git checkout develop
git pull origin develop
# Criar PR de develop → master
```

### Erro: "Checks falhando"

**Solução:**
1. Verifique os logs das Actions no GitHub
2. Execute localmente: `./gradlew ktlintCheck detekt`
3. Corrija os problemas identificados
4. Faça push novamente

### Branch desatualizada

**Solução:**
```bash
git checkout feature/minha-feature
git fetch origin
git merge origin/develop
# Resolver conflitos se houver
git push origin feature/minha-feature
```

## 📚 Referências

- [Git Flow](https://nvie.com/posts/a-successful-git-branching-model/)
- [Conventional Commits](https://www.conventionalcommits.org/)
- [GitHub Flow](https://guides.github.com/introduction/flow/)

