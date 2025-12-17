#!/bin/bash

echo "=========================================="
echo "Generar Nuevo Keystore y Certificado PEM"
echo "=========================================="
echo ""
echo "Este script creará un nuevo keystore y exportará"
echo "el certificado público en formato PEM para subirlo"
echo "a Google Play Console."
echo ""

read -p "¿Deseas continuar? (s/n): " continuar
if [ "$continuar" != "s" ] && [ "$continuar" != "S" ]; then
    echo "Cancelado."
    exit 0
fi

echo ""
echo "Ingresa la información para el nuevo keystore:"
echo ""

# Nombre del keystore
read -p "Nombre del archivo keystore (ej: milinterna_upload_keystore.jks) [milinterna_upload_keystore.jks]: " KEYSTORE_NAME
if [ -z "$KEYSTORE_NAME" ]; then
    KEYSTORE_NAME="milinterna_upload_keystore.jks"
fi

# Alias
read -p "Alias de la clave (ej: upload) [upload]: " KEY_ALIAS
if [ -z "$KEY_ALIAS" ]; then
    KEY_ALIAS="upload"
fi

# Contraseña del keystore
read -sp "Contraseña del keystore: " STORE_PASSWORD
echo ""
if [ -z "$STORE_PASSWORD" ]; then
    echo "Error: La contraseña no puede estar vacía"
    exit 1
fi

# Contraseña del alias
read -sp "Contraseña del alias (puede ser la misma) [$STORE_PASSWORD]: " KEY_PASSWORD
echo ""
if [ -z "$KEY_PASSWORD" ]; then
    KEY_PASSWORD="$STORE_PASSWORD"
fi

# Información del certificado
read -p "Nombre completo (CN) [Gaston Lesbegueris]: " CN
if [ -z "$CN" ]; then
    CN="Gaston Lesbegueris"
fi

read -p "Unidad organizativa (OU) [Development]: " OU
if [ -z "$OU" ]; then
    OU="Development"
fi

read -p "Organización (O) [MiLinterna]: " O
if [ -z "$O" ]; then
    O="MiLinterna"
fi

read -p "Ciudad (L) [Buenos Aires]: " L
if [ -z "$L" ]; then
    L="Buenos Aires"
fi

read -p "Estado/Provincia (ST) [Buenos Aires]: " ST
if [ -z "$ST" ]; then
    ST="Buenos Aires"
fi

read -p "Código de país (C, ej: AR) [AR]: " C
if [ -z "$C" ]; then
    C="AR"
fi

echo ""
echo "Creando el keystore..."
echo ""

# Crear el keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_NAME" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias "$KEY_ALIAS" \
    -storepass "$STORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=$CN, OU=$OU, O=$O, L=$L, ST=$ST, C=$C"

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Error al crear el keystore"
    exit 1
fi

echo ""
echo "✅ Keystore creado exitosamente: $KEYSTORE_NAME"
echo ""

# Mostrar información del certificado
echo "Información del certificado:"
echo ""
keytool -list -v -keystore "$KEYSTORE_NAME" -storepass "$STORE_PASSWORD" -alias "$KEY_ALIAS" | grep -E "(Alias|SHA1|SHA-256|MD5|Validity)"
echo ""

# Exportar el certificado público en formato PEM
CERTIFICATE_FILE="${KEYSTORE_NAME%.jks}_certificate.pem"
echo "Exportando certificado público a: $CERTIFICATE_FILE"
echo ""

keytool -export -rfc \
    -keystore "$KEYSTORE_NAME" \
    -alias "$KEY_ALIAS" \
    -file "$CERTIFICATE_FILE" \
    -storepass "$STORE_PASSWORD"

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Error al exportar el certificado"
    exit 1
fi

echo "✅ Certificado público exportado: $CERTIFICATE_FILE"
echo ""

# Crear el archivo keystore.properties
cat > keystore.properties << EOF
storeFile=$KEYSTORE_NAME
storePassword=$STORE_PASSWORD
keyAlias=$KEY_ALIAS
keyPassword=$KEY_PASSWORD
EOF

echo "✅ Archivo keystore.properties creado"
echo ""

echo "=========================================="
echo "✅ Proceso completado exitosamente"
echo "=========================================="
echo ""
echo "Archivos creados:"
echo "  1. $KEYSTORE_NAME (keystore - GUARDAR EN LUGAR SEGURO)"
echo "  2. $CERTIFICATE_FILE (certificado público - SUBIR A GOOGLE PLAY)"
echo "  3. keystore.properties (configuración para Gradle)"
echo ""
echo "=========================================="
echo "Próximos pasos:"
echo "=========================================="
echo ""
echo "1. En Google Play Console:"
echo "   - Selecciona el motivo del restablecimiento"
echo "   - Sube el archivo: $CERTIFICATE_FILE"
echo "   - Confirma la solicitud"
echo ""
echo "2. Espera la aprobación de Google (puede tardar horas o días)"
echo ""
echo "3. Una vez aprobado, genera el AAB:"
echo "   ./gradlew bundleRelease"
echo ""
echo "4. Sube el AAB a Google Play Console"
echo ""
echo "⚠️  IMPORTANTE:"
echo "  - Guarda el keystore ($KEYSTORE_NAME) en un lugar seguro"
echo "  - Haz backup del keystore"
echo "  - Documenta la contraseña en un gestor de contraseñas"
echo "  - NO subas el keystore a Git"
echo "=========================================="

