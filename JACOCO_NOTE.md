# ⚠️ Nota sobre Jacoco

O plugin Jacoco está causando um conflito com a configuração atual do Gradle/Android, 
impedindo a criação da tarefa `testDebugUnitTest`.

## Solução Temporária

O plugin Jacoco foi removido para manter o build funcionando. 

## Alternativas para Cobertura de Testes

1. **Usar ferramentas externas**: Codecov, Coveralls, etc.
2. **Usar plugins alternativos**: Kover (cobertura nativa do Kotlin)
3. **Aguardar atualização**: O problema pode ser resolvido em versões futuras do Gradle/Android

## Status Atual

- ✅ Build do app funcionando
- ✅ KTLint e Detekt configurados
- ⚠️ Jacoco temporariamente desabilitado

O projeto continua funcional e as verificações de qualidade (KTLint/Detekt) 
funcionam normalmente no GitHub Actions.
