# Guia de Aplicação de Tema Material Components

## Estrutura de Arquivos

### 1. `colors.xml` (values/)
Contém todas as paletas de cores do Material Design 3, incluindo:
- **Cores Primárias**: `md_theme_light_primary`, `md_theme_light_onPrimary`, etc.
- **Cores Secundárias**: `md_theme_light_secondary`, `md_theme_light_onSecondary`, etc.
- **Cores de Superfície**: `md_theme_light_surface`, `md_theme_light_onSurface`, etc.
- **Cores de Erro**: `md_theme_light_error`, `md_theme_light_onError`, etc.
- **Versões Dark**: Todas as cores têm versões para tema escuro (`md_theme_dark_*`)

### 2. `themes.xml` (values/)
Define o tema claro (light theme) com todas as cores aplicadas.

### 3. `themes.xml` (values-night/)
Define o tema escuro (dark theme) que será aplicado automaticamente quando o sistema estiver em modo escuro.

### 4. `styles.xml` (values/)
Contém estilos customizados para componentes Material:
- `Widget.MyChallenge.TextInputLayout.OutlinedBox`
- `Widget.MyChallenge.Card`
- `Widget.MyChallenge.Button`
- `TextAppearance.MyChallenge.*`

## Como Usar

### 1. Aplicar Tema no AndroidManifest.xml
```xml
<application
    android:theme="@style/Theme.MyChallenge"
    ...>
```

### 2. Usar Cores nos Layouts XML
```xml
<!-- Usar cores diretamente -->
<TextView
    android:textColor="@color/md_theme_light_primary"
    ... />

<!-- Usar atributos de tema -->
<TextView
    android:textColor="?attr/colorOnSurface"
    ... />
```

### 3. Usar Estilos Customizados
```xml
<!-- TextInputLayout -->
<com.google.android.material.textfield.TextInputLayout
    style="@style/Widget.MyChallenge.TextInputLayout.OutlinedBox"
    ... />

<!-- Card -->
<com.google.android.material.card.MaterialCardView
    style="@style/Widget.MyChallenge.Card"
    ... />
```

### 4. Atributos de Tema Disponíveis

#### Cores Principais
- `?attr/colorPrimary` - Cor primária do tema
- `?attr/colorOnPrimary` - Cor do texto sobre cor primária
- `?attr/colorPrimaryVariant` - Variante da cor primária

#### Cores Secundárias
- `?attr/colorSecondary` - Cor secundária
- `?attr/colorOnSecondary` - Cor do texto sobre cor secundária

#### Cores de Superfície
- `?attr/colorSurface` - Cor de fundo de superfícies (cards, dialogs)
- `?attr/colorOnSurface` - Cor do texto sobre superfície
- `?attr/colorSurfaceVariant` - Variante da cor de superfície
- `?attr/colorOnSurfaceVariant` - Cor do texto sobre variante de superfície

#### Cores de Erro
- `?attr/colorError` - Cor de erro
- `?attr/colorOnError` - Cor do texto sobre erro

#### Outras Cores
- `?attr/colorOutline` - Cor de contorno
- `?attr/colorBackground` - Cor de fundo da tela

## Personalização

### Alterar Cores Primárias
1. Edite `colors.xml` e altere os valores de `md_theme_light_primary` e `md_theme_dark_primary`
2. O tema será atualizado automaticamente

### Criar Novos Estilos
1. Adicione novos estilos em `styles.xml`
2. Use `parent` para herdar de estilos Material existentes
3. Sobrescreva atributos conforme necessário

### Exemplo de Estilo Customizado
```xml
<style name="Widget.MyChallenge.CustomButton" parent="Widget.MaterialComponents.Button">
    <item name="backgroundTint">@color/md_theme_light_primary</item>
    <item name="android:textColor">@color/md_theme_light_onPrimary</item>
    <item name="cornerRadius">12dp</item>
    <item name="android:textSize">16sp</item>
</style>
```

## Suporte a Dark Mode

O tema suporta automaticamente modo escuro através do `Theme.MaterialComponents.DayNight`. Quando o sistema está em modo escuro:
- O Android automaticamente usa `values-night/themes.xml`
- Todas as cores dark são aplicadas automaticamente
- Não é necessário código adicional

## Boas Práticas

1. **Sempre use atributos de tema** (`?attr/colorPrimary`) ao invés de cores diretas quando possível
2. **Use cores semânticas** (`colorOnSurface`, `colorOnPrimary`) para garantir contraste adequado
3. **Teste em modo claro e escuro** para garantir legibilidade
4. **Mantenha consistência** usando os estilos customizados criados
5. **Use Material Components** ao invés de componentes Android padrão para melhor integração com o tema

## Recursos Adicionais

- [Material Design 3 Color System](https://m3.material.io/styles/color/the-color-system/overview)
- [Material Components for Android](https://github.com/material-components/material-components-android)
- [Material Theme Builder](https://m3.material.io/theme-builder)

