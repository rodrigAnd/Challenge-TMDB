#!/bin/bash

# Script para configurar JDK 17 para o projeto
# Uso: ./scripts/setup-jdk17.sh

echo "🔧 Configurando JDK 17 para o projeto..."

# Verificar se JDK 17 está instalado
JDK17_PATH=$(/usr/libexec/java_home -v 17 2>/dev/null)

if [ -z "$JDK17_PATH" ]; then
    echo "❌ JDK 17 não encontrado!"
    echo "Por favor, instale o JDK 17 primeiro."
    exit 1
fi

echo "✅ JDK 17 encontrado em: $JDK17_PATH"

# Configurar JAVA_HOME temporariamente para esta sessão
export JAVA_HOME="$JDK17_PATH"
export PATH="$JAVA_HOME/bin:$PATH"

echo ""
echo "📋 Configuração aplicada:"
echo "   JAVA_HOME=$JAVA_HOME"
echo ""

# Verificar versão do Java
echo "🔍 Verificando versão do Java:"
java -version

echo ""
echo "✅ Configuração concluída!"
echo ""
echo "📝 Próximos passos:"
echo "   1. Abra o Android Studio"
echo "   2. Vá em File → Project Structure → Project"
echo "   3. Selecione JDK 17 em 'Project SDK'"
echo "   4. Vá em File → Settings → Build Tools → Gradle"
echo "   5. Selecione JDK 17 em 'Gradle JDK'"
echo "   6. Execute: ./gradlew --stop"
echo "   7. Sincronize o projeto (File → Sync Project with Gradle Files)"
echo ""
echo "📖 Para mais detalhes, consulte: CONFIGURAR_JDK_17.md"

