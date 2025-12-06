# 🗑️ Remover JDK 24 do Sistema

## Localização do JDK 24

O JDK 24 está instalado em:
```
/Library/Java/JavaVirtualMachines/jdk-24.jdk/
```

## Método 1: Remover via Terminal (Recomendado)

### Passo 1: Verificar a instalação

```bash
/usr/libexec/java_home -V 2>&1 | grep 24
```

Deve mostrar algo como:
```
24 (arm64) "Oracle Corporation" - "Java SE 24" /Library/Java/JavaVirtualMachines/jdk-24.jdk/Contents/Home
```

### Passo 2: Remover o JDK 24

Como o JDK está instalado em `/Library/Java/JavaVirtualMachines/` (diretório do sistema), você precisará de permissões de administrador:

```bash
sudo rm -rf /Library/Java/JavaVirtualMachines/jdk-24.jdk
```

### Passo 3: Verificar a remoção

```bash
/usr/libexec/java_home -V 2>&1 | grep 24
```

Não deve retornar nada se o JDK 24 foi removido com sucesso.

### Passo 4: Verificar o JDK padrão

```bash
/usr/libexec/java_home
java -version
```

Agora deve usar outro JDK (provavelmente o JDK 20 ou 17).

## Método 2: Remover via Finder (Alternativo)

1. Abra o **Finder**
2. Pressione `Cmd + Shift + G` (ou vá em **Go > Go to Folder...**)
3. Digite: `/Library/Java/JavaVirtualMachines/`
4. Encontre a pasta `jdk-24.jdk`
5. Clique com o botão direito e selecione **Move to Trash**
6. Você precisará inserir sua senha de administrador

## Verificar JDKs Restantes

Após remover o JDK 24, você pode verificar quais JDKs ainda estão instalados:

```bash
/usr/libexec/java_home -V
```

## Configurar JDK Padrão

Se você quiser definir o JDK 20 como padrão, você pode:

### Opção 1: Configurar no shell (temporário)

Adicione ao seu `~/.zshrc` ou `~/.bash_profile`:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 20)
export PATH="$JAVA_HOME/bin:$PATH"
```

Depois execute:
```bash
source ~/.zshrc  # ou source ~/.bash_profile
```

### Opção 2: Configurar no Gradle (permanente para o projeto)

Já está configurado no `gradle.properties` do projeto para usar JDK 20.

## Verificação Final

Após remover o JDK 24:

```bash
# Verificar se o JDK 24 foi removido
/usr/libexec/java_home -V 2>&1 | grep 24

# Verificar o JDK atual
java -version

# Testar o Detekt
./gradlew detekt
```

## Nota Importante

- O JDK 24 está instalado como **root** (pertencente ao sistema), então você precisará de `sudo` para removê-lo
- Após remover o JDK 24, o sistema usará automaticamente o próximo JDK disponível na ordem de prioridade
- Se você tiver configurado o `JAVA_HOME` no seu shell, ele pode ainda apontar para o JDK 24. Verifique e atualize se necessário

