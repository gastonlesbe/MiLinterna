#!/bin/bash

# SHA1 esperado por Google Play Store (del mensaje de error)
EXPECTED_SHA1="97:33:B7:09:B1:B5:F4:F8:40:72:DB:F2:5E:C1:39:3D:57:71:6B:23"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

echo "=========================================="
echo "Buscador del Keystore Correcto para Play Store"
echo "=========================================="
echo ""
echo "SHA1 esperado por Google Play Store:"
echo "$EXPECTED_SHA1"
echo ""
echo "SHA1 del keystore usado actualmente (milinterna_upload_keystore.jks):"
echo "D2:EA:FD:99:BB:6B:03:F8:77:F2:FE:60:80:C3:A7:FD:11:8B:F9:40"
echo ""
echo "Buscando en todos los keystores disponibles..."
echo ""

# Lista de keystores a verificar
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

FOUND=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        echo "⚠️  $keystore no encontrado, saltando..."
        echo ""
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    echo ""
    echo "Necesito la contraseña para verificar este keystore."
    echo "Si no la recuerdas, presiona Enter para saltarlo."
    echo ""
    
    read -sp "Contraseña para $keystore (o Enter para saltar): " password
    echo ""
    echo ""
    
    if [ -z "$password" ]; then
        echo "Saltando $keystore..."
        echo ""
        continue
    fi
    
    # Intentar listar aliases
    ALIASES_OUTPUT=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
    
    if echo "$ALIASES_OUTPUT" | grep -q "Alias name:"; then
        echo "✓ Contraseña correcta!"
        echo ""
        
        # Obtener lista de aliases
        ALIAS_LIST=$(echo "$ALIASES_OUTPUT" | grep "Alias name:" | awk '{print $3}')
        
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
                    
                    # Preguntar si quiere crear el archivo
                    read -p "  ¿Crear keystore.properties automáticamente? (s/n): " crear
                    if [ "$crear" = "s" ] || [ "$crear" = "S" ]; then
                        cat > keystore.properties << EOF
storeFile=$keystore
storePassword=$password
keyAlias=$alias
keyPassword=$password
EOF
                        echo "  ✓ Archivo keystore.properties creado automáticamente"
                    fi
                    echo ""
                    FOUND=true
                else
                    echo "  ❌ SHA1 no coincide"
                fi
                echo ""
            fi
        done
    else
        echo "❌ Contraseña incorrecta o error al leer el keystore"
    fi
    echo ""
done

echo "=========================================="
if [ "$FOUND" = true ]; then
    echo "✅ ¡Keystore correcto encontrado!"
    echo "   Ahora puedes generar el AAB con:"
    echo "   ./gradlew bundleRelease"
else
    echo "⚠️  No se encontró el keystore con el SHA1 correcto"
    echo ""
    echo "El SHA1 esperado es: $EXPECTED_SHA1"
    echo ""
    echo "OPCIÓN: Solicitar restablecimiento de la clave de carga"
    echo ""
    echo "Si no puedes encontrar el keystore correcto:"
    echo "1. Ve a Google Play Console"
    echo "2. Configuración → Integridad de la app → Firma de apps de Play"
    echo "3. Busca 'Cómo solicitar que se restablezca la clave de carga'"
    echo "4. Haz clic en 'Solicitar que se restablezca la clave de carga'"
    echo ""
    echo "⚠️  IMPORTANTE: Esto solo se puede hacer UNA VEZ al año."
fi
echo "=========================================="

