# Guia para Adicionar Screenshots

Este guia explica como adicionar screenshots reais ao README do projeto.

## 📸 Screenshots Necessários

Para uma apresentação completa do projeto, capture os seguintes screenshots:

### 1. Splash Screen
- **Arquivo**: `docs/screenshots/splash_screen.png`
- **Descrição**: Tela inicial com logo do aplicativo
- **Como capturar**: Execute o app e capture a primeira tela que aparece

### 2. Lista de Filmes Populares
- **Arquivo**: `docs/screenshots/movie_list.png`
- **Descrição**: Lista de filmes com scroll infinito
- **Como capturar**: Após o splash, capture a tela principal com a lista de filmes

### 3. Pesquisa de Filmes
- **Arquivo**: `docs/screenshots/movie_search.png`
- **Descrição**: Resultados de pesquisa
- **Como capturar**: Digite algo no campo de busca e capture os resultados

### 4. Detalhes do Filme
- **Arquivo**: `docs/screenshots/movie_details.png`
- **Descrição**: Tela de detalhes completa
- **Como capturar**: Clique em um filme e capture a tela de detalhes

### 5. Lista de Favoritos
- **Arquivo**: `docs/screenshots/favorites_list.png`
- **Descrição**: Tela de favoritos com filmes
- **Como capturar**: Adicione alguns favoritos e capture a tela de favoritos

### 6. Favoritos Vazios
- **Arquivo**: `docs/screenshots/favorites_empty.png`
- **Descrição**: Mensagem quando não há favoritos
- **Como capturar**: Remova todos os favoritos e capture a mensagem

### 7. Erro de Conexão
- **Arquivo**: `docs/screenshots/error_connection.png`
- **Descrição**: Tela de erro de conexão
- **Como capturar**: Desative a internet e tente carregar filmes

### 8. Erro Genérico
- **Arquivo**: `docs/screenshots/error_generic.png`
- **Descrição**: Tela de erro genérica
- **Como capturar**: Force um erro (ex: timeout) e capture a tela

## 🛠️ Como Capturar Screenshots no Android

### Opção 1: Android Studio
1. Execute o app no emulador
2. Use o botão de screenshot na barra de ferramentas do emulador
3. Salve na pasta `docs/screenshots/`

### Opção 2: Dispositivo Físico
1. Execute o app no dispositivo
2. Use a combinação de teclas do dispositivo (geralmente Power + Volume Down)
3. Transfira para o computador e salve em `docs/screenshots/`

### Opção 3: ADB (Android Debug Bridge)
```bash
# Conecte o dispositivo via USB ou WiFi
adb devices

# Capture screenshot
adb shell screencap -p /sdcard/screenshot.png

# Baixe para o computador
adb pull /sdcard/screenshot.png docs/screenshots/movie_list.png
```

## 📐 Especificações Recomendadas

- **Resolução**: Mínimo 1080x1920 (Full HD)
- **Formato**: PNG (melhor qualidade)
- **Tamanho**: Máximo 2MB por imagem
- **Orientação**: Retrato (vertical)

## ✨ Dicas para Screenshots Profissionais

1. **Use Modo Claro**: Screenshots em modo claro são mais legíveis
2. **Remova Informações Sensíveis**: Certifique-se de não capturar dados pessoais
3. **Mostre Funcionalidades**: Capture telas que demonstrem as funcionalidades principais
4. **Consistência**: Use o mesmo dispositivo/emulador para todos os screenshots
5. **Qualidade**: Use alta resolução para melhor visualização

## 🔄 Após Capturar

1. Renomeie os arquivos conforme a lista acima
2. Salve em `docs/screenshots/`
3. Os screenshots serão automaticamente exibidos no README.md

## 📝 Nota

Atualmente, o README usa placeholders para os screenshots. Após adicionar os screenshots reais, eles serão automaticamente exibidos.

