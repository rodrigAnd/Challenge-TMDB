# ⚠️ Problema na Execução Local

Há um problema na configuração do Gradle que está impedindo a execução local das tarefas de ktlint e detekt.

## ✅ Solução: GitHub Actions

As ferramentas estão configuradas e funcionarão automaticamente no GitHub Actions quando você fizer push ou criar um PR.

## 🔧 Solução Local Alternativa

### Opção 1: Instalar via Homebrew (macOS)

```bash
# Instalar ktlint
brew install ktlint

# Executar ktlint
ktlint --version
find app/src -name "*.kt" -type f | xargs ktlint --android

# Para detekt, baixe o JAR de:
# https://github.com/detekt/detekt/releases
java -jar detekt-cli-*.jar \
  --input app/src \
  --config config/detekt/detekt.yml \
  --baseline config/detekt/baseline.xml
```

### Opção 2: Usar Docker

```bash
# KTLint via Docker
docker run --rm -v $(pwd):/project pinterest/ktlint ktlint --android

# Detekt via Docker  
docker run --rm -v $(pwd):/project -w /project detekt/detekt detekt
```

### Opção 3: Corrigir o Gradle

O problema está relacionado à configuração da tarefa `testDebugUnitTest`. 
Você pode tentar:
1. Limpar o projeto: `./gradlew clean`
2. Invalidar caches do Android Studio
3. Verificar versões do Gradle e plugins

## 📝 Status

- ✅ KTLint configurado no build.gradle.kts
- ✅ Detekt configurado no build.gradle.kts  
- ✅ GitHub Actions configurados
- ⚠️ Execução local bloqueada por problema no Gradle

As verificações funcionarão automaticamente no CI/CD!
