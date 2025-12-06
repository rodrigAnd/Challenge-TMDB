# 🗑️ Remover JDK 21 e Deixar Somente JDK 20

## Localização do JDK 21

O JDK 21 (JBR - JetBrains Runtime) está instalado em:
```
~/Library/Java/JavaVirtualMachines/jbr-21.0.9/
```

Como está no diretório do usuário, **não precisa de sudo** para remover.

## Método 1: Remover via Terminal (Recomendado)

### Passo 1: Verificar a instalação

```bash
/usr/libexec/java_home -V 2>&1 | grep 21
```

Deve mostrar:
```
21.0.9 (arm64) "JetBrains s.r.o." - "JBR-21.0.9+10-1163.86-nomod 21.0.9" /Users/rodrigo/Library/Java/JavaVirtualMachines/jbr-21.0.9/Contents/Home
```

### Passo 2: Remover o JDK 21

```bash
rm -rf ~/Library/Java/JavaVirtualMachines/jbr-21.0.9
```

Também pode remover o arquivo de configuração do IntelliJ:
```bash
rm -f ~/Library/Java/JavaVirtualMachines/.jbr-21.0.9.intellij
```

### Passo 3: Verificar a remoção

```bash
/usr/libexec/java_home -V 2>&1 | grep 21
```

Não deve retornar nada se o JDK 21 foi removido com sucesso.

### Passo 4: Verificar o JDK padrão

```bash
/usr/libexec/java_home
java -version
```

Agora deve usar o JDK 20 como padrão.

## Método 2: Remover via Finder (Alternativo)

1. Abra o **Finder**
2. Pressione `Cmd + Shift + G` (ou vá em **Go > Go to Folder...**)
3. Digite: `~/Library/Java/JavaVirtualMachines/`
4. Encontre a pasta `jbr-21.0.9`
5. Clique com o botão direito e selecione **Move to Trash**
6. Também remova o arquivo `.jbr-21.0.9.intellij` se existir

## Verificar JDKs Restantes

Após remover o JDK 21, você pode verificar quais JDKs ainda estão instalados:

```bash
/usr/libexec/java_home -V
```

Deve mostrar apenas:
- JDK 20 (Amazon Corretto 20)
- Outros JDKs que você ainda tenha instalado

## Configurar JDK 20 como Padrão

### Opção 1: Configurar no shell (temporário)

Adicione ao seu `~/.zshrc`:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 20)
export PATH="$JAVA_HOME/bin:$PATH"
```

Depois execute:
```bash
source ~/.zshrc
```

### Opção 2: Configurar no Gradle (permanente para o projeto)

Já está configurado no `gradle.properties` do projeto para usar JDK 20.

### Opção 3: Configurar no Android Studio

1. Abra **File > Settings** (ou **Android Studio > Preferences** no macOS)
2. Vá em **Build, Execution, Deployment > Build Tools > Gradle**
3. Em **Gradle JDK**, selecione **corretto-20** (ou JDK 20)
4. Clique em **Apply** e **OK**

## Verificação Final

Após remover o JDK 21:

```bash
# Verificar se o JDK 21 foi removido
/usr/libexec/java_home -V 2>&1 | grep 21

# Verificar o JDK atual (deve ser JDK 20)
java -version
/usr/libexec/java_home

# Testar o Detekt (deve funcionar sem erros)
./gradlew detekt
```

## Nota Importante

- O JDK 21 é um **JBR (JetBrains Runtime)** instalado pelo Android Studio/IntelliJ IDEA
- Como está no diretório do usuário (`~/Library/`), **não precisa de sudo** para remover
- Após remover o JDK 21, o sistema usará automaticamente o JDK 20 como padrão
- Se você tiver configurado o `JAVA_HOME` no seu shell, ele pode ainda apontar para o JDK 21. Verifique e atualize se necessário

