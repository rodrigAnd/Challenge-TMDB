# 🔍 Ferramentas de Qualidade de Código

Este projeto utiliza **KTLint** e **Detekt** para garantir qualidade e consistência do código Kotlin.

## 📋 Índice

- [KTLint](#ktlint)
- [Detekt](#detekt)
- [GitHub Actions](#github-actions)
- [Comandos Úteis](#comandos-úteis)

## 🎨 KTLint

**KTLint** é um linter e formatador de código Kotlin que garante consistência no estilo de código seguindo as [oficiais Kotlin style guide](https://kotlinlang.org/docs/coding-conventions.html).

### Configuração

- **Versão**: 1.0.1
- **Configuração**: `app/build.gradle.kts`
- **Arquivo de configuração**: `.editorconfig`
- **Arquivos ignorados**: `.ktlintignore`

### Comandos Disponíveis

```bash
# Verifica se o código está formatado corretamente
./gradlew ktlintCheck

# Formata automaticamente o código
./gradlew ktlintFormat

# Verifica formatação sem modificar arquivos
./gradlew ktlintFormatCheck
```

### Regras Configuradas

- ✅ Suporte para Android
- ✅ Regras experimentais habilitadas
- ✅ Exclusão de arquivos gerados e build
- ✅ Saída colorida no console

## 🔎 Detekt

**Detekt** é uma ferramenta de análise estática de código Kotlin que detecta code smells, complexidade e problemas de código.

### Configuração

- **Versão**: 1.23.1
- **Arquivo de configuração**: `config/detekt/detekt.yml`
- **Baseline**: `config/detekt/baseline.xml`
- **Arquivos ignorados**: `.detektignore`

### Comandos Disponíveis

```bash
# Executa análise estática
./gradlew detekt

# Gera baseline de problemas conhecidos
./gradlew detektBaseline
```

### Regras Configuradas

- **Complexidade**: Limite de 10 para métodos complexos
- **Naming**: Padrões de nomenclatura para classes, funções e variáveis
- **Style**: Comprimento máximo de linha (120 caracteres)
- **Performance**: Detecção de problemas de performance
- **Potential Bugs**: Detecção de bugs potenciais

### Relatórios

Os relatórios são gerados em:
- **HTML**: `app/build/reports/detekt/detekt.html`
- **XML**: `app/build/reports/detekt/detekt.xml`

## 🚀 GitHub Actions

As verificações de qualidade são executadas automaticamente em:

### 1. Pull Request para Develop
- ✅ Executa KTLint Check
- ✅ Executa Detekt
- ✅ Upload de relatórios

### 2. Push para Develop
- ✅ Executa KTLint Check
- ✅ Executa Detekt

### 3. Pull Request para Master
- ✅ Executa KTLint Check
- ✅ Executa Detekt
- ✅ Validação rigorosa antes do merge

### 4. Workflow Dedicado (code-quality.yml)
- ✅ Job separado para KTLint
- ✅ Job separado para Detekt
- ✅ Job de resumo que valida ambos

## 📝 Comandos Úteis

### Antes de fazer commit:

```bash
# 1. Formatar código automaticamente
./gradlew ktlintFormat

# 2. Verificar se há problemas de qualidade
./gradlew detekt

# 3. Verificar formatação novamente
./gradlew ktlintCheck
```

### Verificar relatórios localmente:

```bash
# Abrir relatório HTML do Detekt
open app/build/reports/detekt/detekt.html
```

## 🔧 Configuração de IDE

### IntelliJ IDEA / Android Studio

1. Instale o plugin **KTLint** (se disponível)
2. Configure o **EditorConfig** para usar o `.editorconfig` do projeto
3. Habilite "Reformat code on save" nas configurações

### VS Code

1. Instale a extensão **Kotlin Language**
2. Instale a extensão **EditorConfig for VS Code**

## 📚 Referências

- [KTLint GitHub](https://github.com/pinterest/ktlint)
- [Detekt GitHub](https://github.com/detekt/detekt)
- [Kotlin Style Guide](https://kotlinlang.org/docs/coding-conventions.html)
