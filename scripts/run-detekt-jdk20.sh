#!/bin/bash

# Script para rodar Detekt usando JDK 20
# Este script força o uso do JDK 20 mesmo quando o sistema usa JDK mais alto

# Encontrar o caminho do JDK 20
JDK20_HOME=$(/usr/libexec/java_home -v 20)

if [ -z "$JDK20_HOME" ]; then
    echo "JDK 20 não encontrado. Por favor, instale o JDK 20."
    exit 1
fi

echo "Usando JDK 20: $JDK20_HOME"
export JAVA_HOME="$JDK20_HOME"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Parando o Gradle daemon..."
./gradlew --stop

echo "Rodando Detekt com JDK 20..."
# Forçar uso do JDK 20 através do JAVA_HOME
JAVA_HOME="$JDK20_HOME" ./gradlew detekt

