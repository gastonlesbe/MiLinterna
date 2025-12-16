# Solución para Compatibilidad con 16 KB Page Size

## Problema
El APK no es compatible con dispositivos de 16 KB porque algunas librerías nativas de Appodeal no están alineadas a límites de 16 KB:
- `lib/arm64-v8a/libapplovin-native-crash-reporter.so`
- `lib/arm64-v8a/libnms.so`
- `lib/arm64-v8a/libtobEmbedPagEncrypt.so`

## Soluciones Implementadas

### 1. Configuración de NDK
Se agregó `ndkVersion "26.1.10909125"` en `build.gradle` para usar el NDK más reciente que soporta 16 KB.

### 2. Configuración de Packaging
Se configuró `packaging` con `useLegacyPackaging = false` para mejorar el manejo de librerías nativas.

## Soluciones Adicionales Recomendadas

### Opción 1: Actualizar Appodeal SDK (RECOMENDADO)
1. Verifica si hay una versión más reciente del SDK de Appodeal que soporte 16 KB:
   - Visita: https://appodeal.com/sdk/android
   - Contacta al soporte de Appodeal: support@appodeal.com
   - Pregunta específicamente sobre soporte para 16 KB page size

2. Si hay una versión más reciente, actualiza en `app/build.gradle`:
   ```gradle
   implementation("com.appodeal.ads:sdk:X.X.X") {
       // ... excludes
   }
   ```

### Opción 2: Verificar Alineación del APK
Después de construir el APK, verifica la alineación:
```bash
zipalign -c -P 16 -v 4 app/build/outputs/apk/release/app-release.apk
```

### Opción 3: Contactar a Appodeal
Si no hay una versión compatible disponible:
1. Contacta al soporte de Appodeal explicando el problema
2. Solicita una versión del SDK compatible con 16 KB page size
3. Menciona que es un requisito de Google Play a partir de Noviembre 2025

### Opción 4: Solución Temporal (NO RECOMENDADO)
Como último recurso, puedes temporalmente reducir el `targetSdk` a 34, pero esto no es una solución a largo plazo ya que Google Play requerirá 16 KB para Android 15+.

## Verificación
Para verificar si el problema está resuelto:
1. Construye el APK: `./gradlew assembleRelease`
2. Analiza el APK en Android Studio: `Build > Analyze APK...`
3. Revisa la carpeta `lib` y verifica que no haya advertencias de alineación
4. Prueba en un emulador con 16 KB page size

## Fecha Límite
Google Play requiere soporte para 16 KB a partir del **1 de Noviembre de 2025** para apps que apuntan a Android 15 (API 35).

## Recursos
- Documentación oficial: https://developer.android.com/guide/practices/page-sizes
- Herramienta de verificación: Android Studio APK Analyzer
- Contacto Appodeal: support@appodeal.com

