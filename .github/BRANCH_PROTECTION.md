# 🔒 Proteção de Branches - Guia de Configuração

Este documento descreve como configurar as proteções de branch no GitHub para garantir que apenas código revisado e aprovado seja mergeado na branch `master`.

## 📋 Estrutura de Branches

- **`master`**: Branch de produção, protegida e requer aprovações
- **`develop`**: Branch de desenvolvimento, onde o trabalho diário acontece

## ⚙️ Configuração no GitHub

### 1. Proteção da Branch Master

Acesse: **Settings → Branches → Add rule** (ou edite a regra existente para `master`)

#### Configurações Recomendadas:

```
Branch name pattern: master
```

**Protect matching branches:**
- ✅ Require a pull request before merging
  - ✅ Require approvals: **1** (ou mais conforme necessário)
  - ✅ Dismiss stale pull request approvals when new commits are pushed
  - ✅ Require review from Code Owners (se tiver CODEOWNERS)

- ✅ Require status checks to pass before merging
  - ✅ Require branches to be up to date before merging
  - **Status checks que devem passar:**
    - `Code Review Automatizado`
    - `Build e Testes Unitários`
    - `Validação de Qualidade`
    - `Validação Rigorosa para Master`
    - `codecov/project` (se configurado)

- ✅ Require conversation resolution before merging

- ✅ Require signed commits (opcional, mas recomendado)

- ✅ Require linear history (opcional)

- ✅ Include administrators (recomendado para manter consistência)

- ✅ Allow force pushes: ❌ **NÃO permitir**

- ✅ Allow deletions: ❌ **NÃO permitir**

### 2. Proteção da Branch Develop

```
Branch name pattern: develop
```

**Protect matching branches:**
- ✅ Require a pull request before merging
  - ✅ Require approvals: **0** (ou 1 se preferir)
  - ✅ Dismiss stale pull request approvals when new commits are pushed

- ✅ Require status checks to pass before merging
  - ✅ Require branches to be up to date before merging
  - **Status checks que devem passar:**
    - `Validação Contínua`
    - `Build e Testes Unitários`

- ✅ Require conversation resolution before merging

- ✅ Include administrators

- ✅ Allow force pushes: ❌ **NÃO permitir**

- ✅ Allow deletions: ❌ **NÃO permitir**

## 🔄 Fluxo de Trabalho Recomendado

### Para Desenvolvimento Normal:

1. **Criar feature branch** a partir de `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/nova-funcionalidade
   ```

2. **Desenvolver e commitar**:
   ```bash
   git add .
   git commit -m "feat: adiciona nova funcionalidade"
   ```

3. **Criar PR para `develop`**:
   - O workflow `pr-to-develop.yml` será executado automaticamente
   - Code review automatizado será feito
   - Testes serão executados
   - Cobertura de testes será verificada (meta: 90%)

4. **Após aprovação**, fazer merge em `develop`

### Para Release/Deploy:

1. **Criar PR de `develop` para `master`**:
   - O workflow `merge-to-master.yml` será executado
   - Validações mais rigorosas serão aplicadas
   - Requer aprovação manual
   - Cobertura mínima de 90% é obrigatória

2. **Após todas as validações passarem e aprovação**, fazer merge em `master`

## 📊 Requisitos de Cobertura

- **Mínimo para `develop`**: Recomendado 90%, mas não bloqueia
- **Mínimo para `master`**: **Obrigatório 90%** - bloqueia merge se não atingir

## 🤖 Code Review Automatizado

O code review automatizado verifica:

- ✅ Documentação KDoc adequada
- ✅ Tratamento de erros em funções suspensas
- ✅ Uso adequado de Logging
- ✅ Análise estática com CodeQL
- ✅ Qualidade geral do código

## ⚠️ Troubleshooting

### Erro: "Required status check is missing"

**Solução**: Certifique-se de que todos os workflows estão configurados corretamente e que os status checks estão listados nas proteções de branch.

### Erro: "Cobertura abaixo de 90%"

**Solução**: 
1. Execute `./gradlew jacocoTestReport` localmente
2. Verifique o relatório em `app/build/reports/jacoco/jacocoTestReport/`
3. Adicione mais testes para aumentar a cobertura

### Erro: "PR precisa de aprovação"

**Solução**: Solicite review de um membro da equipe com permissões de aprovação.

## 📝 Notas Importantes

- ⚠️ **Nunca force push** na branch `master` ou `develop`
- ✅ **Sempre crie PRs** para fazer merge
- ✅ **Mantenha a cobertura de testes acima de 90%**
- ✅ **Resolva todos os comentários** do code review antes de fazer merge
- ✅ **Aguarde todas as validações** passarem antes de fazer merge

## 🔗 Links Úteis

- [GitHub Branch Protection Rules](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches/about-protected-branches)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

