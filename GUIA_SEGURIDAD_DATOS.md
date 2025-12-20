# Guía: Actualizar Declaración de Seguridad de Datos en Google Play Console

## Problema Detectado
Google Play detectó que tu aplicación transmite **"Dispositivo u otros IDs"** (Device or other IDs) pero no está declarado en la sección de Seguridad de los datos.

## ¿Por qué ocurre esto?
Tu aplicación utiliza servicios de publicidad (AdMob y Appodeal) que recopilan **Advertising IDs** (identificadores de publicidad) para mostrar anuncios personalizados. Estos IDs deben ser declarados en la sección de Seguridad de los datos.

## Datos que tu aplicación recopila y transmite

### 1. **Dispositivo u otros IDs** (Device or other IDs)
- **Tipo**: Advertising ID (ID de publicidad de Android)
- **Recopilado por**: Google AdMob y Appodeal
- **Propósito**: Mostrar anuncios personalizados
- **Compartido con**: Servicios de publicidad (AdMob, Appodeal y sus redes asociadas)
- **Opcional para el usuario**: Sí (el usuario puede desactivarlo en configuración de Android)

### 2. **Información del dispositivo** (Device information)
- **Tipo**: Modelo de dispositivo, versión de Android, características del hardware
- **Recopilado por**: Servicios de publicidad
- **Propósito**: Optimizar la visualización de anuncios
- **Compartido con**: Servicios de publicidad

### 3. **Datos de interacción con anuncios** (App interactions)
- **Tipo**: Interacciones con anuncios (clics, visualizaciones)
- **Recopilado por**: Servicios de publicidad
- **Propósito**: Analizar el rendimiento de los anuncios
- **Compartido con**: Servicios de publicidad

## Pasos para actualizar la declaración en Google Play Console

### Paso 1: Acceder a Seguridad de los datos
1. Ve a [Google Play Console](https://play.google.com/console)
2. Selecciona tu aplicación **LightOn Noti**
3. En el menú lateral, ve a **Políticas y programas** → **Seguridad de los datos**

### Paso 2: Declarar "Dispositivo u otros IDs"

1. **¿Recopilas o compartes datos?**
   - Selecciona: **Sí**

2. **Tipo de datos: "Dispositivo u otros IDs"**
   - Busca y selecciona: **Dispositivo u otros IDs**
   - Marca la casilla

3. **Detalles de "Dispositivo u otros IDs"**
   - **¿Se recopilan estos datos?**: **Sí**
   - **¿Se comparten estos datos?**: **Sí**
   - **¿Se recopilan estos datos de forma opcional?**: **Sí** (el usuario puede desactivar el Advertising ID)
   - **¿Se recopilan estos datos de forma transitoria?**: **Sí** (se procesan pero no se almacenan permanentemente por tu app)
   - **¿Se enlazan estos datos a la identidad del usuario?**: **No** (el Advertising ID no identifica personalmente al usuario)
   - **¿Se utilizan estos datos para rastrear al usuario?**: **Sí** (para personalizar anuncios)
   - **¿Se utilizan estos datos para publicidad o marketing?**: **Sí**
   - **¿Se utilizan estos datos para análisis?**: **Sí** (análisis de rendimiento de anuncios)

4. **¿Con quién se comparten estos datos?**
   - Selecciona: **Terceros**
   - Especifica: **Google AdMob** y **Appodeal**
   - **Propósito**: Publicidad y marketing

### Paso 3: Declarar "Información del dispositivo" (opcional pero recomendado)

1. **Tipo de datos: "Información del dispositivo"**
   - Busca y selecciona: **Información del dispositivo**
   - Marca la casilla

2. **Detalles de "Información del dispositivo"**
   - **¿Se recopilan estos datos?**: **Sí**
   - **¿Se comparten estos datos?**: **Sí**
   - **¿Se recopilan estos datos de forma opcional?**: **Sí**
   - **¿Se recopilan estos datos de forma transitoria?**: **Sí**
   - **¿Se enlazan estos datos a la identidad del usuario?**: **No**
   - **¿Se utilizan estos datos para rastrear al usuario?**: **Sí**
   - **¿Se utilizan estos datos para publicidad o marketing?**: **Sí**
   - **¿Se utilizan estos datos para análisis?**: **Sí**

3. **¿Con quién se comparten estos datos?**
   - Selecciona: **Terceros**
   - Especifica: **Google AdMob** y **Appodeal**

### Paso 4: Declarar "Interacciones de la app" (opcional pero recomendado)

1. **Tipo de datos: "Interacciones de la app"**
   - Busca y selecciona: **Interacciones de la app**
   - Marca la casilla

2. **Detalles de "Interacciones de la app"**
   - **¿Se recopilan estos datos?**: **Sí**
   - **¿Se comparten estos datos?**: **Sí**
   - **¿Se recopilan estos datos de forma opcional?**: **Sí**
   - **¿Se recopilan estos datos de forma transitoria?**: **Sí**
   - **¿Se enlazan estos datos a la identidad del usuario?**: **No**
   - **¿Se utilizan estos datos para rastrear al usuario?**: **Sí**
   - **¿Se utilizan estos datos para publicidad o marketing?**: **Sí**
   - **¿Se utilizan estos datos para análisis?**: **Sí**

3. **¿Con quién se comparten estos datos?**
   - Selecciona: **Terceros**
   - Especifica: **Google AdMob** y **Appodeal**

### Paso 5: Guardar y enviar para revisión

1. Revisa toda la información ingresada
2. Haz clic en **Guardar**
3. Si hay cambios pendientes, haz clic en **Enviar para revisión**

## Resumen de la declaración

### Datos que DEBES declarar:

| Tipo de Dato | Recopilado | Compartido | Propósito |
|--------------|------------|------------|-----------|
| **Dispositivo u otros IDs** | Sí | Sí (AdMob, Appodeal) | Publicidad, Marketing, Análisis |
| **Información del dispositivo** | Sí | Sí (AdMob, Appodeal) | Publicidad, Marketing, Análisis |
| **Interacciones de la app** | Sí | Sí (AdMob, Appodeal) | Publicidad, Marketing, Análisis |

### Características importantes:
- ✅ Todos los datos son **opcionales** (el usuario puede desactivar el Advertising ID)
- ✅ Todos los datos son **transitorios** (no se almacenan permanentemente por tu app)
- ✅ Los datos **NO se enlazan a la identidad del usuario** directamente
- ✅ Los datos se utilizan para **publicidad, marketing y análisis**
- ✅ Los datos se **comparten con terceros** (AdMob y Appodeal)

## Verificación

Después de actualizar la declaración, verifica que:

1. ✅ La política de privacidad menciona estos datos (ya está actualizada)
2. ✅ La declaración de seguridad de datos coincide con la política de privacidad
3. ✅ Todos los datos detectados por Google están declarados

## Notas importantes

- **Advertising ID es opcional**: Los usuarios pueden desactivar el Advertising ID en Configuración → Google → Anuncios → Restablecer ID de publicidad
- **Transitorio**: Los datos se procesan pero no se almacenan permanentemente por tu aplicación
- **No identifica personalmente**: El Advertising ID no identifica directamente al usuario, solo al dispositivo
- **Consistencia**: Asegúrate de que la declaración coincida con tu política de privacidad

## Si el problema persiste

Si después de actualizar la declaración Google sigue detectando el problema:

1. Verifica que guardaste todos los cambios
2. Espera 24-48 horas para que Google procese los cambios
3. Asegúrate de que la versión de código 88 (o la versión actual) tenga la declaración actualizada
4. Revisa que no haya otras versiones en otras pistas (beta, alpha) que también necesiten actualización

## Referencias

- [Política de privacidad actualizada](https://gastonlesbe.github.io/MiLinterna/privacy-policy.html)
- [Guía de Google sobre Seguridad de los datos](https://support.google.com/googleplay/android-developer/answer/10787469)
- [Política de privacidad de AdMob](https://policies.google.com/privacy)
- [Política de privacidad de Appodeal](https://www.appodeal.com/privacy-policy)
