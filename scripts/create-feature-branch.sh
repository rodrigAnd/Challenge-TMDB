#!/bin/bash

if [ -z "$1" ]; then
    echo "❌ Erro: Nome da feature não fornecido"
    echo ""
    echo "Uso: $0 <nome-da-feature>"
    echo ""
    echo "Exemplo:"
    echo "  $0 adiciona-tela-login"
    exit 1
fi

FEATURE_NAME="$1"
BRANCH_NAME="feature/$FEATURE_NAME"

echo "🌿 Criando feature branch: $BRANCH_NAME"
echo ""

# Verificar se estamos em um repositório git
if [ ! -d .git ]; then
    echo "❌ Erro: Não é um repositório git"
    exit 1
fi

# Verificar se develop existe
if ! git show-ref --verify --quiet refs/heads/develop; then
    echo "⚠️  Branch develop não existe localmente"
    echo "📥 Fazendo checkout da develop remota..."
    git fetch origin
    git checkout -b develop origin/develop || git checkout develop
fi

# Atualizar develop
echo "📥 Atualizando develop..."
git checkout develop
git pull origin develop

# Criar feature branch
echo "🌿 Criando branch: $BRANCH_NAME"
git checkout -b "$BRANCH_NAME"

echo ""
echo "✅ Feature branch criada: $BRANCH_NAME"
echo ""
echo "📝 Próximos passos:"
echo "   1. Faça suas alterações"
echo "   2. Commit: git commit -m 'feat: sua mensagem'"
echo "   3. Push: git push origin $BRANCH_NAME"
echo "   4. Crie um PR para develop no GitHub"
