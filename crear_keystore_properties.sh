#!/bin/bash
echo "Creando archivo keystore.properties..."
echo ""
read -sp "Contraseña del keystore key.jks: " STORE_PASS
echo ""
read -p "Alias de la clave: " KEY_ALIAS
read -sp "Contraseña del alias: " KEY_PASS
echo ""

cat > keystore.properties << EOF
storeFile=key.jks
storePassword=$STORE_PASS
keyAlias=$KEY_ALIAS
keyPassword=$KEY_PASS
EOF

echo "✓ Archivo keystore.properties creado"
echo "Ahora puedes ejecutar: ./gradlew bundleRelease"
