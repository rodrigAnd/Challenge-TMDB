# 🤖 Configuração do CodeRabbit - Passo a Passo

## 📋 Pré-requisitos

- ✅ Conta no GitHub
- ✅ Acesso ao repositório `MyChallenge`
- ✅ Permissões de administrador no repositório (ou permissão para instalar GitHub Apps)

---

## 🚀 Passo a Passo Completo

### Passo 1: Acessar o CodeRabbit GitHub App

1. **Abra seu navegador** e acesse:
   ```
   https://github.com/apps/coderabbitai
   ```

2. **Ou acesse via GitHub**:
   - Vá para: https://github.com/settings/apps
   - Procure por "CodeRabbit" na busca
   - Clique no resultado

---

### Passo 2: Instalar o CodeRabbit

1. Na página do CodeRabbit GitHub App, clique no botão **"Install"** ou **"Configure"**

2. Você será redirecionado para uma página de configuração

3. **Selecione onde instalar**:
   - ✅ **"Only select repositories"** (recomendado)
   - Selecione: `MyChallenge` (ou seu repositório)
   - Ou escolha **"All repositories"** se quiser em todos

4. Clique em **"Install"**

---

### Passo 3: Configurar Permissões

O GitHub mostrará as permissões que o CodeRabbit precisa:

**Permissões necessárias:**
- ✅ **Read pull requests** - Para ler PRs
- ✅ **Write pull request reviews** - Para criar reviews
- ✅ **Read repository contents** - Para ler código
- ✅ **Read repository metadata** - Para informações básicas

**Permissões opcionais (recomendadas):**
- ✅ **Read issues** - Para comentar em issues
- ✅ **Write issues** - Para criar issues se necessário

5. **Revise as permissões** e clique em **"Install"** ou **"Authorize"**

---

### Passo 4: Configurar CodeRabbit (Opcional)

Após instalar, você pode configurar o CodeRabbit:

1. **Acesse**: https://coderabbit.ai
2. **Faça login** com GitHub
3. **Selecione seu repositório** `MyChallenge`
4. **Configure opções**:
   - ✅ Enable code review
   - ✅ Enable suggestions
   - ✅ Auto-approve if no issues (opcional)
   - ✅ Review only changed files (recomendado)

---

### Passo 5: Verificar Instalação

1. **No GitHub**, vá para:
   - **Settings** → **Integrations** → **Installed GitHub Apps**
   - Ou: **Settings** → **Applications** → **Installed GitHub Apps**

2. **Verifique** se "CodeRabbit" aparece na lista

3. **Clique em "CodeRabbit"** para ver configurações:
   - ✅ Verificar permissões
   - ✅ Verificar repositórios conectados
   - ✅ Configurar opções adicionais

---

### Passo 6: Testar

1. **Crie um PR de teste**:
   ```bash
   git checkout -b test/coderabbit-setup
   echo "// Test CodeRabbit" >> app/src/main/java/com/onboarding/mychallenge/TestFile.kt
   git add .
   git commit -m "test: verificar CodeRabbit"
   git push origin test/coderabbit-setup
   ```

2. **No GitHub**, crie um PR desta branch para `develop`

3. **Aguarde alguns minutos** - O CodeRabbit automaticamente:
   - ✅ Analisará o código
   - ✅ Criará comentários no PR
   - ✅ Fará sugestões de melhoria

4. **Verifique os comentários** do CodeRabbit no PR

---

## ✅ Verificação de Sucesso

Você saberá que está funcionando quando:

- ✅ O CodeRabbit aparece como um "reviewer" no PR
- ✅ Comentários automáticos aparecem no PR
- ✅ Sugestões de código são feitas
- ✅ O bot aprova ou solicita mudanças automaticamente

---

## 🔧 Configurações Avançadas

### Configurar via arquivo `.coderabbit.yaml`

Crie um arquivo `.coderabbit.yaml` na raiz do projeto:

```yaml
# .coderabbit.yaml
language: kotlin
reviews:
  enabled: true
  auto_approve: false
  suggestions: true
  review_only_changed_files: true
  max_files_to_review: 50
  max_review_comments: 100
  min_confidence: 0.7
```

### Configurar via GitHub Settings

1. Vá para: **Settings** → **Code security and analysis**
2. Procure por configurações do CodeRabbit
3. Configure preferências de review

---

## 🐛 Troubleshooting

### Problema: CodeRabbit não aparece no PR

**Solução**:
1. Verifique se o GitHub App está instalado
2. Verifique se tem permissões corretas
3. Aguarde alguns minutos (pode demorar na primeira vez)
4. Verifique se o PR está aberto (não draft)

### Problema: CodeRabbit não cria comentários

**Solução**:
1. Verifique permissões: precisa de "Write pull request reviews"
2. Verifique se há arquivos modificados no PR
3. Verifique logs em: **Settings** → **Integrations** → **CodeRabbit** → **Logs**

### Problema: CodeRabbit está muito lento

**Solução**:
1. Configure `review_only_changed_files: true`
2. Reduza `max_files_to_review`
3. Verifique se há muitos arquivos no PR

---

## 📝 Próximos Passos Após Configuração

1. ✅ **Testar em um PR real**
2. ✅ **Ajustar configurações** conforme necessário
3. ✅ **Revisar sugestões** do CodeRabbit
4. ✅ **Integrar com seu workflow** atual

---

## 🔗 Links Úteis

- **CodeRabbit GitHub App**: https://github.com/apps/coderabbitai
- **CodeRabbit Website**: https://coderabbit.ai
- **Documentação**: https://docs.coderabbit.ai
- **Suporte**: https://coderabbit.ai/support

---

## 💡 Dicas

1. **Comece simples**: Use configurações padrão primeiro
2. **Ajuste gradualmente**: Configure opções avançadas depois
3. **Revise sugestões**: Nem todas as sugestões são perfeitas
4. **Combine com seu bot**: CodeRabbit complementa seu bot atual
5. **Use auto-approve com cuidado**: Só se confiar totalmente

---

## ✅ Checklist de Configuração

- [ ] Acessei https://github.com/apps/coderabbitai
- [ ] Instalei o CodeRabbit GitHub App
- [ ] Selecionei o repositório `MyChallenge`
- [ ] Autorizei as permissões necessárias
- [ ] Verifiquei a instalação em Settings → Installed GitHub Apps
- [ ] Criei um PR de teste
- [ ] Verifiquei que o CodeRabbit fez review
- [ ] Configurei opções adicionais (opcional)

---

**Pronto!** 🎉 O CodeRabbit agora está configurado e funcionando!

