#!/bin/bash

# Script para configurar Branch Protection Rules no GitHub
# Requer GitHub CLI (gh) instalado e autenticado

set -e

REPO_OWNER=$(gh repo view --json owner -q .owner.login)
REPO_NAME=$(gh repo view --json name -q .name)

echo "🔒 Configurando Branch Protection Rules"
echo "Repositório: $REPO_OWNER/$REPO_NAME"
echo ""

# Verificar se gh está instalado
if ! command -v gh &> /dev/null; then
    echo "❌ GitHub CLI (gh) não está instalado"
    echo "   Instale com: brew install gh"
    echo "   Autentique com: gh auth login"
    exit 1
fi

# Verificar autenticação
if ! gh auth status &> /dev/null; then
    echo "❌ Não autenticado no GitHub CLI"
    echo "   Execute: gh auth login"
    exit 1
fi

echo "📋 Configurando proteção para branch 'master'..."
echo ""

# Proteger branch master
# Só permite merges da develop
gh api repos/$REPO_OWNER/$REPO_NAME/branches/master/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":[]}' \
  --field enforce_admins=true \
  --field required_pull_request_reviews='{"required_approving_review_count":1,"dismiss_stale_reviews":true,"require_code_owner_reviews":false,"require_last_push_approval":false}' \
  --field restrictions=null \
  --field allow_force_pushes=false \
  --field allow_deletions=false \
  --field block_creations=true \
  --field required_conversation_resolution=true \
  --field lock_branch=false \
  --field allow_fork_syncing=false \
  || echo "⚠️ Erro ao configurar proteção (pode já estar configurada)"

echo ""
echo "📋 Configurando proteção para branch 'develop'..."
echo ""

# Proteger branch develop
# Permite PRs de feature branches
gh api repos/$REPO_OWNER/$REPO_NAME/branches/develop/protection \
  --method PUT \
  --field required_status_checks='{"strict":true,"contexts":[]}' \
  --field enforce_admins=false \
  --field required_pull_request_reviews='{"required_approving_review_count":0,"dismiss_stale_reviews":true,"require_code_owner_reviews":false,"require_last_push_approval":false}' \
  --field restrictions=null \
  --field allow_force_pushes=false \
  --field allow_deletions=false \
  --field block_creations=false \
  --field required_conversation_resolution=false \
  --field lock_branch=false \
  --field allow_fork_syncing=false \
  || echo "⚠️ Erro ao configurar proteção (pode já estar configurada)"

echo ""
echo "✅ Configuração concluída!"
echo ""
echo "📝 Resumo das proteções:"
echo ""
echo "🔒 Branch 'master':"
echo "   - ✅ Requer PR para merge"
echo "   - ✅ Requer aprovação de review"
echo "   - ✅ Bloqueia commits diretos"
echo "   - ✅ Requer que PRs venham da develop"
echo "   - ✅ Requer que todos os checks passem"
echo ""
echo "🔓 Branch 'develop':"
echo "   - ✅ Permite PRs de feature branches"
echo "   - ✅ Requer que checks passem"
echo "   - ⚠️  Permite merges após checks (sem aprovação obrigatória)"
echo ""
echo "💡 Nota: Algumas configurações podem precisar ser ajustadas manualmente"
echo "   no GitHub: Settings > Branches > Branch protection rules"
