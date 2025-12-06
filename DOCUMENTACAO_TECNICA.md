# 📚 Documentação Técnica - My Challenge Movies App

## Índice

1. [Visão Geral](#visão-geral)
2. [Arquitetura](#arquitetura)
3. [Decisões Tecnológicas](#decisões-tecnológicas)
4. [Padrões e Práticas](#padrões-e-práticas)
5. [Segurança](#segurança)
6. [CI/CD e Qualidade](#cicd-e-qualidade)
7. [Testes](#testes)
8. [Performance](#performance)
9. [Referências e Documentação](#referências-e-documentação)

---

## Visão Geral

Este documento detalha todas as decisões técnicas tomadas durante o desenvolvimento do aplicativo **My Challenge**, explicando o **porquê** de cada escolha, as **alternativas consideradas** e os **trade-offs** envolvidos.

### Objetivo do Projeto

Aplicativo Android para exibição de filmes populares, pesquisa e gerenciamento de favoritos utilizando a API do TMDb (The Movie Database).

**Documentação da API TMDb**: [https://developer.themoviedb.org/docs](https://developer.themoviedb.org/docs)

---

## Arquitetura

### 1. Clean Architecture + MVVM

#### Decisão: Implementar Clean Architecture com padrão MVVM

**Por quê?**

A combinação de Clean Architecture com MVVM foi escolhida para garantir:

1. **Separação de Responsabilidades**: Cada camada tem uma responsabilidade única e bem definida
2. **Testabilidade**: Facilita a criação de testes unitários isolados, permitindo mockar dependências facilmente
3. **Manutenibilidade**: Código mais organizado e fácil de entender, facilitando manutenção futura
4. **Escalabilidade**: Facilita adicionar novas funcionalidades sem impactar código existente
5. **Independência de Frameworks**: A camada de domínio não depende de bibliotecas Android, facilitando testes

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **MVP (Model-View-Presenter)** | Mais simples, menos boilerplate | Menos reativo, mais difícil de testar ViewModels | MVVM oferece melhor integração com Android Architecture Components |
| **MVI (Model-View-Intent)** | Estado imutável, mais previsível | Mais complexo, curva de aprendizado maior | MVVM é mais familiar e suficiente para o escopo do projeto |
| **MVVM sem Clean Architecture** | Mais rápido de implementar | Acoplamento maior, difícil de testar | Clean Architecture oferece melhor separação e testabilidade |
| **Jetpack Compose + ViewModel** | Mais moderno, menos boilerplate | Requer migração completa, maior esforço inicial | Decidido como melhoria futura (ver seção de melhorias) |

**Estrutura de Camadas:**

```
┌─────────────────────────────────────┐
│   Presentation Layer                │
│   (Activities, Fragments, ViewModels)│
│   Depende de: Domain               │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│   Domain Layer                      │
│   (UseCases, Models, Repository)    │
│   Não depende de nada              │
└─────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────┐
│   Data Layer                        │
│   (Repository, API, Local DB)       │
│   Depende de: Domain                │
└─────────────────────────────────────┘
```

**Referências:**
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [MVVM Pattern - Android Developers](https://developer.android.com/topic/libraries/architecture/viewmodel)

---

### 2. Presentation Layer

#### 2.1 ViewModels com StateFlow

**Decisão: Usar StateFlow ao invés de LiveData**

**Por quê?**

1. **Coroutines First**: StateFlow é nativo do Kotlin e funciona perfeitamente com Coroutines
2. **Type Safety**: Melhor suporte a tipos genéricos e menos erros em tempo de execução
3. **Operadores Funcionais**: Mais operadores disponíveis (combine, flatMapLatest, etc.)
4. **Cold Flow**: Permite controle mais fino sobre quando o Flow é coletado
5. **Testabilidade**: Mais fácil de testar com Turbine
6. **Performance**: Mais eficiente em alguns cenários, especialmente com múltiplos collectors

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **LiveData** | Mais simples, lifecycle-aware automático | Menos operadores, não é Coroutines-first | StateFlow oferece mais flexibilidade e melhor integração com Coroutines |
| **SharedFlow** | Permite múltiplos valores | Não mantém estado atual, mais complexo | StateFlow mantém o último valor, ideal para UI state |
| **RxJava** | Muito poderoso, muitos operadores | Curva de aprendizado alta, overhead maior | StateFlow é mais leve e nativo do Kotlin |

**Exemplo de Uso:**

```kotlin
// ViewModel
private val _uiState = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

// Fragment
viewLifecycleOwner.lifecycleScope.launch {
    viewModel.uiState.collect { state ->
        when (state) {
            is MovieListUiState.Loading -> showLoading()
            is MovieListUiState.Success -> showMovies(state.movies)
            is MovieListUiState.Error -> showError(state.message)
        }
    }
}
```

**Referências:**
- [StateFlow Documentation](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/)
- [StateFlow vs LiveData](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Flow Operators](https://kotlinlang.org/docs/flow.html#flow-builders)

#### 2.2 ViewBinding

**Decisão: Usar ViewBinding ao invés de findViewById ou Data Binding**

**Por quê?**

1. **Type Safety**: Elimina erros de ClassCastException em tempo de execução
2. **Null Safety**: Views são nullable apenas quando apropriado
3. **Performance**: Melhor performance que findViewById (gerado em tempo de compilação)
4. **Simplicidade**: Menos boilerplate que Data Binding para casos simples
5. **Compile-time Safety**: Erros são detectados em tempo de compilação

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **findViewById** | Simples, direto | Não type-safe, propenso a erros | ViewBinding oferece type-safety e melhor performance |
| **Data Binding** | Binding bidirecional, expressões | Mais complexo, overhead maior | ViewBinding é suficiente para binding unidirecional |
| **Kotlin Synthetics** | Sintaxe mais limpa | Deprecated, não recomendado | ViewBinding é a solução oficial recomendada |

**Referências:**
- [ViewBinding Documentation](https://developer.android.com/topic/libraries/view-binding)
- [ViewBinding vs Data Binding](https://developer.android.com/topic/libraries/view-binding#data-binding)

#### 2.3 Navigation Component

**Decisão: Usar Navigation Component para navegação entre telas**

**Por quê?**

1. **Navegação Declarativa**: Navegação definida em XML, mais fácil de visualizar
2. **Type Safety**: Argumentos type-safe entre destinos
3. **Deep Linking**: Suporte nativo a deep links
4. **Gerenciamento de Back Stack**: Gerenciamento automático do back stack
5. **Integração com ViewModel**: Facilita compartilhamento de ViewModels entre destinos

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Intents Manuais** | Controle total | Muito boilerplate, propenso a erros | Navigation Component reduz boilerplate e erros |
| **Fragment Transactions** | Controle fino | Mais código, difícil de manter | Navigation Component oferece melhor abstração |

**Referências:**
- [Navigation Component](https://developer.android.com/guide/navigation)
- [Navigation Graph](https://developer.android.com/guide/navigation/navigation-design-graph)

---

### 3. Domain Layer

#### 3.1 UseCases

**Decisão: Criar UseCases para cada funcionalidade**

**Por quê?**

1. **Single Responsibility Principle**: Cada UseCase tem uma única responsabilidade
2. **Reutilização**: Lógica de negócio pode ser reutilizada em diferentes ViewModels
3. **Testabilidade**: Mais fácil testar lógica de negócio isoladamente
4. **Clareza**: Código mais legível e fácil de entender
5. **Manutenibilidade**: Mudanças em uma funcionalidade não afetam outras

**Estrutura de um UseCase:**

```kotlin
class GetPopularMoviesUseCase(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(page: Int): Result<PaginatedResult<Movie>> {
        return repository.getPopularMovies(page)
    }
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Lógica no ViewModel** | Mais rápido de implementar | ViewModel fica grande, difícil de testar | UseCases facilitam testes e reutilização |
| **Lógica no Repository** | Menos camadas | Repository fica com responsabilidades mistas | UseCases separam lógica de negócio de acesso a dados |

**Referências:**
- [Use Cases - Clean Architecture](https://developer.android.com/topic/architecture/domain-layer)
- [Use Cases Best Practices](https://proandroiddev.com/android-use-cases-clean-architecture-42d1b1f1c0b0)

#### 3.2 Models de Domínio

**Decisão: Criar modelos de domínio independentes de frameworks**

**Por quê?**

1. **Independência**: Modelos não dependem de bibliotecas Android ou frameworks
2. **Testabilidade**: Podem ser testados sem Android SDK
3. **Reutilização**: Podem ser usados em diferentes camadas
4. **Clareza**: Modelos representam entidades de negócio, não estruturas de dados

**Estrutura:**

```kotlin
data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double
)
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Usar DTOs diretamente** | Menos código | Acoplamento com API, difícil de testar | Modelos de domínio oferecem independência |
| **Usar Entities do Room** | Menos conversões | Acoplamento com Room, difícil de testar | Modelos de domínio são independentes |

**Referências:**
- [Domain Models](https://developer.android.com/topic/architecture/domain-layer#domain-models)

---

### 4. Data Layer

#### 4.1 Repository Pattern

**Decisão: Implementar Repository Pattern para abstrair fonte de dados**

**Por quê?**

1. **Single Source of Truth**: Repository é a única fonte de verdade para dados
2. **Abstração**: ViewModels não precisam saber de onde vêm os dados (API ou DB)
3. **Testabilidade**: Facilita mockar fonte de dados em testes
4. **Flexibilidade**: Facilita trocar implementação (ex: cache, offline-first)

**Estrutura:**

```kotlin
interface MovieRepository {
    suspend fun getPopularMovies(page: Int): Result<PaginatedResult<Movie>>
    suspend fun searchMovies(query: String, page: Int): Result<PaginatedResult<Movie>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    suspend fun addToFavorites(movie: Movie): Result<Unit>
    suspend fun removeFromFavorites(movieId: Int): Result<Unit>
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Acesso direto à API** | Mais simples | Acoplamento forte, difícil de testar | Repository oferece abstração e testabilidade |
| **DataSource Pattern** | Separação mais fina | Mais complexo, pode ser over-engineering | Repository é suficiente para o escopo atual |

**Referências:**
- [Repository Pattern](https://developer.android.com/codelabs/android-room-with-a-view#0)
- [Repository Pattern - Android Guide](https://developer.android.com/topic/architecture/data-layer)

#### 4.2 Retrofit + OkHttp

**Decisão: Usar Retrofit com OkHttp para comunicação HTTP**

**Por quê?**

1. **Type Safety**: Interfaces type-safe, menos erros em tempo de execução
2. **Simplicidade**: Menos boilerplate que outras soluções
3. **Performance**: OkHttp é otimizado e usado por padrão no Android
4. **Interoperabilidade**: Funciona bem com Coroutines e RxJava
5. **Ecossistema**: Grande comunidade e suporte

**Configuração:**

```kotlin
@Provides
@Singleton
fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Volley** | Mais simples | Menos features, menos mantido | Retrofit é mais poderoso e melhor mantido |
| **Ktor** | Kotlin-first, multiplataforma | Menos maduro para Android, curva de aprendizado | Retrofit é padrão da indústria |
| **HttpURLConnection** | Nativo do Android | Muito boilerplate, propenso a erros | Retrofit reduz código e erros |

**Referências:**
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [OkHttp Documentation](https://square.github.io/okhttp/)
- [Retrofit with Coroutines](https://square.github.io/retrofit/2.x/retrofit/retrofit2/CallAdapter.Factory.html)

#### 4.3 Moshi

**Decisão: Usar Moshi para serialização/desserialização JSON**

**Por quê?**

1. **Kotlin First**: Projetado especificamente para Kotlin
2. **Performance**: Mais rápido que Gson em alguns cenários
3. **Type Safety**: Melhor suporte a tipos Kotlin (data classes, sealed classes)
4. **Code Generation**: Gera código em tempo de compilação (mais rápido)
5. **Null Safety**: Melhor tratamento de valores nulos

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Gson** | Muito popular, maduro | Menos otimizado para Kotlin | Moshi é melhor para Kotlin |
| **Jackson** | Muito poderoso | Mais pesado, mais complexo | Moshi é mais leve e suficiente |
| **kotlinx.serialization** | Nativo do Kotlin | Menos integração com Retrofit | Moshi tem melhor integração |

**Referências:**
- [Moshi Documentation](https://github.com/square/moshi)
- [Moshi vs Gson](https://proandroiddev.com/moshi-vs-gson-which-is-better-for-kotlin-projects-5c4b5c5e5e5e)

#### 4.4 Room Database

**Decisão: Usar Room para persistência local**

**Por quê?**

1. **Oficial Android**: Solução oficial do Google para persistência local
2. **Type Safety**: Queries type-safe em tempo de compilação
3. **Reativo**: Suporte nativo a Flow para observação reativa
4. **Performance**: Otimizado para Android, usa SQLite eficientemente
5. **Migrações**: Sistema robusto de migrações de banco de dados

**Estrutura:**

```kotlin
@Database(entities = [FavoriteMovieEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **SQLite Direto** | Controle total | Muito boilerplate, propenso a erros | Room reduz código e erros |
| **Realm** | Mais fácil de usar | Mais pesado, não oficial | Room é oficial e mais leve |
| **DataStore** | Mais moderno | Apenas para dados simples (key-value) | Room é melhor para dados relacionais |

**Referências:**
- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Room with Flow](https://developer.android.com/codelabs/android-room-with-a-view-kotlin#0)

#### 4.5 Flow para Dados Reativos

**Decisão: Usar Flow para observação reativa de dados do Room**

**Por quê?**

1. **Reatividade**: Atualizações automáticas quando dados mudam
2. **Coroutines**: Integração nativa com Coroutines
3. **Operadores**: Muitos operadores para transformação de dados
4. **Performance**: Mais eficiente que LiveData em alguns cenários
5. **Testabilidade**: Mais fácil de testar com Turbine

**Exemplo:**

```kotlin
@Query("SELECT * FROM favorite_movies")
fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **LiveData** | Lifecycle-aware automático | Menos operadores, não Coroutines-first | Flow oferece mais flexibilidade |
| **RxJava** | Muito poderoso | Curva de aprendizado alta, overhead | Flow é mais leve e nativo |

**Referências:**
- [Flow Documentation](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/)
- [Room with Flow](https://developer.android.com/codelabs/android-room-with-a-view-kotlin#0)

---

### 5. Dependency Injection

#### 5.1 Hilt

**Decisão: Usar Hilt para Dependency Injection**

**Por quê?**

1. **Oficial Android**: Solução oficial do Google baseada em Dagger
2. **Simplicidade**: Menos boilerplate que Dagger puro
3. **Integração**: Integração nativa com Android (Application, ViewModels, etc.)
4. **Testabilidade**: Facilita criação de testes com mocks
5. **Compile-time Safety**: Erros detectados em tempo de compilação

**Configuração:**

```kotlin
@HiltAndroidApp
class MyChallengeApplication : Application()

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideRetrofit(...): Retrofit { ... }
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Dagger** | Mais controle, mais features | Muito boilerplate, curva de aprendizado alta | Hilt é mais simples e suficiente |
| **Koin** | Mais simples, Kotlin-first | Runtime DI (menos seguro) | Hilt oferece compile-time safety |
| **Manual DI** | Controle total | Muito boilerplate, propenso a erros | Hilt reduz código e erros |

**Referências:**
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Hilt vs Dagger](https://developer.android.com/training/dependency-injection/hilt-android#hilt-vs-dagger)

---

### 6. UI/UX

#### 6.1 Material Design 3

**Decisão: Usar Material Design 3 como design system**

**Por quê?**

1. **Padrão Android**: Design system oficial do Google
2. **Consistência**: Garante consistência visual com outros apps Android
3. **Acessibilidade**: Componentes já incluem suporte a acessibilidade
4. **Temas**: Suporte a temas claro/escuro
5. **Componentes**: Componentes prontos e testados

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Design Custom** | Total controle visual | Muito trabalho, inconsistente | Material Design oferece consistência |
| **Material Design 2** | Mais maduro | Menos moderno | Material Design 3 é mais atual |

**Referências:**
- [Material Design 3](https://m3.material.io/)
- [Material Components for Android](https://github.com/material-components/material-components-android)

#### 6.2 Coil

**Decisão: Usar Coil para carregamento de imagens**

**Por quê?**

1. **Kotlin First**: Escrito em Kotlin, aproveitando recursos da linguagem
2. **Performance**: Mais rápido que Glide em alguns cenários
3. **Simplicidade**: API mais simples e intuitiva
4. **Coroutines**: Integração nativa com Coroutines
5. **Tamanho**: Biblioteca menor que Glide

**Exemplo:**

```kotlin
imageView.load(imageUrl) {
    crossfade(300)
    placeholder(R.drawable.placeholder)
    error(R.drawable.error)
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Glide** | Muito popular, maduro | Mais pesado, mais complexo | Coil é mais leve e Kotlin-first |
| **Picasso** | Simples | Menos features, menos mantido | Coil oferece mais features |
| **Fresco** | Muito poderoso | Mais pesado, mais complexo | Coil é mais leve e suficiente |

**Referências:**
- [Coil Documentation](https://coil-kt.github.io/coil/)
- [Coil vs Glide](https://coil-kt.github.io/coil/comparisons/)

#### 6.3 Shimmer Effect

**Decisão: Usar Shimmer para loading states**

**Por quê?**

1. **UX**: Melhor experiência do usuário durante carregamento
2. **Feedback Visual**: Usuário sabe que algo está carregando
3. **Profissionalismo**: Efeito moderno e profissional

**Referências:**
- [Shimmer for Android](https://github.com/facebook/shimmer-android)

---

### 7. Assíncrono

#### 7.1 Coroutines

**Decisão: Usar Coroutines para programação assíncrona**

**Por quê?**

1. **Kotlin Native**: Nativo do Kotlin, sem dependências externas
2. **Simplicidade**: Código mais legível que callbacks ou RxJava
3. **Performance**: Mais leve que threads tradicionais
4. **Integração**: Integração nativa com Android (ViewModel, Room, etc.)
5. **Testabilidade**: Facilita testes assíncronos

**Exemplo:**

```kotlin
viewModelScope.launch {
    val result = repository.getPopularMovies(1)
    result.onSuccess { movies ->
        _uiState.value = MovieListUiState.Success(movies)
    }.onFailure { error ->
        _uiState.value = MovieListUiState.Error(error.message)
    }
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Callbacks** | Simples | Callback hell, difícil de testar | Coroutines são mais legíveis |
| **RxJava** | Muito poderoso | Curva de aprendizado alta, overhead | Coroutines são mais leves e nativas |
| **AsyncTask** | Nativo do Android | Deprecated, não recomendado | Coroutines são a solução moderna |

**Referências:**
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Android Coroutines](https://developer.android.com/kotlin/coroutines)

---

### 8. Paginação

#### 8.1 Paging 3 (Migração Realizada)

**Decisão: Migrar de paginação manual para Paging 3**

**Por quê?**

1. **Biblioteca Oficial**: Solução oficial do Google para paginação
2. **Performance**: Cache automático, melhor gerenciamento de memória
3. **Estados**: Gerenciamento automático de estados (loading, error, empty)
4. **Atualizações Diferenciais**: Atualizações eficientes com DiffUtil
5. **Integração**: Integração nativa com RecyclerView e ViewModel

**Estrutura:**

```kotlin
class PopularMoviesPagingSource(
    private val apiService: TmdbApiService
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        // Implementação
    }
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Paginação Manual** | Controle total | Muito código, propenso a erros | Paging 3 reduz código e erros |
| **Paging 2** | Mais maduro | Menos features, mais complexo | Paging 3 é mais moderno e simples |

**Referências:**
- [Paging 3 Documentation](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- [Paging 3 Codelab](https://developer.android.com/codelabs/android-paging)

---

## Segurança

### 1. Gerenciamento de Tokens

**Decisão: Armazenar token da API em `local.properties` e expor via BuildConfig**

**Por quê?**

1. **Segurança**: Token não é commitado no repositório (`.gitignore`)
2. **Facilidade**: Fácil de configurar localmente
3. **CI/CD**: Pode ser configurado via secrets no GitHub Actions
4. **Build-time**: Token é injetado em tempo de build, não em runtime

**Implementação:**

```kotlin
// build.gradle.kts
fun getLocalProperty(key: String, defaultValue: String = ""): String {
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { localProperties.load(it) }
    }
    return localProperties.getProperty(key, defaultValue)
}

defaultConfig {
    val tmdbBearerToken = getLocalProperty("TMDB_BEARER_TOKEN", "")
    buildConfigField("String", "TMDB_BEARER_TOKEN", "\"$tmdbBearerToken\"")
}
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Hardcoded no código** | Simples | Inseguro, token commitado | `local.properties` é mais seguro |
| **Android Keystore** | Mais seguro | Mais complexo, requer setup adicional | `local.properties` é suficiente para desenvolvimento |
| **Variáveis de Ambiente** | Padrão em CI/CD | Requer configuração adicional | `local.properties` é mais simples para desenvolvimento |

**Melhorias Futuras:**
- Para produção: Usar Android Keystore ou variáveis de ambiente do CI/CD
- Implementar certificate pinning para API

**Referências:**
- [Android Keystore System](https://developer.android.com/training/articles/keystore)
- [Secrets Management](https://developer.android.com/studio/build/gradle-tips#use-properties-files)

---

## CI/CD e Qualidade

### 1. GitHub Actions

**Decisão: Usar GitHub Actions para CI/CD**

**Por quê?**

1. **Integração**: Integração nativa com GitHub
2. **Gratuito**: Gratuito para repositórios públicos
3. **Flexibilidade**: Muito flexível e configurável
4. **Ecossistema**: Grande quantidade de actions disponíveis

**Workflow Configurado:**

```yaml
- name: Run ktlint check
  run: ./gradlew :app:ktlintCheck --no-daemon

- name: Run detekt
  run: ./gradlew :app:detekt --no-daemon

- name: Run unit tests
  run: ./gradlew :app:testDebugUnitTest --no-daemon
```

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Jenkins** | Muito poderoso | Requer servidor próprio, mais complexo | GitHub Actions é mais simples |
| **CircleCI** | Boa integração | Requer configuração adicional | GitHub Actions é nativo |
| **GitLab CI** | Integrado ao GitLab | Não estamos usando GitLab | GitHub Actions é nativo do GitHub |

**Referências:**
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

### 2. ktlint

**Decisão: Usar ktlint para formatação de código**

**Por quê?**

1. **Padrão Kotlin**: Segue padrões oficiais do Kotlin
2. **Automático**: Pode formatar código automaticamente
3. **Integração**: Integração fácil com Gradle
4. **Consistência**: Garante consistência de código

**Referências:**
- [ktlint Documentation](https://ktlint.github.io/)

### 3. detekt

**Decisão: Usar detekt para análise estática de código**

**Por quê?**

1. **Kotlin First**: Especificamente para Kotlin
2. **Configurável**: Muito configurável
3. **Regras**: Muitas regras disponíveis
4. **Integração**: Integração fácil com Gradle

**Referências:**
- [detekt Documentation](https://detekt.github.io/detekt/)

### 4. JaCoCo

**Decisão: Usar JaCoCo para cobertura de código**

**Por quê?**

1. **Padrão**: Padrão da indústria para cobertura de código
2. **Integração**: Integração fácil com Gradle
3. **Relatórios**: Gera relatórios HTML e XML
4. **Gratuito**: Gratuito e open source

**Configuração Atual:**
- Relatórios são gerados mas verificação de mínimo está desabilitada
- Considerado como melhoria futura

**Referências:**
- [JaCoCo Documentation](https://www.jacoco.org/jacoco/trunk/doc/)

---

## Testes

### 1. Estrutura de Testes

**Decisão: Implementar testes unitários para camadas principais**

**Por quê?**

1. **Confiabilidade**: Garante que código funciona corretamente
2. **Refatoração**: Facilita refatoração segura
3. **Documentação**: Testes servem como documentação
4. **Regressão**: Previne regressões futuras

### 2. MockK

**Decisão: Usar MockK para mocking em testes**

**Por quê?**

1. **Kotlin First**: Projetado especificamente para Kotlin
2. **Simplicidade**: API mais simples que Mockito
3. **Type Safety**: Melhor suporte a tipos Kotlin
4. **Coroutines**: Suporte nativo a Coroutines

**Alternativas Consideradas:**

| Alternativa | Vantagens | Desvantagens | Por que não foi escolhida |
|------------|-----------|--------------|--------------------------|
| **Mockito** | Muito popular | Menos otimizado para Kotlin | MockK é melhor para Kotlin |
| **Mockito-Kotlin** | Wrapper Kotlin | Ainda usa Mockito por baixo | MockK é nativo |

**Referências:**
- [MockK Documentation](https://mockk.io/)

### 3. Turbine

**Decisão: Usar Turbine para testes de Flow**

**Por quê?**

1. **Flow Testing**: Especificamente para testar Flows
2. **Simplicidade**: API simples e intuitiva
3. **Integração**: Funciona bem com Coroutines Test

**Exemplo:**

```kotlin
viewModel.uiState.test {
    assertEquals(MovieListUiState.Loading, awaitItem())
    val successState = awaitItem()
    assertIs<MovieListUiState.Success>(successState)
}
```

**Referências:**
- [Turbine Documentation](https://github.com/cashapp/turbine)

### 4. Coroutines Test

**Decisão: Usar Coroutines Test para testes assíncronos**

**Por quê?**

1. **Controle de Tempo**: Permite controlar tempo em testes
2. **Determinismo**: Testes determinísticos
3. **Integração**: Integração nativa com Coroutines

**Referências:**
- [Coroutines Test](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/)

---

## Performance

### 1. Debounce na Pesquisa

**Decisão: Implementar debounce de 500ms na pesquisa**

**Por quê?**

1. **Performance**: Reduz número de requisições à API
2. **UX**: Melhora experiência do usuário evitando buscas desnecessárias
3. **Economia**: Reduz uso de dados e recursos do servidor
4. **Rate Limiting**: Evita atingir limites de rate da API

**Implementação:**

```kotlin
searchQuery
    .debounce(500)
    .distinctUntilChanged()
    .flatMapLatest { query ->
        if (query.isBlank()) {
            popularMoviesFlow
        } else {
            searchMoviesFlow(query)
        }
    }
```

**Referências:**
- [Flow Operators - debounce](https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/debounce.html)

### 2. Cache de Imagens

**Decisão: Usar Coil com cache automático**

**Por quê?**

1. **Performance**: Imagens são cacheadas automaticamente
2. **UX**: Carregamento mais rápido em visualizações subsequentes
3. **Economia**: Reduz uso de dados

**Referências:**
- [Coil Caching](https://coil-kt.github.io/coil/getting_started/#caching)

---

## Decisões de Design

### 1. Adaptive Icon

**Decisão: Usar Adaptive Icon com PNG como foreground**

**Por quê?**

1. **Compatibilidade**: Funciona em diferentes formatos de ícone (quadrado, redondo)
2. **Flexibilidade**: Permite usar imagens PNG personalizadas
3. **Padrão Android**: Padrão recomendado pelo Android

**Estrutura:**

```xml
<adaptive-icon>
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@drawable/ic_launcher_foreground" />
</adaptive-icon>
```

**Referências:**
- [Adaptive Icons](https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive)

---

## Melhorias Futuras

### 1. Migração para Jetpack Compose

**Status**: Planejado como melhoria futura

**Por quê?**

1. **Modernização**: Compose é o futuro do Android UI
2. **Menos Boilerplate**: Menos código que Views tradicionais
3. **Performance**: Recomposição inteligente
4. **DX**: Melhor experiência de desenvolvimento

**Referências:**
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

### 2. Paging 3

**Status**: ✅ Implementado

**Referências:**
- [Paging 3 Documentation](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)

---

## Conclusão

Este documento detalhou todas as decisões técnicas tomadas durante o desenvolvimento do aplicativo **My Challenge**, explicando o **porquê** de cada escolha, as **alternativas consideradas** e os **trade-offs** envolvidos.

Cada decisão foi tomada considerando:
- **Manutenibilidade**: Facilidade de manter e evoluir o código
- **Testabilidade**: Facilidade de testar o código
- **Performance**: Impacto na performance do aplicativo
- **Experiência do Desenvolvedor**: Facilidade de desenvolvimento
- **Padrões da Indústria**: Alinhamento com melhores práticas

---

## Referências e Documentação

### Documentação Oficial

- [Android Developer Guide](https://developer.android.com/guide)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Coroutines Guide](https://kotlinlang.org/docs/coroutines-guide.html)
- [Room Documentation](https://developer.android.com/training/data-storage/room)
- [Retrofit Documentation](https://square.github.io/retrofit/)
- [Hilt Documentation](https://developer.android.com/training/dependency-injection/hilt-android)
- [Material Design 3](https://m3.material.io/)

### Artigos e Tutoriais

- [Clean Architecture - Uncle Bob](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Android Architecture Guide](https://developer.android.com/topic/architecture)
- [StateFlow vs LiveData](https://developer.android.com/kotlin/flow/stateflow-and-sharedflow)
- [Paging 3 Codelab](https://developer.android.com/codelabs/android-paging)

### Ferramentas e Bibliotecas

- [Coil](https://coil-kt.github.io/coil/)
- [MockK](https://mockk.io/)
- [Turbine](https://github.com/cashapp/turbine)
- [ktlint](https://ktlint.github.io/)
- [detekt](https://detekt.github.io/detekt/)

---

**Última atualização**: Dezembro 2024

