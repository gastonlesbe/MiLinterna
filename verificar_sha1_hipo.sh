#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

# SHA1 del AAB incorrecto que subiste
INCORRECT_SHA1="62:B6:6F:1D:92:87:BF:4C:76:8A:3C:D0:24:39:90:C3:11:15:05:34"
INCORRECT_SHA1_CLEAN=$(echo "$INCORRECT_SHA1" | tr -d ':')

# Contraseñas a probar
PASSWORDS=("Hipo0809" "Octopus2317522" "goldfish210809")

# Lista de keystores
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

echo "=========================================="
echo "Verificador de SHA1 con Hipo0809"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""
echo "SHA1 del AAB que subiste (incorrecto):"
echo "$INCORRECT_SHA1"
echo ""
echo "Probando contraseñas..."
echo ""

FOUND_CORRECT=false
FOUND_INCORRECT=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Verificando: $keystore"
    echo "=========================================="
    
    for password in "${PASSWORDS[@]}"; do
        echo "Probando contraseña: $password"
        
        # Intentar listar los aliases
        ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
        
        if echo "$ALIASES" | grep -q "Alias name:"; then
            echo "✓ Contraseña correcta!"
            
            # Obtener lista de aliases
            ALIAS_LIST=$(echo "$ALIASES" | grep "Alias name:" | awk '{print $3}')
            
            echo "Aliases encontrados:"
            echo "$ALIAS_LIST" | while read alias; do
                echo "  - $alias"
            done
            echo ""
            
            # Verificar SHA1 de cada alias
            echo "Verificando SHA1 de cada alias..."
            echo ""
            
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
                    elif [ "$SHA1" = "$INCORRECT_SHA1_CLEAN" ]; then
                        echo ""
                        echo "  ⚠️  Este es el keystore que usaste (SHA1 incorrecto)"
                        echo "  No uses este keystore para el nuevo AAB"
                        echo ""
                        FOUND_INCORRECT=true
                    else
                        echo "  ❌ SHA1 diferente"
                    fi
                    echo ""
                fi
            done
            
            break  # Si la contraseña funcionó, no probar las demás
        else
            echo "  ❌ Contraseña incorrecta"
        fi
    done
    echo ""
done

if [ "$FOUND_CORRECT" = false ]; then
    echo "=========================================="
    if [ "$FOUND_INCORRECT" = true ]; then
        echo "⚠️  Se encontró el keystore incorrecto que usaste"
        echo "   pero NO se encontró el keystore correcto"
    else
        echo "⚠️  No se encontró ningún keystore con el SHA1 esperado"
    fi
    echo "=========================================="
    echo ""
    echo "SHA1 esperado: $EXPECTED_SHA1"
    echo ""
    echo "Verifica que:"
    echo "1. Las contraseñas sean correctas"
    echo "2. El keystore correcto esté en este directorio"
    echo "3. Puedas tener otros keystores en otros lugares"
fi

