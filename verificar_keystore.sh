#!/bin/bash

echo "=========================================="
echo "Verificador de Keystore"
echo "=========================================="
echo ""
echo "Este script te ayudará a verificar el alias de tu keystore"
echo ""

read -sp "Ingresa la contraseña del keystore key.jks: " STORE_PASS
echo ""
echo ""

echo "Listando aliases disponibles en key.jks..."
echo ""

if keytool -list -v -keystore key.jks -storepass "$STORE_PASS" 2>/dev/null; then
    echo ""
    echo "=========================================="
    echo "✓ Keystore verificado correctamente"
    echo "=========================================="
    echo ""
    echo "Busca la línea que dice 'Alias name:' para ver el alias"
    echo "El alias es el nombre que necesitas para generar el AAB"
else
    echo ""
    echo "Error: Contraseña incorrecta o problema con el keystore"
    exit 1
fi

