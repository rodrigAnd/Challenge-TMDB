# Configuração do JaCoCo

O JaCoCo (Java Code Coverage) foi configurado no projeto para análise de cobertura de código.

## Tarefas Disponíveis

### Gerar Relatório de Cobertura

Para gerar o relatório de cobertura de código após executar os testes:

```bash
./gradlew :app:testDebugUnitTest :app:jacocoTestReport
```

Ou simplesmente:

```bash
./gradlew :app:jacocoTestReport
```

Esta tarefa automaticamente executa os testes unitários antes de gerar o relatório.

### Verificar Cobertura Mínima

Para verificar se a cobertura atende aos requisitos mínimos:

```bash
./gradlew :app:jacocoTestCoverageVerification
```

**Nota:** Atualmente, a cobertura mínima está configurada como 0.0% (sem restrições). Você pode ajustar isso no arquivo `app/build.gradle.kts` na tarefa `jacocoTestCoverageVerification`.

### Relatórios Gerados

Os relatórios são gerados em:

- **HTML:** `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- **XML:** `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`
- **CSV:** Desabilitado por padrão

## Arquivos Excluídos da Cobertura

Os seguintes tipos de arquivos são automaticamente excluídos da análise de cobertura:

- Classes geradas (`R.class`, `BuildConfig`, `Manifest`)
- Classes de teste (`*Test*`)
- Classes geradas pelo Hilt (`*_Hilt*`, `*_Factory*`, etc.)
- Classes de binding/databinding
- Mappers e ViewObjects
- Classes do pacote `android/**`
- Classes do pacote `models/**`
- Classes do pacote `di/**`

## Configuração

A configuração do JaCoCo está no arquivo `app/build.gradle.kts`:

- Versão do JaCoCo: 0.8.11
- Cobertura habilitada no buildType `debug`
- Relatórios em formato HTML e XML

## Próximos Passos

1. Execute os testes: `./gradlew :app:testDebugUnitTest`
2. Gere o relatório: `./gradlew :app:jacocoTestReport`
3. Abra o relatório HTML no navegador para visualizar a cobertura
4. Ajuste os limites mínimos de cobertura conforme necessário

## Integração com CI/CD

Para usar em pipelines de CI/CD, você pode:

1. Executar os testes e gerar o relatório
2. Publicar o relatório XML para ferramentas como SonarQube, Codecov, etc.
3. Configurar verificações de cobertura mínima para bloquear merges

