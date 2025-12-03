# ⚡ Soluções Rápidas para Problemas do Detekt

## 🎯 Problema Atual

- **117 problemas** encontrados
- **maxIssues: 10** configurado
- **Build falhando** no CI

---

## ✅ Solução Imediata: Ajustar Configuração

### Opção 1: Aumentar maxIssues Temporariamente

Edite `config/detekt/detekt.yml`:

```yaml
build:
  maxIssues: 150  # Aumentar de 10 para 150
```

### Opção 2: Gerar Baseline (Recomendado)

Isso cria um arquivo que ignora problemas existentes:

```bash
# Gerar baseline
./gradlew detektBaseline

# Agora o Detekt só reportará NOVOS problemas
./gradlew detekt
```

### Opção 3: Ajustar Thresholds

Edite `config/detekt/detekt.yml`:

```yaml
complexity:
  CyclomaticComplexMethod:
    threshold: 15  # Aumentar de 10 para 15
  
  LongMethod:
    threshold: 80  # Aumentar de 60 para 80
  
  LongParameterList:
    functionThreshold: 8
    constructorThreshold: 8
  
  TooManyFunctions:
    threshold: 15  # Aumentar de 11 para 15
```

---

## 🔧 Correções Rápidas de Código

### 1. Blocos Vazios (5 problemas)

**Arquivos**:
- `FavoritesFragment.kt` (linhas 71, 77)
- `MovieListFragment.kt` (linhas 96, 100)

**Correção**: Remover ou implementar:
```kotlin
// ❌ Antes
override fun onViewCreated(view: View, savedInstanceState: Bundle?) { }

// ✅ Depois
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // ou remover se não necessário
}
```

### 2. Nomenclatura de Pacote (1 problema)

**Arquivo**: `MovieDetailActivity.kt`

**Correção**: Verificar se o pacote está correto.

### 3. Exceções Engolidas (4 problemas)

**Arquivos**:
- `MovieMapper.kt`
- `MovieViewObjectMapper.kt`
- `GenreListConverter.kt`
- `MovieAdapter.kt`

**Correção**: Adicionar log:
```kotlin
// ❌ Antes
catch (e: Exception) { }

// ✅ Depois
catch (e: Exception) {
    Log.e(TAG, "Error processing data", e)
}
```

---

## 📋 Recomendação Final

**Para CI funcionar AGORA**:

1. **Gerar baseline** (melhor opção):
   ```bash
   ./gradlew detektBaseline
   git add config/detekt/baseline.xml
   git commit -m "chore: adicionar baseline do Detekt"
   ```

2. **OU aumentar maxIssues temporariamente**:
   ```yaml
   build:
     maxIssues: 150
   ```

3. **Depois**, corrigir problemas gradualmente:
   - Começar pelos mais críticos
   - Corrigir em PRs separados
   - Reduzir maxIssues gradualmente

---

## 🎯 Próximos Passos

1. ✅ Escolher uma solução acima
2. ✅ Testar localmente: `./gradlew detekt`
3. ✅ Commit e push
4. ✅ Verificar CI
5. ✅ Corrigir problemas gradualmente

