# Generar AAB firmado para Google Play Store

## 📋 Pasos para generar el AAB:

### 1. Crear el archivo `keystore.properties`

Crea un archivo llamado `keystore.properties` en la raíz del proyecto con el siguiente contenido:

```properties
storeFile=key.jks
storePassword=TU_CONTRASEÑA_DEL_KEYSTORE
keyAlias=TU_ALIAS
keyPassword=TU_CONTRASEÑA_DEL_ALIAS
```

**⚠️ IMPORTANTE:** 
- Reemplaza los valores con tus credenciales reales
- Este archivo NO se subirá a Git (está en .gitignore)
- Si usas `LightOnNoti.jks` en lugar de `key.jks`, cambia `storeFile=LightOnNoti.jks`

### 2. Verificar qué keystore usar

Tienes dos keystores disponibles:
- `key.jks`
- `LightOnNoti.jks`

Usa el mismo keystore que usaste para versiones anteriores de la app en Play Store.

### 3. Generar el AAB

Ejecuta el siguiente comando desde la raíz del proyecto:

```bash
./gradlew bundleRelease
```

El archivo AAB se generará en:
```
app/build/outputs/bundle/release/app-release.aab
```

### 4. Verificar el AAB

Puedes verificar que el AAB está firmado correctamente con:

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab
```

O usando el bundletool de Google:

```bash
bundletool verify --bundle=app/build/outputs/bundle/release/app-release.aab
```

## 🔐 Si no recuerdas las credenciales del keystore:

Si no recuerdas la contraseña o el alias, puedes intentar listar los alias del keystore:

```bash
keytool -list -v -keystore key.jks
```

O para LightOnNoti.jks:

```bash
keytool -list -v -keystore LightOnNoti.jks
```

Te pedirá la contraseña del keystore y mostrará todos los alias disponibles.

## 📤 Subir a Google Play Console:

1. Ve a Google Play Console
2. Selecciona tu app
3. Ve a "Producción" → "Lanzamiento" → "Crear nueva versión"
4. Sube el archivo `app-release.aab`
5. Completa la información requerida y publica

## ⚠️ Nota sobre la versión:

La versión actual configurada es:
- **versionName**: 1.7.3
- **versionCode**: 73

Asegúrate de que esta versión sea mayor que la última versión publicada en Play Store.

