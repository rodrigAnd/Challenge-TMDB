sh
#!/bin/bash

# ==============================================================================
# Script Interativo para Captura de Screenshots para o Projeto My Challenge
# ==============================================================================
#
# Este script automatiza a captura, o download, o renomeio e a organização
# das screenshots necessárias para o arquivo README.md.
#
# Pré-requisitos:
# 1. 'adb' deve estar instalado e acessível no PATH do sistema.
# 2. Um dispositivo Android ou emulador deve estar conectado e visível via `adb devices`.
# 3. O script deve ser executado da pasta raiz do projeto.
#
# ==============================================================================

# --- Configuração ---
# Pasta de destino final para as screenshots
DEST_FOLDER="docs/screenshots"
# Local temporário no dispositivo para salvar a captura
DEVICE_TEMP_PATH="/sdcard/screenshot_temp.png"

# --- Funções Auxiliares ---

# Função para imprimir cabeçalhos e dar um visual melhor
print_header() {
    echo ""
    echo "=========================================================="
    echo "  $1"
    echo "=========================================================="
}

# Função principal que realiza o processo de captura
capture_and_process() {
    local screen_name=$1 # Ex: "Tela Inicial (Splash Screen)"
    local file_name=$2   # Ex: "splash_screen.png"

    print_header "Preparando para capturar: $screen_name"
    echo "Por favor, navegue até a tela correspondente no seu aplicativo."
    read -p "Quando a tela estiver pronta, pressione [Enter] para continuar..."

    echo "📸 Capturando tela..."
    adb shell screencap -p "$DEVICE_TEMP_PATH"

    if [ $? -ne 0 ]; then
        echo "❌ Erro: Falha ao capturar a tela com adb. Verifique se o dispositivo está conectado."
        exit 1
    fi

    echo "📥 Baixando captura do dispositivo..."
    adb pull "$DEVICE_TEMP_PATH" "$file_name"

    if [ $? -ne 0 ]; then
        echo "❌ Erro: Falha ao baixar a captura do dispositivo."
        exit 1
    fi

    echo "📁 Movendo para $DEST_FOLDER/$file_name..."
    mv "$file_name" "$DEST_FOLDER/"

    echo "✅ Sucesso! Screenshot '$file_name' salva."
}

# --- Execução Principal ---

print_header "Início do Script de Captura de Screenshots"

# Verifica se a pasta de destino existe, senão a cria
if [ ! -d "$DEST_FOLDER" ]; then
    echo "Criando pasta de destino: $DEST_FOLDER"
    mkdir -p "$DEST_FOLDER"
fi

# Verifica se o adb está disponível
if ! command -v adb &> /dev/null; then
    echo "❌ Erro: O comando 'adb' não foi encontrado. Certifique-se de que as Ferramentas de Plataforma do Android SDK estão no seu PATH."
    exit 1
fi

echo "Verificando dispositivos conectados..."
adb devices

echo ""
echo "O script começará agora. Siga as instruções para cada tela."
echo "Certifique-se de que apenas um dispositivo/emulador esteja ativo."
echo ""

# Lista de screenshots a serem capturadas (baseado no seu README)
capture_and_process "Tela Inicial (Splash Screen)" "splash_screen.png"
capture_and_process "Lista de Filmes Populares" "movie_list.png"
capture_and_process "Pesquisa de Filmes" "movie_search.png"
capture_and_process "Detalhes do Filme" "movie_details.png"
capture_and_process "Lista de Favoritos" "favorites_list.png"
capture_and_process "Favoritos Vazios" "favorites_empty.png"
capture_and_process "Erro de Conexão" "error_connection.png"
capture_and_process "Erro Genérico" "error_generic.png"

# Limpeza final no dispositivo
echo "🧹 Limpando arquivo temporário do dispositivo..."
adb shell rm "$DEVICE_TEMP_PATH"

print_header "🎉 Processo Concluído! Todas as screenshots foram capturadas."
echo "Verifique a pasta '$DEST_FOLDER' e faça o commit dos novos arquivos."
echo ""
