#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Verificador de SHA1 de Keystores"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""
echo "Este script verificará cada keystore para encontrar"
echo "cuál tiene el SHA1 correcto."
echo ""

# Lista de keystores a verificar
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        echo "⚠️  $keystore no encontrado, saltando..."
        echo ""
        continue
    fi
    
    echo "=========================================="
    echo "Verificando: $keystore"
    echo "=========================================="
    echo ""
    read -sp "Ingresa la contraseña del keystore $keystore: " STORE_PASS
    echo ""
    echo ""
    
    # Intentar listar los aliases primero
    ALIASES=$(keytool -list -keystore "$keystore" -storepass "$STORE_PASS" 2>&1 | grep "Alias name:" | awk '{print $3}')
    
    if [ -z "$ALIASES" ]; then
        echo "❌ Error: Contraseña incorrecta o problema con el keystore"
        echo ""
        continue
    fi
    
    echo "✓ Contraseña correcta!"
    echo "Aliases encontrados:"
    echo "$ALIASES" | while read alias; do
        echo "  - $alias"
    done
    echo ""
    
    # Verificar SHA1 de cada alias
    echo "Verificando SHA1 de cada alias..."
    echo ""
    
    echo "$ALIASES" | while read alias; do
        SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$STORE_PASS" -alias "$alias" 2>&1)
        SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | awk '{print $2}' | tr -d ':')
        
        if [ -n "$SHA1" ]; then
            SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
            echo "Alias: $alias"
            echo "SHA1:  $SHA1_FORMATTED"
            
            if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
                echo "✅ ✅ ✅ ¡COINCIDE! Este es el keystore correcto ✅ ✅ ✅"
                echo ""
                echo "Configuración para keystore.properties:"
                echo "storeFile=$keystore"
                echo "keyAlias=$alias"
                echo ""
            else
                echo "❌ No coincide"
            fi
            echo ""
        fi
    done
    
    echo ""
    read -p "¿Deseas verificar otro keystore? (s/n): " continuar
    if [ "$continuar" != "s" ] && [ "$continuar" != "S" ]; then
        break
    fi
    echo ""
done

echo ""
echo "=========================================="
echo "Verificación completada"
echo "=========================================="

