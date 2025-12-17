#!/bin/bash

echo "=========================================="
echo "Mostrar SHA1 de todos los keystores"
echo "=========================================="
echo ""
echo "Este script te pedirá la contraseña de cada keystore"
echo "y mostrará el SHA1 para que puedas identificar cuál"
echo "tiene el SHA1 correcto que Google Play espera."
echo ""
echo "SHA1 esperado por Google Play:"
echo "54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD"
echo ""
echo "SHA1 del AAB que subiste (incorrecto):"
echo "62:B6:6F:1D:92:87:BF:4C:76:8A:3C:D0:24:39:90:C3:11:15:05:34"
echo ""

KEYSTORES=("key.jks" "LightOnNoti.jks" "LightOnNoti1.jks")

for keystore in "${KEYSTORES[@]}"; do
    if [ ! -f "$keystore" ]; then
        continue
    fi
    
    echo "=========================================="
    echo "Keystore: $keystore"
    echo "=========================================="
    echo ""
    
    # Intentar con las contraseñas conocidas primero
    PASSWORDS=("Octopus2317522" "goldfish210809")
    PASSWORD_FOUND=false
    
    for password in "${PASSWORDS[@]}"; do
        ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
        if echo "$ALIASES" | grep -q "Alias name:"; then
            echo "✓ Contraseña encontrada: $password"
            PASSWORD_FOUND=true
            break
        fi
    done
    
    if [ "$PASSWORD_FOUND" = false ]; then
        read -sp "Ingresa la contraseña para $keystore (o presiona Enter para saltar): " password
        echo ""
        if [ -z "$password" ]; then
            echo "Saltando $keystore..."
            echo ""
            continue
        fi
    fi
    
    # Verificar que la contraseña funciona
    ALIASES=$(keytool -list -keystore "$keystore" -storepass "$password" 2>&1)
    if ! echo "$ALIASES" | grep -q "Alias name:"; then
        echo "❌ Contraseña incorrecta para $keystore"
        echo ""
        continue
    fi
    
    # Obtener lista de aliases
    ALIAS_LIST=$(echo "$ALIASES" | grep "Alias name:" | awk '{print $3}')
    
    echo "Aliases encontrados:"
    echo "$ALIAS_LIST" | while read alias; do
        echo "  - $alias"
    done
    echo ""
    
    # Mostrar SHA1 de cada alias
    echo "SHA1 de cada alias:"
    echo ""
    echo "$ALIAS_LIST" | while read alias; do
        SHA1_INFO=$(keytool -list -v -keystore "$keystore" -storepass "$password" -alias "$alias" 2>&1)
        SHA1=$(echo "$SHA1_INFO" | grep -i "SHA1:" | head -1 | awk '{print $2}')
        
        if [ -n "$SHA1" ]; then
            echo "  Alias: $alias"
            echo "  SHA1:  $SHA1"
            
            # Comparar con el esperado
            SHA1_CLEAN=$(echo "$SHA1" | tr -d ':')
            EXPECTED_CLEAN="54D8F14164718A1663B543E1A907A9B891BD1FAD"
            INCORRECT_CLEAN="62B66F1D9287BF4C768A3CD0243990C311150534"
            
            if [ "$SHA1_CLEAN" = "$EXPECTED_CLEAN" ]; then
                echo "  ✅ ✅ ✅ ¡ESTE ES EL CORRECTO! ✅ ✅ ✅"
            elif [ "$SHA1_CLEAN" = "$INCORRECT_CLEAN" ]; then
                echo "  ⚠️  Este es el que usaste (incorrecto)"
            else
                echo "  ❌ No coincide con ninguno conocido"
            fi
            echo ""
        fi
    done
    echo ""
done

echo "=========================================="
echo "Verificación completada"
echo "=========================================="

