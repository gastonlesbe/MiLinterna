# Proceso Completo: Restablecer Clave de Carga y Generar AAB

## 📋 Estado Actual

- **Certificado de clave de carga actual (SHA-1)**: `54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD`
- **Estado**: Necesitas restablecer la clave de carga porque no encuentras el keystore original

## 🔄 Paso 1: Solicitar Restablecimiento (AHORA)

1. En Google Play Console, haz clic en **"Cómo solicitar que se restablezca la clave de carga"**
2. Sigue las instrucciones que aparezcan
3. Confirma la solicitud
4. Espera la aprobación de Google (puede tardar horas o días)

## ⏳ Paso 2: Esperar Aprobación

- Google revisará tu solicitud
- Recibirás un email cuando se apruebe
- Una vez aprobado, Google te dará instrucciones específicas

## 🔑 Paso 3: Crear Nuevo Keystore (Después de Aprobación)

Una vez que Google apruebe el restablecimiento:

### Opción A: Usar el Script Automático

```bash
./crear_nuevo_keystore.sh
```

El script te pedirá:
- Nombre del archivo keystore
- Alias de la clave
- Contraseñas
- Información del certificado

### Opción B: Crear Manualmente

```bash
keytool -genkey -v \
    -keystore milinterna_keystore.jks \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -alias milinterna_key \
    -storepass TU_CONTRASEÑA \
    -keypass TU_CONTRASEÑA
```

## 📤 Paso 4: Subir el Nuevo Certificado a Google

Google te pedirá que subas el certificado público del nuevo keystore:

```bash
keytool -export -rfc \
    -keystore milinterna_keystore.jks \
    -alias milinterna_key \
    -file certificado_publico.pem \
    -storepass TU_CONTRASEÑA
```

Luego sube el archivo `certificado_publico.pem` a Google Play Console según las instrucciones.

## 📝 Paso 5: Crear keystore.properties

Crea el archivo `keystore.properties` en la raíz del proyecto:

```properties
storeFile=milinterna_keystore.jks
storePassword=TU_CONTRASEÑA_DEL_KEYSTORE
keyAlias=milinterna_key
keyPassword=TU_CONTRASEÑA_DEL_ALIAS
```

⚠️ **IMPORTANTE**: Este archivo NO debe subirse a Git (ya está en `.gitignore`)

## 🏗️ Paso 6: Generar el AAB

```bash
./gradlew clean bundleRelease
```

El AAB se generará en:
```
app/build/outputs/bundle/release/app-release.aab
```

## 📤 Paso 7: Subir el AAB a Google Play

1. Ve a Google Play Console
2. Ve a **"Producción"** → **"Lanzamiento"** → **"Crear nueva versión"**
3. Sube el archivo `app-release.aab`
4. Completa la información requerida
5. Publica la actualización

## ✅ Verificación

Después de subir el AAB, verifica que:
- El SHA1 del nuevo certificado de carga aparezca en Google Play Console
- El AAB se acepte sin errores
- La app se publique correctamente

## 🔒 Seguridad

- **Guarda el nuevo keystore en un lugar seguro**
- **Haz backup del keystore**
- **Documenta la contraseña en un gestor de contraseñas**
- **NO subas el keystore a Git**

## 📞 Si Tienes Problemas

Si encuentras problemas:
1. Revisa la documentación oficial de Google
2. Contacta al soporte de Google Play Console
3. Verifica que el keystore.properties esté configurado correctamente

## 🎯 Resumen Rápido

1. ✅ Solicitar restablecimiento (AHORA)
2. ⏳ Esperar aprobación
3. 🔑 Crear nuevo keystore (`./crear_nuevo_keystore.sh`)
4. 📤 Subir certificado público a Google
5. 📝 Crear `keystore.properties`
6. 🏗️ Generar AAB (`./gradlew bundleRelease`)
7. 📤 Subir AAB a Google Play

¡Buena suerte! 🚀

