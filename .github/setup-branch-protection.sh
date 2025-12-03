#!/bin/bash

# Script para configurar proteções de branch no GitHub
# Requer: gh CLI instalado e autenticado

set -e

echo "🔒 Configurando Proteções de Branch no GitHub"
echo "=============================================="

# Verifica se gh CLI está instalado
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) não está instalado."
    echo "   Instale em: https://cli.github.com/"
    exit 1
fi

# Verifica se está autenticado
if ! gh auth status &> /dev/null; then
    echo "❌ Não está autenticado no GitHub CLI."
    echo "   Execute: gh auth login"
    exit 1
fi

REPO=$(gh repo view --json nameWithOwner -q .nameWithOwner)
echo "📦 Repositório: $REPO"
echo ""

# Função para configurar proteção da branch master
setup_master_protection() {
    echo "🔐 Configurando proteção para branch 'master'..."
    
    gh api repos/$REPO/branches/master/protection \
        --method PUT \
        --field required_status_checks='{"strict":true,"contexts":["Code Review Automatizado","Build e Testes Unitários","Validação de Qualidade","Validação Rigorosa para Master"]}' \
        --field enforce_admins=true \
        --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true,"require_code_owner_reviews":false}' \
        --field restrictions=null \
        --field required_linear_history=false \
        --field allow_force_pushes=false \
        --field allow_deletions=false \
        --field required_conversation_resolution=true \
        --field lock_branch=false || {
        echo "⚠️  Erro ao configurar proteção. Verifique se você tem permissões de administrador."
        echo "   Configure manualmente em: Settings → Branches → Add rule"
        return 1
    }
    
    echo "✅ Proteção da branch 'master' configurada!"
}

# Função para configurar proteção da branch develop
setup_develop_protection() {
    echo "🔐 Configurando proteção para branch 'develop'..."
    
    gh api repos/$REPO/branches/develop/protection \
        --method PUT \
        --field required_status_checks='{"strict":true,"contexts":["Validação Contínua","Build e Testes Unitários"]}' \
        --field enforce_admins=false \
        --field required_pull_request_reviews='{"required_approving_review_count":0,"dismiss_stale_reviews":true,"require_code_owner_reviews":false}' \
        --field restrictions=null \
        --field required_linear_history=false \
        --field allow_force_pushes=false \
        --field allow_deletions=false \
        --field required_conversation_resolution=true \
        --field lock_branch=false || {
        echo "⚠️  Erro ao configurar proteção. Verifique se você tem permissões de administrador."
        echo "   Configure manualmente em: Settings → Branches → Add rule"
        return 1
    }
    
    echo "✅ Proteção da branch 'develop' configurada!"
}

# Verifica se as branches existem
echo "🔍 Verificando branches..."
BRANCHES=$(gh api repos/$REPO/branches --jq '.[].name')

if echo "$BRANCHES" | grep -q "^master$"; then
    echo "✅ Branch 'master' encontrada"
    setup_master_protection
else
    echo "⚠️  Branch 'master' não encontrada. Criando..."
    git checkout -b master 2>/dev/null || echo "   (já existe localmente)"
    git push -u origin master || echo "   (push manual necessário)"
    setup_master_protection
fi

echo ""

if echo "$BRANCHES" | grep -q "^develop$"; then
    echo "✅ Branch 'develop' encontrada"
    setup_develop_protection
else
    echo "⚠️  Branch 'develop' não encontrada. Criando..."
    git checkout -b develop 2>/dev/null || echo "   (já existe localmente)"
    git push -u origin develop || echo "   (push manual necessário)"
    setup_develop_protection
fi

echo ""
echo "=============================================="
echo "✅ Configuração concluída!"
echo ""
echo "📋 Próximos passos:"
echo "   1. Verifique as proteções em: https://github.com/$REPO/settings/branches"
echo "   2. Ajuste os status checks conforme necessário"
echo "   3. Configure CODEOWNERS em .github/CODEOWNERS"
echo "   4. Teste criando um PR para 'develop'"
echo ""
echo "📚 Documentação:"
echo "   - .github/BRANCH_PROTECTION.md"
echo "   - .github/workflows/README.md"
echo ""

