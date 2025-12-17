#!/bin/bash

# SHA1 esperado por Google Play
EXPECTED_SHA1="54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
EXPECTED_SHA1_CLEAN=$(echo "$EXPECTED_SHA1" | tr -d ':')

# Generar todas las variaciones posibles
WORDS=("octopus" "Octopus" "OCTOPUS" "goldfish" "Goldfish" "GOLDFISH" "hipo" "Hipo" "HIPO")
NUMBERS=("2317522" "210809" "0809")

# Crear lista de contraseñas
PASSWORDS=()

# Combinar palabras con números
for word in "${WORDS[@]}"; do
    for num in "${NUMBERS[@]}"; do
        PASSWORDS+=("${word}${num}")
        PASSWORDS+=("${word}_${num}")
        PASSWORDS+=("${word}-${num}")
    done
done

# Agregar las originales también
PASSWORDS+=("Octopus2317522" "goldfish210809" "Hipo0809")

# Eliminar duplicados
PASSWORDS=($(printf '%s\n' "${PASSWORDS[@]}" | sort -u))

# Lista de keystores
KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

echo "=========================================="
echo "Verificador con Todas las Variaciones"
echo "=========================================="
echo ""
echo "SHA1 esperado: $EXPECTED_SHA1"
echo ""
echo "Probando ${#PASSWORDS[@]} variaciones de contraseñas..."
echo ""

FOUND=false
ATTEMPTS=0

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    
    for password in "${PASSWORDS[@]}"; do
        ATTEMPTS=$((ATTEMPTS + 1))
        
        # Mostrar progreso cada 10 intentos
        if [ $((ATTEMPTS % 10)) -eq 0 ]; then
            echo "Probando... (intento $ATTEMPTS/${#PASSWORDS[@]})"
        fi
        
        # Intentar listar aliases (silenciosamente)
        ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
        
        if echo "$ALIASES" | grep -q "Alias name:"; then
            echo ""
            echo "✓ ¡Contraseña encontrada: $password"
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
                        echo "  ❌ SHA1 no coincide (esperado: $EXPECTED_SHA1)"
                    fi
                    echo ""
                fi
            done
            
            # Si encontramos una contraseña que funciona, continuar con este keystore
            # pero seguir buscando el SHA1 correcto
        fi
    done
    echo ""
done

echo "=========================================="
if [ "$FOUND" = true ]; then
    echo "✅ ¡Keystore correcto encontrado!"
    echo "   El archivo keystore.properties ha sido creado"
else
    echo "⚠️  No se encontró el keystore con el SHA1 correcto"
    echo "   Se probaron $ATTEMPTS combinaciones"
fi
echo "=========================================="

