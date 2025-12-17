#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

# Contraseñas base y sus variaciones
BASE_PASSWORDS=("Octopus2317522" "goldfish210809")

# Generar variaciones
PASSWORDS=()
for base in "${BASE_PASSWORDS[@]}"; do
    PASSWORDS+=("$base")
    PASSWORDS+=("${base^}")  # Primera letra mayúscula
    PASSWORDS+=("${base,,}")  # Todo minúsculas
    PASSWORDS+=("${base^^}")  # Todo mayúsculas
done

# Lista de keystores
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

echo "=========================================="
echo "Verificador con Variaciones de Contraseñas"
echo "=========================================="
echo ""
echo "SHA1 esperado: $EXPECTED_SHA1"
echo ""
echo "Probando variaciones de contraseñas..."
echo ""

FOUND=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    
    for password in "${PASSWORDS[@]}"; do
        # Intentar listar aliases (silenciosamente)
        ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
        
        if echo "$ALIASES" | grep -q "Alias name:"; then
            echo "✓ Contraseña correcta: $password"
            
            # Obtener lista de aliases
            ALIAS_LIST=$(echo "$ALIASES" | grep "Alias name:" | awk '{print $3}')
            
            echo "Aliases:"
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
                        echo "  ✅ ✅ ✅ ¡ENCONTRADO! ✅ ✅ ✅"
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
                        FOUND=true
                    else
                        echo "  ❌ SHA1 no coincide (esperado: $EXPECTED_SHA1)"
                    fi
                    echo ""
                fi
            done
            
            break  # Si la contraseña funcionó, no probar más variaciones
        fi
    done
    echo ""
done

if [ "$FOUND" = false ]; then
    echo "=========================================="
    echo "⚠️  No se encontró el keystore correcto"
    echo "=========================================="
    echo ""
    echo "Posibles razones:"
    echo "1. Las contraseñas proporcionadas no son correctas"
    echo "2. El keystore correcto no está en este directorio"
    echo "3. El alias puede tener una contraseña diferente"
    echo ""
    echo "SHA1 esperado: $EXPECTED_SHA1"
    echo ""
    echo "¿Puedes verificar si hay otros keystores o si las contraseñas"
    echo "pueden tener variaciones adicionales?"
fi

