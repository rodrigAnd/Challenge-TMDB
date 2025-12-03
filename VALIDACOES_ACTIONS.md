# 📋 Validações das GitHub Actions

Este documento lista todas as validações que são executadas automaticamente pelas GitHub Actions.

## 🔍 Workflows Disponíveis

### 1. 🤖 Code Review Automatizado (`code-review.yml`)

**Quando executa**: PRs para `develop` ou `master`

**Validações realizadas**:

#### ✅ Análise de Código Kotlin
- **Documentação KDoc**: Verifica se classes e funções públicas têm documentação KDoc
- **Tratamento de Erros**: Verifica se funções suspensas têm tratamento adequado (Result<T> ou try-catch)
- **Uso de Log**: Sugere mover tags de Log para companion object
- **Nomenclatura**: Verifica se classes começam com maiúscula
- **Complexidade**: Identifica funções muito longas (>50 linhas)
- **Imports**: Verifica imports não utilizados

#### 📊 O que faz:
1. Analisa todos os arquivos `.kt` modificados no PR
2. Cria comentário detalhado com sugestões
3. Cria review automático no PR
4. Classifica problemas em: Erros ❌, Avisos ⚠️, Sugestões 💡

**Resultado**: 
- ✅ Aprova automaticamente se não houver problemas
- ⚠️ Solicita mudanças se houver erros críticos
- 💬 Comenta se houver apenas sugestões

---

### 2. 📏 Code Quality (`code-quality.yml`)

**Quando executa**: PRs e pushes para `develop` ou `master`

**Validações realizadas**:

#### ✅ KTLint Check
- **Formatação de Código**: Verifica se o código segue o padrão Kotlin
- **Estilo de Código**: Valida indentação, espaçamento, quebras de linha
- **Regras Experimentais**: Verifica regras avançadas de formatação

#### ✅ Detekt Analysis
- **Análise Estática**: Detecta code smells e problemas de código
- **Complexidade**: Verifica complexidade ciclomática
- **Naming**: Valida nomenclatura de classes, funções, variáveis
- **Performance**: Detecta problemas de performance
- **Potential Bugs**: Identifica bugs potenciais
- **Style**: Verifica estilo de código (linha muito longa, etc)

**Resultado**: 
- ⚠️ Não bloqueia merge (apenas informa)
- 📊 Gera relatórios HTML para download
- 💡 Mostra problemas encontrados nos logs

---

### 3. 🔄 PR to Develop (`pr-to-develop.yml`)

**Quando executa**: PRs para `develop`

**Validações realizadas**:

#### ✅ Code Review Automatizado
- Mesmas validações do workflow `code-review.yml`
- Análise de código Kotlin
- Verificação de padrões e boas práticas

#### ✅ Build e Testes Unitários
- **Compilação**: Verifica se o código compila sem erros
- **Testes Unitários**: Executa todos os testes unitários
- **Cobertura de Testes**: Verifica se cobertura está ≥ 90%
  - ❌ **Bloqueia merge** se cobertura < 90%
  - ✅ Permite merge se cobertura ≥ 90%

#### ✅ Validação de Qualidade
- **KTLint Check**: Formatação de código
- **Detekt**: Análise estática
- **Android Lint**: Verifica problemas específicos do Android

#### ✅ Verificação de Aprovações
- Verifica se todos os jobs passaram
- Mostra resumo das validações

**Resultado**: 
- ✅ Permite merge se tudo passar
- ❌ Bloqueia se cobertura < 90%
- ⚠️ Informa problemas de qualidade (não bloqueia)

---

### 4. 🚀 Merge to Master (`merge-to-master.yml`)

**Quando executa**: PRs para `master`

**Validações realizadas**:

#### ✅ Validação de Origem
- **Origem do PR**: ❌ **Bloqueia** se PR não vier de `develop`
- **Base Branch**: Verifica se base é `master`

#### ✅ Build Completo
- **Compilação**: Build completo do projeto
- **Testes**: Todos os testes (debug e release)

#### ✅ Cobertura de Testes
- **Cobertura Mínima**: ❌ **Bloqueia** se < 90%
- **Relatório**: Gera relatório detalhado

#### ✅ CodeQL Security
- **Análise de Segurança**: Verifica vulnerabilidades
- **CodeQL**: Análise estática de segurança

#### ✅ Verificação de Aprovações
- **Reviews**: Verifica se PR tem aprovações necessárias
- **Status Checks**: Verifica se todos os checks passaram
- **Solicitações de Mudança**: ❌ **Bloqueia** se houver

**Resultado**: 
- ✅ Permite merge apenas se:
  - PR vem de `develop` ✅
  - Cobertura ≥ 90% ✅
  - Tem aprovações ✅
  - Todos os checks passaram ✅
- ❌ Bloqueia em qualquer outro caso

---

### 5. 📝 Simple Quality Check (`simple-quality-check.yml`)

**Quando executa**: PRs para `develop` ou `master`

**Validações realizadas**:
- **Arquivos Modificados**: Lista arquivos Kotlin modificados
- **Verificação Básica**: Verificação simples sem Gradle

**Resultado**: 
- ✅ Apenas informa arquivos modificados
- 💡 Útil para debug

---

## 📊 Resumo das Validações

### ✅ Validações que BLOQUEIAM merge:

1. **Cobertura de Testes < 90%** (PR para develop/master)
2. **PR para master não vem de develop** (merge-to-master)
3. **PR para master sem aprovações** (merge-to-master)
4. **Build falha** (em alguns casos)

### ⚠️ Validações que APENAS INFORMAM:

1. **KTLint** (problemas de formatação)
2. **Detekt** (code smells)
3. **Code Review Bot** (sugestões de melhoria)
4. **Android Lint** (avisos do Android)

### 🤖 Code Review Bot verifica:

- ✅ Documentação KDoc
- ✅ Tratamento de erros
- ✅ Uso de Log
- ✅ Nomenclatura
- ✅ Complexidade de funções
- ✅ Imports não utilizados

---

## 🎯 Fluxo de Validação

```
PR Criado
    ↓
┌─────────────────────────────────────┐
│ 1. Code Review Bot                  │
│    - Analisa código                 │
│    - Cria review automático         │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ 2. Code Quality                     │
│    - KTLint (formatação)            │
│    - Detekt (análise estática)      │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ 3. Build e Testes                   │
│    - Compilação                     │
│    - Testes unitários               │
│    - Cobertura ≥ 90% (BLOQUEIA)     │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ 4. Validação de Qualidade           │
│    - KTLint                         │
│    - Detekt                         │
│    - Android Lint                   │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ 5. Verificação Final                │
│    - Todos os checks passaram?      │
│    - Tem aprovações?                │
└─────────────────────────────────────┘
    ↓
✅ Merge Permitido ou ❌ Bloqueado
```

---

## 📝 Checklist para PRs

Antes de criar um PR, certifique-se:

- [ ] ✅ Código compila (`./gradlew assembleDebug`)
- [ ] ✅ Testes passam (`./gradlew testDebugUnitTest`)
- [ ] ✅ Cobertura ≥ 90%
- [ ] ✅ KTLint passa (`./gradlew ktlintCheck`)
- [ ] ✅ Código formatado (`./gradlew ktlintFormat`)
- [ ] ✅ Documentação KDoc adicionada
- [ ] ✅ Tratamento de erros adequado
- [ ] ✅ Nomenclatura correta

---

## 🔍 Como Ver os Resultados

1. **No GitHub**:
   - Vá para a aba "Actions"
   - Clique no workflow executado
   - Veja os logs de cada job

2. **No PR**:
   - Veja os checks na parte inferior
   - Veja comentários do Code Review Bot
   - Veja o review automático criado

3. **Artifacts**:
   - Relatórios Detekt disponíveis para download
   - Relatórios de cobertura disponíveis

---

## 💡 Dicas

- ✅ Corrija problemas de formatação antes de fazer PR
- ✅ Mantenha cobertura de testes ≥ 90%
- ✅ Adicione documentação KDoc
- ✅ Revise sugestões do Code Review Bot
- ✅ Teste localmente antes de fazer push

