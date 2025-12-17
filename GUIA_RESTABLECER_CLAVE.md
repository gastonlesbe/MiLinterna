# Guía: Restablecer la Clave de Carga en Google Play Console

## ⚠️ IMPORTANTE ANTES DE COMENZAR

- **Solo puedes hacer esto UNA VEZ al año**
- Asegúrate de que realmente no puedes encontrar tu keystore original
- Después del restablecimiento, deberás crear un nuevo keystore y usarlo para todas las futuras actualizaciones

## 📋 Pasos para Solicitar el Restablecimiento

### Paso 1: Acceder a Google Play Console

1. Ve a [Google Play Console](https://play.google.com/console)
2. Inicia sesión con tu cuenta de desarrollador
3. Selecciona tu app **MiLinterna**

### Paso 2: Navegar a la Configuración de Firma

1. En el menú lateral izquierdo, haz clic en **"Configuración"** (Settings)
2. Luego haz clic en **"Integridad de la app"** (App integrity)
3. Busca la sección **"Firma de apps de Play"** (Play App Signing)

### Paso 3: Encontrar la Opción de Restablecimiento

1. En la sección **"Certificado de clave de carga"**, busca el texto:
   - **"Cómo solicitar que se restablezca la clave de carga"**
   - O en inglés: **"How to request an upload key reset"**

2. Deberías ver un enlace o botón que dice:
   - **"Solicitar que se restablezca la clave de carga"**
   - O en inglés: **"Request upload key reset"**

### Paso 4: Solicitar el Restablecimiento

1. Haz clic en **"Solicitar que se restablezca la clave de carga"**
2. Google te pedirá que confirmes la solicitud
3. Lee cuidadosamente la información que Google proporciona
4. Confirma que entiendes las implicaciones

### Paso 5: Esperar la Aprobación

- Google revisará tu solicitud
- Esto puede tomar desde unas horas hasta unos días
- Recibirás una notificación por email cuando se apruebe

### Paso 6: Crear un Nuevo Keystore

Una vez que Google apruebe el restablecimiento, deberás:

1. **Crear un nuevo keystore:**
   ```bash
   keytool -genkey -v -keystore nuevo_keystore.jks -keyalg RSA -keysize 2048 -validity 10000 -alias mi_linterna_key
   ```

2. **Crear el archivo `keystore.properties`:**
   ```properties
   storeFile=nuevo_keystore.jks
   storePassword=TU_CONTRASEÑA_DEL_KEYSTORE
   keyAlias=mi_linterna_key
   keyPassword=TU_CONTRASEÑA_DEL_ALIAS
   ```

3. **Generar el AAB:**
   ```bash
   ./gradlew bundleRelease
   ```

## 📝 Información que Google Puede Pedirte

Google puede pedirte información adicional como:
- Razón por la que necesitas restablecer la clave
- Confirmación de que eres el propietario de la app
- Información sobre cuándo fue la última vez que usaste la clave

## ✅ Después del Restablecimiento

Una vez que Google apruebe el restablecimiento:

1. **Sube el nuevo certificado de carga:**
   - Google te dará instrucciones específicas
   - Probablemente necesitarás subir el certificado público del nuevo keystore

2. **Genera y sube el nuevo AAB:**
   - Usa el nuevo keystore para firmar el AAB
   - Sube el AAB a Google Play Console

3. **Google firmará la app:**
   - Google usará su clave de firma de la app (que no cambia)
   - Los usuarios seguirán recibiendo actualizaciones normalmente

## 🔒 Seguridad

- **Guarda el nuevo keystore en un lugar seguro**
- **Haz backup del keystore**
- **Documenta la contraseña en un gestor de contraseñas seguro**
- **No subas el keystore a Git** (debe estar en `.gitignore`)

## 📞 Si Tienes Problemas

Si encuentras problemas durante el proceso:

1. Revisa la documentación oficial de Google:
   - [App Signing by Google Play](https://support.google.com/googleplay/android-developer/answer/9842756)

2. Contacta al soporte de Google Play Console:
   - Ve a Google Play Console → Ayuda → Contactar con el equipo de Play Console

## 🎯 Resumen

1. Ve a Google Play Console → Configuración → Integridad de la app → Firma de apps de Play
2. Busca "Cómo solicitar que se restablezca la clave de carga"
3. Haz clic en "Solicitar que se restablezca la clave de carga"
4. Confirma la solicitud
5. Espera la aprobación de Google
6. Crea un nuevo keystore
7. Genera y sube el nuevo AAB

¡Buena suerte! 🚀

