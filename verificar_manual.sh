#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Verificación Manual de Keystores"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""
echo "Este script te permitirá probar manualmente cada keystore"
echo "con las contraseñas que recuerdes."
echo ""

KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    echo ""
    
    while true; do
        read -sp "Ingresa la contraseña para $keystore (o Enter para saltar): " password
        echo ""
        
        if [ -z "$password" ]; then
            echo "Saltando $keystore..."
            break
        fi
        
        # Intentar listar aliases
        ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
        
        if echo "$ALIASES" | grep -q "Alias name:"; then
            echo "✓ Contraseña correcta!"
            echo ""
            
            # Obtener lista de aliases
            ALIAS_LIST=$(echo "$ALIASES" | grep "Alias name:" | awk '{print $3}')
            
            echo "Aliases encontrados:"
            echo "$ALIAS_LIST" | while read alias; do
                echo "  - $alias"
            done
            echo ""
            
            # Verificar SHA1 de cada alias
            echo "SHA1 de cada alias:"
            echo ""
            
            FOUND_CORRECT=false
            
            echo "$ALIAS_LIST" | while read alias; do
                SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$password" -alias "$alias" 2>&1)
                SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | awk '{print $2}' | tr -d ':')
                
                if [ -n "$SHA1" ]; then
                    SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
                    echo "  Alias: $alias"
                    echo "  SHA1:  $SHA1_FORMATTED"
                    
                    if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
                        echo ""
                        echo "  ✅ ✅ ✅ ¡ENCONTRADO! Este es el keystore CORRECTO ✅ ✅ ✅"
                        echo ""
                        echo "  Configuración para keystore.properties:"
                        echo "  storeFile=$keystore"
                        echo "  storePassword=$password"
                        echo "  keyAlias=$alias"
                        echo "  keyPassword=$password"
                        echo ""
                        
                        # Crear el archivo automáticamente
                        cat > keystore.properties << EOF
storeFile=$keystore
storePassword=$password
keyAlias=$alias
keyPassword=$password
EOF
                        echo "  ✓ Archivo keystore.properties creado automáticamente"
                        echo ""
                        FOUND_CORRECT=true
                    else
                        echo "  ❌ SHA1 no coincide"
                    fi
                    echo ""
                fi
            done
            
            if [ "$FOUND_CORRECT" = false ]; then
                read -p "¿Deseas probar otra contraseña para este keystore? (s/n): " otra
                if [ "$otra" != "s" ] && [ "$otra" != "S" ]; then
                    break
                fi
            else
                break
            fi
        else
            echo "❌ Contraseña incorrecta"
            read -p "¿Intentar otra contraseña? (s/n): " otra
            if [ "$otra" != "s" ] && [ "$otra" != "S" ]; then
                break
            fi
        fi
        echo ""
    done
    echo ""
done

echo "=========================================="
echo "Verificación completada"
echo "=========================================="

