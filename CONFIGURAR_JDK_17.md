# 🔧 Configurar Android Studio para usar JDK 17

Este guia mostra como configurar o Android Studio para usar JDK 17, necessário para evitar problemas com o Detekt que não suporta JDK > 20.

## ✅ Verificação Prévia

Você já tem JDK 17 instalado no sistema:
- **Zulu 17.50.19**: `/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home`
- **Amazon Corretto 17**: `/Users/rodrigo/Library/Java/JavaVirtualMachines/corretto-17.0.8/Contents/Home`

## 📋 Passos para Configurar no Android Studio

### 1. Configurar JDK do Projeto

1. Abra o Android Studio
2. Vá em **File** → **Project Structure** (ou `⌘;` no Mac)
3. Na aba **Project**, em **SDK Location**:
   - **Project SDK**: Selecione **17** (ou adicione se não aparecer)
   - **Project Language Level**: Selecione **17 - Sealed types, always-strict floating-point semantics**
4. Clique em **Apply** e depois **OK**

### 2. Configurar Gradle JDK

1. Vá em **File** → **Settings** (ou `⌘,` no Mac)
2. Navegue até **Build, Execution, Deployment** → **Build Tools** → **Gradle**
3. Em **Gradle JDK**, selecione:
   - **17** (se disponível)
   - Ou **Download JDK...** e baixe o JDK 17
4. Clique em **Apply** e depois **OK**

### 3. Configurar JDK do Gradle Daemon (Alternativa)

Se a opção acima não funcionar:

1. Vá em **File** → **Settings** → **Build, Execution, Deployment** → **Build Tools** → **Gradle**
2. Em **Gradle JVM**, selecione **17** ou configure um JDK customizado apontando para:
   ```
   /Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
   ```
   ou
   ```
   /Users/rodrigo/Library/Java/JavaVirtualMachines/corretto-17.0.8/Contents/Home
   ```

### 4. Reiniciar Gradle Daemon

Após configurar, reinicie o Gradle Daemon:

1. Abra o terminal integrado do Android Studio (View → Tool Windows → Terminal)
2. Execute:
   ```bash
   ./gradlew --stop
   ```
3. Sincronize o projeto novamente (File → Sync Project with Gradle Files)

## 🔍 Verificar Configuração

Para verificar se está usando JDK 17:

1. Abra o terminal no Android Studio
2. Execute:
   ```bash
   ./gradlew detekt --info 2>&1 | grep -i "jvm\|java" | head -5
   ```

Ou verifique diretamente:
```bash
java -version
```

Deve mostrar algo como:
```
openjdk version "17.0.11" ...
```

## 🚨 Solução de Problemas

### Se o JDK 17 não aparecer nas opções:

1. **Adicionar JDK manualmente:**
   - Vá em **File** → **Project Structure** → **SDKs**
   - Clique no **+** → **Add JDK...**
   - Navegue até: `/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home`
   - Clique em **OK**

### Se ainda estiver usando JDK 24:

1. Verifique a variável de ambiente `JAVA_HOME`:
   ```bash
   echo $JAVA_HOME
   ```

2. Configure temporariamente no terminal:
   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 17)
   ```

3. Ou configure permanentemente no `~/.zshrc`:
   ```bash
   echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
   source ~/.zshrc
   ```

## ✅ Configuração Automática

Os arquivos de configuração do projeto já foram atualizados:
- `.idea/misc.xml` - Configurado para JDK 17
- `.idea/gradle.xml` - Configurado para usar JDK 17 no Gradle

Após seguir os passos acima, o Android Studio deve usar JDK 17 automaticamente para este projeto.

## 🚀 Configuração Rápida via Terminal

Para configurar rapidamente via terminal (temporário para esta sessão):

```bash
# Configurar JDK 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

# Parar Gradle daemon antigo
./gradlew --stop

# Verificar versão
java -version

# Testar Detekt
./gradlew detekt
```

Para tornar permanente, adicione ao `~/.zshrc`:
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 17)' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

Ou use o script auxiliar:
```bash
./scripts/setup-jdk17.sh
```

## 📝 Nota

O Detekt requer JVM target ≤ 20. Com JDK 17 configurado, o Detekt funcionará corretamente sem erros de "Invalid value (24)".

**Importante**: Após configurar o JDK 17 no Android Studio, sempre execute `./gradlew --stop` antes de rodar o Detekt para garantir que o Gradle daemon use o JDK correto.

