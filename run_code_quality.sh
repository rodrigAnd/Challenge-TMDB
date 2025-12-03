#!/bin/bash

echo "🔍 Executando análise de qualidade de código..."
echo ""

# Verificar se ktlint está disponível
if command -v ktlint &> /dev/null; then
    echo "✅ KTLint encontrado"
    echo "📝 Executando KTLint..."
    ktlint --version
    find app/src -name "*.kt" -type f | xargs ktlint --android || echo "⚠️ KTLint encontrou problemas"
else
    echo "⚠️ KTLint não está instalado via CLI"
    echo "💡 Instale com: brew install ktlint (macOS) ou baixe de https://github.com/pinterest/ktlint"
fi

echo ""
echo "🔎 Executando Detekt..."

# Verificar se detekt está disponível
if command -v detekt &> /dev/null; then
    echo "✅ Detekt encontrado"
    detekt --version
    detekt --input app/src --config config/detekt/detekt.yml --baseline config/detekt/baseline.xml || echo "⚠️ Detekt encontrou problemas"
else
    echo "⚠️ Detekt não está instalado via CLI"
    echo "💡 Baixe de https://github.com/detekt/detekt/releases"
fi

echo ""
echo "✅ Análise concluída!"
