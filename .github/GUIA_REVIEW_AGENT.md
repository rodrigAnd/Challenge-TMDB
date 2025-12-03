# 🤖 Guia: Configurar Agente de Code Review no GitHub

Este guia explica como configurar diferentes tipos de agentes/bots para fazer code review automático no GitHub.

## 📋 Opções Disponíveis

### 1. **GitHub Copilot para Pull Requests** (Recomendado - Nativo)
### 2. **GitHub Actions com Code Review Bot** (Já implementado)
### 3. **CodeQL + GitHub Advanced Security** (Já implementado)
### 4. **Ferramentas de Terceiros** (CodeRabbit, DeepCode, etc.)

---

## 1. 🤖 GitHub Copilot para Pull Requests

### O que é?
O GitHub Copilot pode fazer code review automático em Pull Requests usando IA.

### Como Configurar:

#### Opção A: Via Interface do GitHub (Mais Fácil)

1. **Ativar GitHub Copilot no Repositório**:
   - Vá para: **Settings** → **Copilot**
   - Ative o GitHub Copilot para o repositório
   - Requer assinatura do GitHub Copilot (pago)

2. **Configurar Code Review Automático**:
   - Vá para: **Settings** → **Code security and analysis**
   - Ative: **GitHub Copilot suggestions**
   - Configure para sugerir em Pull Requests

#### Opção B: Via GitHub Actions (Gratuito com Limitações)

O GitHub Copilot também pode ser usado via Actions, mas requer token de autenticação.

---

## 2. ✅ GitHub Actions Code Review Bot (Já Implementado)

Você já tem um code review bot implementado em `pr-to-develop.yml`!

### Como Funciona:

O workflow `pr-to-develop.yml` já inclui um **Code Review Bot** que:
- ✅ Analisa arquivos Kotlin modificados
- ✅ Verifica documentação KDoc
- ✅ Verifica tratamento de erros
- ✅ Verifica padrões de código
- ✅ Cria review automático no PR

### Melhorias Possíveis:

Você pode melhorar o bot atual adicionando mais verificações ou integrando com IA.

---

## 3. 🔒 CodeQL + GitHub Advanced Security (Já Implementado)

Você já tem CodeQL configurado nos workflows!

### Como Funciona:

- `pr-to-develop.yml`: Executa CodeQL em PRs para develop
- `merge-to-master.yml`: Executa CodeQL em PRs para master

### Configuração Adicional:

1. **Ativar GitHub Advanced Security**:
   - Vá para: **Settings** → **Code security and analysis**
   - Ative: **Code scanning**
   - Configure: **CodeQL analysis**

2. **Configurar Alertas**:
   - Vá para: **Security** → **Code scanning alerts**
   - Configure alertas automáticos

---

## 4. 🛠️ Ferramentas de Terceiros

### A. CodeRabbit (Recomendado - Gratuito)

**CodeRabbit** é um bot de code review gratuito e poderoso.

#### Como Configurar:

1. **Instalar CodeRabbit**:
   - Acesse: https://coderabbit.ai
   - Conecte seu repositório GitHub
   - Autorize o acesso

2. **Configurar via GitHub App**:
   - Vá para: **Settings** → **Integrations** → **GitHub Apps**
   - Procure por "CodeRabbit"
   - Instale e configure

3. **Configurar via Actions** (Alternativa):

```yaml
# .github/workflows/coderabbit.yml
name: CodeRabbit Review

on:
  pull_request:
    types: [opened, synchronize, reopened]

jobs:
  review:
    runs-on: ubuntu-latest
    steps:
      - uses: coderabbitai/openai-pr-reviewer@latest
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
```

### B. DeepCode / Snyk Code

**Snyk Code** (anteriormente DeepCode) oferece análise de código.

#### Como Configurar:

1. Acesse: https://snyk.io
2. Conecte seu repositório GitHub
3. Configure para criar PRs com sugestões

### C. SonarCloud

**SonarCloud** oferece análise de qualidade de código.

#### Como Configurar:

1. Acesse: https://sonarcloud.io
2. Conecte seu repositório GitHub
3. Configure o workflow:

```yaml
# .github/workflows/sonarcloud.yml
name: SonarCloud Analysis

on:
  pull_request:
    types: [opened, synchronize, reopened]

jobs:
  sonarcloud:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      
      - name: SonarCloud Scan
        uses: SonarSource/sonarcloud-github-action@master
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
```

---

## 🚀 Configuração Recomendada: Melhorar o Bot Atual

Vou criar uma versão melhorada do seu code review bot que usa IA (OpenAI) para análises mais inteligentes:

### Opção 1: Usar OpenAI API (Requer Chave)

```yaml
# Adicionar ao pr-to-develop.yml
- name: AI Code Review
  uses: actions/github-script@v7
  env:
    OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
  with:
    script: |
      // Código para usar OpenAI para análise mais inteligente
```

### Opção 2: Usar GitHub Copilot API (Requer Token)

```yaml
# Adicionar ao pr-to-develop.yml
- name: Copilot Code Review
  uses: actions/github-script@v7
  env:
    GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
    COPILOT_TOKEN: ${{ secrets.COPILOT_TOKEN }}
  with:
    script: |
      // Código para usar GitHub Copilot API
```

---

## 📝 Configuração Passo a Passo: CodeRabbit (Gratuito)

### Passo 1: Criar Conta

1. Acesse: https://coderabbit.ai
2. Faça login com GitHub
3. Autorize o acesso

### Passo 2: Conectar Repositório

1. No CodeRabbit, clique em **"Add Repository"**
2. Selecione seu repositório `MyChallenge`
3. Configure as opções:
   - ✅ Enable code review
   - ✅ Enable suggestions
   - ✅ Auto-approve if no issues

### Passo 3: Configurar no GitHub

1. Vá para: **Settings** → **Integrations** → **GitHub Apps**
2. Procure por "CodeRabbit"
3. Configure permissões:
   - ✅ Read pull requests
   - ✅ Write pull request reviews
   - ✅ Read repository contents

### Passo 4: Testar

1. Crie um PR de teste
2. O CodeRabbit automaticamente fará review
3. Veja os comentários no PR

---

## 📝 Configuração Passo a Passo: Melhorar Bot Atual com IA

Vou criar uma versão melhorada do seu bot que usa análise mais inteligente:

### Passo 1: Adicionar Secret (se usar OpenAI)

1. Vá para: **Settings** → **Secrets and variables** → **Actions**
2. Adicione: `OPENAI_API_KEY` (se usar OpenAI)
3. Ou use apenas análise baseada em regras (sem IA)

### Passo 2: Melhorar o Bot Atual

O bot atual já é bom, mas podemos adicionar:
- ✅ Análise de segurança
- ✅ Análise de performance
- ✅ Sugestões de refatoração
- ✅ Verificação de testes

---

## 🎯 Recomendação Final

### Para Projeto Gratuito:
1. ✅ **Manter o bot atual** (`pr-to-develop.yml`) - já funciona bem
2. ✅ **Adicionar CodeRabbit** - gratuito e complementa
3. ✅ **Manter CodeQL** - já configurado

### Para Projeto com Orçamento:
1. ✅ **GitHub Copilot** - melhor integração
2. ✅ **SonarCloud** - análise profissional
3. ✅ **Manter CodeQL** - segurança

---

## 🔧 Próximos Passos

1. **Escolher ferramenta**: CodeRabbit (gratuito) ou GitHub Copilot (pago)
2. **Configurar**: Seguir passos acima
3. **Testar**: Criar PR de teste
4. **Ajustar**: Configurar regras e preferências

---

## 📚 Recursos

- [GitHub Copilot Docs](https://docs.github.com/en/copilot)
- [CodeRabbit](https://coderabbit.ai)
- [CodeQL Docs](https://codeql.github.com/docs/)
- [SonarCloud](https://sonarcloud.io)
- [GitHub Actions Docs](https://docs.github.com/en/actions)

