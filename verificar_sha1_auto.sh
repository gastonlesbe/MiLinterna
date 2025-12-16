#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

# Contraseñas a probar
PASSWORDS=("Octopus2317522" "goldfish210809")

# Lista de keystores a verificar
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

echo "=========================================="
echo "Verificador Automático de SHA1"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play:"
echo "$EXPECTED_SHA1"
echo ""
echo "Probando contraseñas en cada keystore..."
echo ""

FOUND=false

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
            echo "$ALIAS_LIST" | while read alias; do
                SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$password" -alias "$alias" 2>&1)
                SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | awk '{print $2}' | tr -d ':')
                
                if [ -n "$SHA1" ]; then
                    SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
                    echo "  Alias: $alias"
                    echo "  SHA1:  $SHA1_FORMATTED"
                    
                    if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
                        echo ""
                        echo "  ✅ ✅ ✅ ¡ENCONTRADO! Este es el keystore correcto ✅ ✅ ✅"
                        echo ""
                        echo "  Configuración para keystore.properties:"
                        echo "  storeFile=$keystore"
                        echo "  storePassword=$password"
                        echo "  keyAlias=$alias"
                        echo "  keyPassword=$password"
                        echo ""
                        FOUND=true
                    else
                        echo "  ❌ No coincide"
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

if [ "$FOUND" = false ]; then
    echo "=========================================="
    echo "⚠️  No se encontró ningún keystore con el SHA1 esperado"
    echo "=========================================="
    echo ""
    echo "El SHA1 esperado es: $EXPECTED_SHA1"
    echo "Verifica que estés usando el keystore correcto."
fi

