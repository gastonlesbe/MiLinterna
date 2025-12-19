#!/bin/bash

# Huellas digitales esperadas por Google Play Console
EXPECTED_SHA1="8A:EF:5D:8E:2D:89:4C:88:59:2A:2C:0B:4E:DF:1A:BE:BA:12:4A:49"
EXPECTED_SHA256="AF:A1:74:D3:C7:B6:C6:77:36:08:0C:94:D5:80:21:3F:E1:07:43:5A:E5:97:7B:45:11:99:26:08:53:0C:0E:6D"
EXPECTED_MD5="6D:DF:F5:67:4B:AB:63:D6:CC:BB:8B:85:9F:E4:BD:D2"

EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')
EXPECTED_SHA256_CLEAN=$(echo "$EXPECTED_SHA256" | tr -d ':')
EXPECTED_MD5_CLEAN=$(echo "$EXPECTED_MD5" | tr -d ':')

echo "=========================================="
echo "Verificador de Keystore para Play Store"
echo "=========================================="
echo ""
echo "Huellas digitales esperadas por Play Console:"
echo "  SHA-1:   $EXPECTED_SHA1"
echo "  SHA-256: $EXPECTED_SHA256"
echo "  MD5:     $EXPECTED_MD5"
echo ""
echo "Buscando en todos los keystores..."
echo ""

# Lista de keystores a verificar
KEYSTORES=("milinterna_upload_keystore.jks" "key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

FOUND=false

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    echo ""
    
    # Si es milinterna_upload_keystore.jks, usar la contraseña del keystore.properties
    if [ "$keystore" = "milinterna_upload_keystore.jks" ]; then
        if [ -f "keystore.properties" ]; then
            STORE_PASS=$(grep storePassword keystore.properties | cut -d'=' -f2)
            KEY_ALIAS=$(grep keyAlias keystore.properties | cut -d'=' -f2)
        else
            echo "⚠️  No se encontró keystore.properties, saltando..."
            echo ""
            continue
        fi
    else
        echo "Necesito la contraseña para verificar este keystore."
        read -sp "Contraseña para $keystore (o Enter para saltar): " STORE_PASS
        echo ""
        echo ""
        
        if [ -z "$STORE_PASS" ]; then
            echo "Saltando $keystore..."
            echo ""
            continue
        fi
        
        # Listar aliases
        ALIASES_OUTPUT=$(keytool -list -keystore "$keystore" -storepass "$STORE_PASS" 2>&1)
        if ! echo "$ALIASES_OUTPUT" | grep -q "Alias name:"; then
            echo "❌ Contraseña incorrecta"
            echo ""
            continue
        fi
        
        KEY_ALIAS=$(echo "$ALIASES_OUTPUT" | grep "Alias name:" | head -1 | awk '{print $3}')
        if [ -z "$KEY_ALIAS" ]; then
            echo "⚠️  No se encontró alias, intentando sin alias..."
            KEY_ALIAS=""
        fi
    fi
    
    echo "Verificando alias: ${KEY_ALIAS:-(todos los aliases)}"
    echo ""
    
    # Obtener todas las huellas digitales
    if [ -n "$KEY_ALIAS" ]; then
        CERT_INFO=$(keytool -list -v -keystore "$keystore" -alias "$KEY_ALIAS" -storepass "$STORE_PASS" 2>&1)
    else
        CERT_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$STORE_PASS" 2>&1)
    fi
    
    SHA1=$(echo "$CERT_INFO" | grep -i "SHA1:" | head -1 | awk '{print $2}' | tr -d ':')
    SHA256=$(echo "$CERT_INFO" | grep -i "SHA256:" | head -1 | awk '{print $2}' | tr -d ':')
    MD5=$(echo "$CERT_INFO" | grep -i "MD5:" | head -1 | awk '{print $2}' | tr -d ':')
    
    if [ -n "$SHA1" ]; then
        SHA1_FORMATTED=$(echo "$SHA1" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
        SHA256_FORMATTED=$(echo "$SHA256" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
        MD5_FORMATTED=$(echo "$MD5" | sed 's/\(..\)/\1:/g' | sed 's/:$//')
        
        echo "  Alias: ${KEY_ALIAS:-N/A}"
        echo "  SHA-1:   $SHA1_FORMATTED"
        echo "  SHA-256: $SHA256_FORMATTED"
        echo "  MD5:     $MD5_FORMATTED"
        echo ""
        
        # Verificar si coincide
        SHA1_MATCH=false
        SHA256_MATCH=false
        MD5_MATCH=false
        
        if [ "$SHA1" = "$EXPECTED_SHA1_CLEAN" ]; then
            SHA1_MATCH=true
        fi
        if [ "$SHA256" = "$EXPECTED_SHA256_CLEAN" ]; then
            SHA256_MATCH=true
        fi
        if [ "$MD5" = "$EXPECTED_MD5_CLEAN" ]; then
            MD5_MATCH=true
        fi
        
        if [ "$SHA1_MATCH" = true ] && [ "$SHA256_MATCH" = true ]; then
            echo "  ✅ ✅ ✅ ¡ENCONTRADO! Este es el keystore CORRECTO ✅ ✅ ✅"
            echo ""
            echo "  Configuración para keystore.properties:"
            echo "  storeFile=$keystore"
            echo "  storePassword=$STORE_PASS"
            echo "  keyAlias=$KEY_ALIAS"
            echo "  keyPassword=$STORE_PASS"
            echo ""
            
            # Preguntar si quiere crear el archivo
            read -p "  ¿Actualizar keystore.properties automáticamente? (s/n): " crear
            if [ "$crear" = "s" ] || [ "$crear" = "S" ]; then
                cat > keystore.properties << EOF
storeFile=$keystore
storePassword=$STORE_PASS
keyAlias=$KEY_ALIAS
keyPassword=$STORE_PASS
EOF
                echo "  ✓ Archivo keystore.properties actualizado"
            fi
            echo ""
            FOUND=true
        else
            echo "  ❌ No coincide con las huellas esperadas"
            if [ "$SHA1_MATCH" = true ]; then
                echo "     (SHA-1 coincide pero SHA-256 no)"
            elif [ "$SHA256_MATCH" = true ]; then
                echo "     (SHA-256 coincide pero SHA-1 no)"
            fi
        fi
    else
        echo "  ⚠️  No se pudieron obtener las huellas digitales"
    fi
    echo ""
done

echo "=========================================="
if [ "$FOUND" = true ]; then
    echo "✅ ¡Keystore correcto encontrado!"
    echo "   Ahora puedes generar el AAB con:"
    echo "   ./gradlew bundleRelease"
else
    echo "⚠️  No se encontró el keystore con las huellas correctas"
    echo ""
    echo "Las huellas esperadas son:"
    echo "  SHA-1:   $EXPECTED_SHA1"
    echo "  SHA-256: $EXPECTED_SHA256"
    echo ""
    echo "Si creaste un nuevo keystore después del restablecimiento,"
    echo "asegúrate de haber subido el certificado público a Play Console."
fi
echo "=========================================="
