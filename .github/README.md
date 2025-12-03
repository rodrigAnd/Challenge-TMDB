# 📚 Documentação GitHub Actions e Code Review

Este diretório contém toda a documentação e configurações relacionadas a GitHub Actions e Code Review automatizado.

## 📋 Documentação Disponível

### 🚀 Quick Starts

- **`.github/QUICK_START_CODERABBIT.md`** - Configuração rápida do CodeRabbit (3 passos)
- **`.github/CONFIGURAR_REVIEW_AGENT.md`** - Guia rápido de configuração de review agents

### 📚 Guias Completos

- **`.github/GUIA_REVIEW_AGENT.md`** - Guia completo sobre todas as opções de review agents
- **`.github/CODERABBIT_SETUP.md`** - Passo a passo detalhado para configurar CodeRabbit
- **`.github/workflows/README.md`** - Documentação dos workflows GitHub Actions
- **`.github/workflows/ANALISE_ACTIONS.md`** - Análise crítica das actions (antes/depois)
- **`.github/workflows/CHANGELOG_MELHORIAS.md`** - Changelog das melhorias implementadas

### ⚙️ Configurações

- **`.coderabbit.yaml`** - Configuração do CodeRabbit
- **`.github/workflows/`** - Workflows GitHub Actions

### 🔧 Scripts

- **`.github/scripts/check-coderabbit.sh`** - Script para verificar se CodeRabbit está configurado

---

## 🎯 O Que Você Precisa Fazer Agora

### Para Configurar CodeRabbit:

1. **Acesse**: https://github.com/apps/coderabbitai
2. **Clique em "Install"**
3. **Selecione** o repositório `MyChallenge`
4. **Autorize** as permissões
5. **Pronto!** 🎉

Veja o guia completo: `.github/CODERABBIT_SETUP.md`

---

## 📊 Workflows Atuais

### 1. `pr-to-develop.yml`
- Code Review automatizado completo
- Build + Testes
- Validação de cobertura ≥ 90%

### 2. `merge-to-master.yml`
- Validação rigorosa
- CodeQL Security
- Requer aprovações

### 3. `code-quality.yml`
- KTLint + Detekt
- Apenas em pushes

---

## 🤖 Code Review Agents

### Bot Atual (GitHub Actions)
- ✅ Já configurado e funcionando
- ✅ Análise baseada em regras
- ✅ Verifica KDoc, tratamento de erros, etc.

### CodeRabbit (Recomendado)
- ✅ Gratuito
- ✅ Usa IA para análise
- ✅ Complementa o bot atual
- 📝 Veja: `.github/QUICK_START_CODERABBIT.md`

---

## 🔗 Links Úteis

- **CodeRabbit**: https://github.com/apps/coderabbitai
- **GitHub Actions**: https://docs.github.com/en/actions
- **CodeQL**: https://codeql.github.com/docs/

---

## 📝 Próximos Passos

1. ✅ Configure CodeRabbit (veja guia acima)
2. ✅ Teste em um PR
3. ✅ Ajuste configurações conforme necessário
4. ✅ Integre com seu workflow

