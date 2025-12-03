# 🔧 Como Corrigir GitHub Actions

## ⚠️ Problemas Comuns e Soluções

### 1. Erro: "Gradle task failed"

**Causa**: Problemas na configuração do Gradle ou dependências

**Solução**:
```bash
# Limpar projeto localmente
./gradlew clean

# Verificar se compila
./gradlew assembleDebug

# Verificar ktlint localmente
./gradlew ktlintCheck || echo "KTLint encontrou problemas"

# Verificar detekt localmente  
./gradlew detekt || echo "Detekt encontrou problemas"
```

### 2. Erro: "KTLint/Detekt não encontrado"

**Causa**: Plugins não aplicados corretamente

**Solução**: Verifique `app/build.gradle.kts`:
```kotlin
plugins {
    // ...
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}
```

### 3. Erro: "Permission denied"

**Solução**: O workflow já adiciona `chmod +x gradlew`, mas se persistir:
```yaml
- name: Grant execute permission
  run: chmod +x gradlew && ls -la gradlew
```

### 4. Code Review não aparece

**Causa**: Permissões do GITHUB_TOKEN

**Solução**: Verifique se o workflow tem:
```yaml
permissions:
  contents: read
  pull-requests: write
  issues: write
```

## 🛠️ Workflows Criados

### ✅ code-review.yml
- Code Review Bot automático
- Analisa código e cria reviews
- Funciona independente do Gradle

### ✅ code-quality.yml  
- Executa KTLint e Detekt
- Não bloqueia merge (continue-on-error)
- Apenas informa problemas

### ✅ simple-quality-check.yml
- Verificação básica sem Gradle
- Útil para debug

## 🧪 Testar Localmente

```bash
# Testar KTLint
./gradlew ktlintCheck

# Testar Detekt
./gradlew detekt

# Se der erro, ver stacktrace
./gradlew ktlintCheck --stacktrace
./gradlew detekt --stacktrace
```

## 📝 Checklist de Correção

- [ ] Código compila localmente (`./gradlew assembleDebug`)
- [ ] KTLint funciona localmente (`./gradlew ktlintCheck`)
- [ ] Detekt funciona localmente (`./gradlew detekt`)
- [ ] Workflows têm `continue-on-error` onde apropriado
- [ ] Permissões corretas nos workflows
- [ ] `gradlew` tem permissão de execução

## 🚀 Próximos Passos

1. Teste localmente primeiro
2. Faça commit das correções
3. Crie PR de teste
4. Observe os logs no GitHub Actions
5. Ajuste conforme necessário
