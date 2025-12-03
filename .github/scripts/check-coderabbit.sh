#!/bin/bash

# Script para verificar se o CodeRabbit está configurado
# Uso: ./check-coderabbit.sh

echo "🔍 Verificando configuração do CodeRabbit..."
echo ""

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar se gh CLI está instalado
if ! command -v gh &> /dev/null; then
    echo "${YELLOW}⚠️  GitHub CLI (gh) não está instalado${NC}"
    echo "   Instale com: brew install gh"
    echo "   Ou verifique manualmente em: https://github.com/settings/installations"
    exit 0
fi

# Verificar se está autenticado
if ! gh auth status &> /dev/null; then
    echo "${YELLOW}⚠️  Não está autenticado no GitHub CLI${NC}"
    echo "   Execute: gh auth login"
    exit 0
fi

echo "📋 Verificando GitHub Apps instalados..."
echo ""

# Listar apps instalados
APPS=$(gh api user/installations --jq '.installations[] | select(.app_slug == "coderabbitai") | .app_slug' 2>/dev/null)

if [ -z "$APPS" ]; then
    echo "${RED}❌ CodeRabbit não está instalado${NC}"
    echo ""
    echo "📝 Para instalar:"
    echo "   1. Acesse: https://github.com/apps/coderabbitai"
    echo "   2. Clique em 'Install'"
    echo "   3. Selecione o repositório MyChallenge"
    echo "   4. Autorize as permissões"
    echo ""
    exit 1
else
    echo "${GREEN}✅ CodeRabbit está instalado${NC}"
    echo ""
    
    # Verificar configuração do arquivo .coderabbit.yaml
    if [ -f ".coderabbit.yaml" ]; then
        echo "${GREEN}✅ Arquivo .coderabbit.yaml encontrado${NC}"
    else
        echo "${YELLOW}⚠️  Arquivo .coderabbit.yaml não encontrado${NC}"
        echo "   (Opcional, mas recomendado para configuração customizada)"
    fi
    
    echo ""
    echo "📝 Próximos passos:"
    echo "   1. Crie um PR de teste"
    echo "   2. O CodeRabbit automaticamente fará review"
    echo "   3. Verifique os comentários no PR"
    echo ""
    echo "🔗 Links úteis:"
    echo "   - Configuração: https://github.com/apps/coderabbitai"
    echo "   - Documentação: https://docs.coderabbit.ai"
fi

