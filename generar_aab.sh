#!/bin/bash

# Script para generar AAB firmado
# Este script te pedirá las credenciales del keystore

echo "=========================================="
echo "Generador de AAB firmado para Play Store"
echo "=========================================="
echo ""

# Verificar si existe keystore.properties
if [ -f "keystore.properties" ]; then
    echo "✓ Archivo keystore.properties encontrado"
    echo ""
    read -p "¿Deseas usar las credenciales existentes? (s/n): " usar_existentes
    if [ "$usar_existentes" != "s" ] && [ "$usar_existentes" != "S" ]; then
        rm keystore.properties
    fi
fi

# Si no existe keystore.properties, pedir credenciales
if [ ! -f "keystore.properties" ]; then
    echo "Necesito las credenciales del keystore key.jks"
    echo ""
    read -sp "Contraseña del keystore: " STORE_PASSWORD
    echo ""
    read -p "Alias de la clave: " KEY_ALIAS
    read -sp "Contraseña del alias: " KEY_PASSWORD
    echo ""
    
    # Crear archivo keystore.properties
    cat > keystore.properties << EOF
storeFile=key.jks
storePassword=$STORE_PASSWORD
keyAlias=$KEY_ALIAS
keyPassword=$KEY_PASSWORD
EOF
    
    echo "✓ Archivo keystore.properties creado"
    echo ""
fi

# Limpiar builds anteriores
echo "Limpiando builds anteriores..."
./gradlew clean

# Generar AAB
echo ""
echo "Generando AAB firmado..."
echo ""

if ./gradlew bundleRelease; then
    AAB_PATH="app/build/outputs/bundle/release/app-release.aab"
    if [ -f "$AAB_PATH" ]; then
        AAB_SIZE=$(du -h "$AAB_PATH" | cut -f1)
        echo ""
        echo "=========================================="
        echo "✓ AAB generado exitosamente!"
        echo "=========================================="
        echo "Ubicación: $AAB_PATH"
        echo "Tamaño: $AAB_SIZE"
        echo ""
        echo "Puedes subir este archivo a Google Play Console"
        echo "=========================================="
    else
        echo "Error: El AAB no se generó correctamente"
        exit 1
    fi
else
    echo ""
    echo "Error al generar el AAB. Revisa los mensajes de error arriba."
    exit 1
fi

