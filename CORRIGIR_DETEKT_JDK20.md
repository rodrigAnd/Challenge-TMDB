# 🔧 Corrigir Detekt com JDK 20

## Problema

O Detekt está tentando usar JVM target 24 (do JDK do sistema) quando o projeto usa JDK 20. O Detekt só suporta até JVM target 20.

## Solução

O Detekt herda automaticamente o `kotlinOptions.jvmTarget = "20"` configurado no `android` block do `app/build.gradle.kts`. No entanto, quando o sistema usa JDK > 20, o Detekt pode tentar usar o JDK do sistema.

### Opção 1: Usar o script fornecido (Recomendado)

Execute o script que força o uso do JDK 20:

```bash
./scripts/run-detekt-jdk20.sh
```

### Opção 2: Configurar JAVA_HOME manualmente

Antes de rodar o Detekt, configure o JAVA_HOME para JDK 20:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 20)
./gradlew --stop
./gradlew detekt
```

### Opção 3: Configurar no Android Studio

1. Abra **File > Settings** (ou **Android Studio > Preferences** no macOS)
2. Vá em **Build, Execution, Deployment > Build Tools > Gradle**
3. Em **Gradle JDK**, selecione **corretto-20** (ou JDK 20)
4. Clique em **Apply** e **OK**
5. Execute o Detekt novamente

## Configuração Atual

- **JDK do Projeto**: 20 (configurado em `.idea/misc.xml` e `.idea/gradle.xml`)
- **JVM Target**: 20 (configurado em `app/build.gradle.kts`)
- **Kotlin Options**: `jvmTarget = "20"` (configurado em `app/build.gradle.kts`)

## Verificação

Para verificar se está usando JDK 20:

```bash
java -version
```

Deve mostrar algo como:
```
openjdk version "20.0.2" 2024-07-16
OpenJDK Runtime Environment Corretto-20.0.2.1 (build 20.0.2+9-LTS)
OpenJDK 64-Bit Server VM Corretto-20.0.2.1 (build 20.0.2+9-LTS, mixed mode, sharing)
```

## Nota

O problema ocorre porque o Detekt detecta o JDK do sistema (24) e tenta usar esse valor. Ao forçar o uso do JDK 20 através do `JAVA_HOME` ou do script fornecido, o Detekt usará corretamente o JVM target 20.

