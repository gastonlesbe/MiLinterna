#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

# Contraseña a probar
PASSWORD="gsl23175"

# Lista de keystores
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

echo "=========================================="
echo "Verificando con: gsl23175"
echo "=========================================="
echo ""
echo "SHA1 esperado: $EXPECTED_SHA1"
echo ""

FOUND=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    echo "Probando contraseña: $PASSWORD"
    echo ""
    
    # Intentar listar los aliases
    ALIASES=$(keytool -list -keystore "$keystore" -storepass "$PASSWORD" 2>&1)
    
    if echo "$ALIASES" | grep -q "Alias name:"; then
        echo "✓ ¡Contraseña correcta!"
        echo ""
        
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
            SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$PASSWORD" -alias "$alias" 2>&1)
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
                    echo "  storePassword=$PASSWORD"
                    echo "  keyAlias=$alias"
                    echo "  keyPassword=$PASSWORD"
                    echo ""
                    
                    # Crear el archivo automáticamente
                    cat > keystore.properties << EOF
storeFile=$keystore
storePassword=$PASSWORD
keyAlias=$alias
keyPassword=$PASSWORD
EOF
                    echo "  ✓ Archivo keystore.properties creado automáticamente"
                    echo ""
                    FOUND=true
                else
                    echo "  ❌ SHA1 no coincide (esperado: $EXPECTED_SHA1)"
                fi
                echo ""
            fi
        done
    else
        echo "❌ Contraseña incorrecta"
    fi
    echo ""
done

if [ "$FOUND" = false ]; then
    echo "=========================================="
    echo "⚠️  No se encontró el keystore con el SHA1 correcto"
    echo "=========================================="
    echo ""
    echo "La contraseña funcionó con algún keystore, pero el SHA1 no coincide."
    echo "SHA1 esperado: $EXPECTED_SHA1"
fi

