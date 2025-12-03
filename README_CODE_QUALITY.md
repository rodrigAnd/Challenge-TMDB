# Code Quality Tools

Este projeto utiliza ferramentas de qualidade de código para garantir boas práticas de desenvolvimento.

## KTLint

KTLint é um linter e formatador de código Kotlin que garante consistência no estilo de código.

### Comandos disponíveis:

- `./gradlew ktlintCheck` - Verifica se o código está formatado corretamente
- `./gradlew ktlintFormat` - Formata automaticamente o código
- `./gradlew ktlintFormatCheck` - Verifica formatação sem modificar arquivos

### Configuração:

- Arquivo `.editorconfig` na raiz do projeto
- Configuração no `app/build.gradle.kts`

## Detekt

Detekt é uma ferramenta de análise estática de código Kotlin que detecta code smells, complexidade e problemas de código.

### Comandos disponíveis:

- `./gradlew detekt` - Executa análise estática
- `./gradlew detektBaseline` - Gera baseline de problemas conhecidos

### Configuração:

- Arquivo `config/detekt/detekt.yml` - Regras e configurações
- Arquivo `config/detekt/baseline.xml` - Baseline de problemas conhecidos

## GitHub Actions

As verificações de qualidade são executadas automaticamente:

- **PR para develop**: Executa ktlint e detekt
- **Push para develop**: Executa ktlint e detekt
- **PR para master**: Executa ktlint e detekt antes do merge

## Boas Práticas

1. Execute `./gradlew ktlintFormat` antes de fazer commit
2. Verifique os relatórios do detekt antes de criar PRs
3. Mantenha o baseline do detekt atualizado quando necessário
