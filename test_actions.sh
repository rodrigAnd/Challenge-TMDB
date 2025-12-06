#!/bin/bash

echo "🧪 Script de Teste para GitHub Actions"
echo "======================================"
echo ""

# Cores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Verificar se está em um repositório git
if [ ! -d .git ]; then
    echo "❌ Erro: Não é um repositório git"
    exit 1
fi

# Verificar se há workflows
if [ ! -d .github/workflows ]; then
    echo "❌ Erro: Nenhum workflow encontrado em .github/workflows"
    exit 1
fi

echo "📋 Workflows encontrados:"
ls -1 .github/workflows/*.yml | sed 's/^/  - /'
echo ""

# Opções
echo "Escolha uma opção:"
echo "1) Criar branch de teste e fazer push"
echo "2) Verificar status das actions (requer gh CLI)"
echo "3) Listar workflows disponíveis"
echo "4) Testar com act (requer act e Docker)"
read -p "Opção (1-4): " option

case $option in
    1)
        echo ""
        echo "${YELLOW}Criando branch de teste...${NC}"
        BRANCH_NAME="test/actions-$(date +%s)"
        git checkout -b "$BRANCH_NAME"
        
        echo "${YELLOW}Fazendo commit vazio...${NC}"
        git commit --allow-empty -m "test: validar GitHub Actions"
        
        echo "${YELLOW}Fazendo push...${NC}"
        git push origin "$BRANCH_NAME"
        
        echo ""
        echo "${GREEN}✅ Branch criada: $BRANCH_NAME${NC}"
        echo "📝 Próximos passos:"
        echo "   1. Vá para o GitHub e crie um PR desta branch para develop"
        echo "   2. Observe as actions executando na aba 'Actions'"
        ;;
    2)
        if ! command -v gh &> /dev/null; then
            echo "❌ GitHub CLI (gh) não está instalado"
            echo "   Instale com: brew install gh"
            exit 1
        fi
        
        echo ""
        echo "${YELLOW}Verificando execuções recentes...${NC}"
        gh run list --limit 10
        ;;
    3)
        echo ""
        echo "${YELLOW}Workflows disponíveis:${NC}"
        for file in .github/workflows/*.yml; do
            echo ""
            echo "📄 $(basename $file):"
            grep -E "^on:|^name:" "$file" | head -5 | sed 's/^/   /'
        done
        ;;
    4)
        if ! command -v act &> /dev/null; then
            echo "❌ Act não está instalado"
            echo "   Instale com: brew install act"
            exit 1
        fi
        
        if ! docker info &> /dev/null; then
            echo "❌ Docker não está rodando"
            exit 1
        fi
        
        echo ""
        echo "${YELLOW}Listando workflows disponíveis no act:${NC}"
        act -l
        
        echo ""
        read -p "Qual workflow deseja testar? (deixe vazio para code-quality): " workflow
        workflow=${workflow:-code-quality.yml}
        
        echo "${YELLOW}Executando workflow: $workflow${NC}"
        act pull_request -W ".github/workflows/$workflow" || echo "⚠️ Alguns steps podem falhar localmente"
        ;;
    *)
        echo "❌ Opção inválida"
        exit 1
        ;;
esac

echo ""
echo "✅ Concluído!"
