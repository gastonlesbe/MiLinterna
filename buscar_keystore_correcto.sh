#!/bin/bash

# SHA1 del certificado de carga según Google Play Console
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Buscador del Keystore Correcto"
echo "=========================================="
echo ""
echo "SHA1 esperado (de Google Play Console):"
echo "$EXPECTED_SHA1"
echo ""
echo "Este es el SHA1 del 'Certificado de clave de carga'"
echo "que Google Play Console muestra."
echo ""
echo "Buscando en todos los keystores disponibles..."
echo ""

# Buscar todos los keystores en el sistema
echo "Keystores encontrados:"
find ~/StudioProjects -name "*.jks" -o -name "*.keystore" 2>/dev/null | while read keystore; do
    echo "  - $keystore"
done
echo ""

# Lista de keystores conocidos
KEYSTORES=(
    "key.jks"
    "LightOnNoti.jks"
    "LightOnNoti1.jks"
    "../MyRide/key.jks"
)

FOUND=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    echo ""
    echo "Para verificar este keystore, necesito la contraseña."
    echo "Si no la recuerdas, puedes presionar Enter para saltarlo."
    echo ""
    
    read -sp "Contraseña para $keystore (o Enter para saltar): " password
    echo ""
    
    if [ -z "$password" ]; then
        echo "Saltando $keystore..."
        echo ""
        continue
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
        echo "Verificando SHA1..."
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
                    FOUND=true
                else
                    echo "  ❌ SHA1 no coincide"
                fi
                echo ""
            fi
        done
    else
        echo "❌ Contraseña incorrecta"
    fi
    echo ""
done

echo "=========================================="
if [ "$FOUND" = true ]; then
    echo "✅ ¡Keystore correcto encontrado!"
    echo "   El archivo keystore.properties ha sido creado"
    echo ""
    echo "Ahora puedes generar el AAB con:"
    echo "  ./gradlew bundleRelease"
else
    echo "⚠️  No se encontró el keystore con el SHA1 correcto"
    echo "=========================================="
    echo ""
    echo "OPCIÓN: Solicitar restablecimiento de la clave de carga"
    echo ""
    echo "Si no puedes encontrar el keystore correcto, puedes:"
    echo ""
    echo "1. Ve a Google Play Console"
    echo "2. Configuración → Integridad de la app → Firma de apps de Play"
    echo "3. Busca 'Cómo solicitar que se restablezca la clave de carga'"
    echo "4. Haz clic en 'Solicitar que se restablezca la clave de carga'"
    echo ""
    echo "Esto te permitirá usar un nuevo keystore para futuras actualizaciones."
    echo ""
    echo "⚠️  IMPORTANTE: Esto solo se puede hacer UNA VEZ al año."
fi
echo "=========================================="

