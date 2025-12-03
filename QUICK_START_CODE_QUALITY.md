# 🚀 Quick Start - Ferramentas de Qualidade

## Comandos Rápidos

### Antes de cada commit:

```bash
# 1. Formatar código automaticamente
./gradlew ktlintFormat

# 2. Verificar qualidade (opcional, mas recomendado)
./gradlew detekt
```

### Verificar formatação:

```bash
./gradlew ktlintCheck
```

### Ver relatórios:

```bash
# Abrir relatório Detekt no navegador (macOS)
open app/build/reports/detekt/detekt.html

# Ou no Linux
xdg-open app/build/reports/detekt/detekt.html
```

## ⚠️ Se o build falhar no CI/CD

1. Execute `./gradlew ktlintFormat` localmente
2. Execute `./gradlew detekt` e corrija os problemas
3. Faça commit das correções
4. Push novamente

## 📝 Dica

Configure seu IDE para formatar automaticamente ao salvar usando o `.editorconfig` do projeto.
