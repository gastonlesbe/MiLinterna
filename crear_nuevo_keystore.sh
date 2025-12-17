#!/bin/bash

echo "=========================================="
echo "Crear Nuevo Keystore para MiLinterna"
echo "=========================================="
echo ""
echo "Este script te ayudará a crear un nuevo keystore"
echo "después de que Google apruebe el restablecimiento"
echo "de la clave de carga."
echo ""
echo "⚠️  IMPORTANTE:"
echo "- Guarda el keystore en un lugar seguro"
echo "- Haz backup del keystore"
echo "- Documenta la contraseña en un gestor de contraseñas"
echo "- NO subas el keystore a Git"
echo ""

read -p "¿Deseas continuar? (s/n): " continuar
if [ "$continuar" != "s" ] && [ "$continuar" != "S" ]; then
    echo "Cancelado."
    exit 0
fi

echo ""
echo "Ingresa la información para el nuevo keystore:"
echo ""

read -p "Nombre del archivo keystore (ej: milinterna_keystore.jks): " KEYSTORE_NAME
if [ -z "$KEYSTORE_NAME" ]; then
    KEYSTORE_NAME="milinterna_keystore.jks"
fi

read -p "Alias de la clave (ej: milinterna_key): " KEY_ALIAS
if [ -z "$KEY_ALIAS" ]; then
    KEY_ALIAS="milinterna_key"
fi

read -sp "Contraseña del keystore: " STORE_PASSWORD
echo ""
if [ -z "$STORE_PASSWORD" ]; then
    echo "Error: La contraseña no puede estar vacía"
    exit 1
fi

read -sp "Contraseña del alias (puede ser la misma): " KEY_PASSWORD
echo ""
if [ -z "$KEY_PASSWORD" ]; then
    KEY_PASSWORD="$STORE_PASSWORD"
fi

read -p "Nombre completo (CN): " CN
if [ -z "$CN" ]; then
    CN="Gaston Lesbegueris"
fi

read -p "Unidad organizativa (OU): " OU
if [ -z "$OU" ]; then
    OU="Development"
fi

read -p "Organización (O): " O
if [ -z "$O" ]; then
    O="MiLinterna"
fi

read -p "Ciudad (L): " L
if [ -z "$L" ]; then
    L="Buenos Aires"
fi

read -p "Estado/Provincia (ST): " ST
if [ -z "$ST" ]; then
    ST="Buenos Aires"
fi

read -p "Código de país (C, ej: AR): " C
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

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Keystore creado exitosamente: $KEYSTORE_NAME"
    echo ""
    
    # Mostrar información del certificado
    echo "Información del certificado:"
    echo ""
    keytool -list -v -keystore "$KEYSTORE_NAME" -storepass "$STORE_PASSWORD" -alias "$KEY_ALIAS" | grep -E "(Alias|SHA1|SHA-256|MD5|Validity)"
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
    echo "Próximos pasos:"
    echo "=========================================="
    echo ""
    echo "1. Espera la aprobación de Google para el restablecimiento"
    echo "2. Cuando Google te lo indique, sube el certificado público:"
    echo "   keytool -export -rfc -keystore $KEYSTORE_NAME -alias $KEY_ALIAS -file certificado_publico.pem"
    echo ""
    echo "3. Genera el AAB:"
    echo "   ./gradlew bundleRelease"
    echo ""
    echo "4. Sube el AAB a Google Play Console"
    echo ""
    echo "⚠️  IMPORTANTE: Guarda el keystore y las contraseñas en un lugar seguro!"
    echo "=========================================="
else
    echo ""
    echo "❌ Error al crear el keystore"
    exit 1
fi

