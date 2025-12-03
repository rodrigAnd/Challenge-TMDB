# 📋 Problemas Encontrados pelo Detekt

## 📊 Resumo

O Detekt encontrou **127 problemas** no código. Abaixo está a categorização e como corrigir.

---

## 🔴 Problemas Críticos (Precisam Correção)

### 1. **Complexidade Ciclomática Alta** (CyclomaticComplexMethod)

**Problema**: Funções muito complexas (> 10)

**Arquivos afetados**:
- `MovieRepositoryImpl.kt`:
  - `getPopularMovies()` - complexidade: 14
  - `searchMovies()` - complexidade: 13
  - `getMovieDetails()` - complexidade: 11
- `MovieDetailActivity.kt`:
  - `displayMovieDetail()` - complexidade: 10
- `MovieAdapter.kt`:
  - `bind()` - complexidade: 10
- `MovieListFragment.kt`:
  - `observeUiState()` - complexidade: 11

**Solução**: Dividir funções em funções menores e mais específicas.

---

### 2. **Métodos Muito Longos** (LongMethod)

**Problema**: Métodos com mais de 60 linhas

**Arquivos afetados**:
- `MovieListViewModel.kt`: `toggleFavorite()` - 65 linhas
- `MovieAdapter.kt`: `bind()` - 78 linhas

**Solução**: Extrair lógica para funções auxiliares.

---

### 3. **Lista de Parâmetros Longa** (LongParameterList)

**Problema**: Construtores/funções com muitos parâmetros (> 6)

**Arquivos afetados**:
- `MovieListViewModel.kt`: Construtor com 8 parâmetros

**Solução**: Usar data classes ou builder pattern.

---

### 4. **Muitas Funções em Classes** (TooManyFunctions)

**Problema**: Classes com muitas funções (> 11)

**Arquivos afetados**:
- `MovieListViewModel.kt`: 11 funções
- `MovieListFragment.kt`: 12 funções

**Solução**: Dividir responsabilidades em classes menores.

---

## ⚠️ Problemas de Qualidade (Recomendado Corrigir)

### 5. **Exceções Genéricas** (TooGenericExceptionCaught)

**Problema**: Uso de `catch (Exception)` em vez de exceções específicas

**Arquivos afetados**: Múltiplos arquivos (30+ ocorrências)

**Solução**: Capturar exceções específicas:
```kotlin
// ❌ Ruim
catch (e: Exception) { }

// ✅ Bom
catch (e: IOException) { }
catch (e: HttpException) { }
catch (e: SQLException) { }
```

---

### 6. **Exceções Engolidas** (SwallowedException)

**Problema**: Exceções capturadas mas não logadas ou tratadas

**Arquivos afetados**:
- `MovieMapper.kt`
- `MovieViewObjectMapper.kt`
- `GenreListConverter.kt`
- `MovieAdapter.kt`

**Solução**: Logar ou relançar exceções:
```kotlin
// ❌ Ruim
catch (e: Exception) { }

// ✅ Bom
catch (e: Exception) {
    Log.e(TAG, "Error", e)
    // ou relançar
    throw e
}
```

---

### 7. **Blocos Vazios** (EmptyFunctionBlock)

**Problema**: Funções com blocos vazios

**Arquivos afetados**:
- `FavoritesFragment.kt`
- `MovieListFragment.kt`

**Solução**: Remover ou implementar lógica.

---

### 8. **Nomenclatura de Pacote** (PackageNaming)

**Problema**: Nome de pacote não segue padrão

**Arquivos afetados**:
- `MovieDetailActivity.kt`: Pacote não segue padrão `^[a-z]+(\.[a-z][a-z0-9]*)*$`

**Solução**: Corrigir nome do pacote.

---

## 🔧 Soluções Rápidas

### Opção 1: Ajustar Thresholds (Temporário)

Edite `config/detekt/detekt.yml`:

```yaml
complexity:
  CyclomaticComplexMethod:
    threshold: 15  # Aumentar de 10 para 15
  
  LongMethod:
    threshold: 80  # Aumentar de 60 para 80
  
  LongParameterList:
    functionThreshold: 8  # Aumentar de 6 para 8
  
  TooManyFunctions:
    # Adicionar threshold se necessário
```

### Opção 2: Desabilitar Regras Temporariamente

```yaml
complexity:
  CyclomaticComplexMethod:
    active: false  # Desabilitar temporariamente
```

### Opção 3: Usar Baseline

```bash
# Gerar baseline para ignorar problemas existentes
./gradlew detektBaseline
```

---

## 📝 Plano de Ação Recomendado

### Fase 1: Correções Rápidas (1-2 horas)
1. ✅ Corrigir blocos vazios
2. ✅ Corrigir nomenclatura de pacote
3. ✅ Adicionar logs em exceções engolidas

### Fase 2: Refatorações Médias (4-8 horas)
1. ✅ Dividir funções complexas
2. ✅ Reduzir tamanho de métodos longos
3. ✅ Melhorar tratamento de exceções

### Fase 3: Refatorações Grandes (1-2 dias)
1. ✅ Refatorar construtores com muitos parâmetros
2. ✅ Dividir classes grandes
3. ✅ Revisar arquitetura

---

## 🎯 Priorização

**Alta Prioridade**:
- Exceções engolidas (pode causar bugs silenciosos)
- Exceções genéricas (má prática)
- Blocos vazios (código morto)

**Média Prioridade**:
- Complexidade ciclomática
- Métodos longos
- Nomenclatura

**Baixa Prioridade**:
- Muitas funções em classes (pode ser aceitável dependendo do contexto)
- Lista de parâmetros longa (pode ser aceitável em alguns casos)

---

## 📚 Referências

- [Detekt Rules](https://detekt.github.io/detekt/complexity.html)
- [Cyclomatic Complexity](https://en.wikipedia.org/wiki/Cyclomatic_complexity)
- [Clean Code - Functions](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

