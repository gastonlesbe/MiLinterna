# Guía: Declaración de Seguridad de Datos en Google Play Console

## Problema Identificado
Google Play detectó que tu aplicación está transmitiendo **"Dispositivo u otros IDs"** (Device or other IDs) fuera del dispositivo, pero esto no está declarado en la sección de Seguridad de los datos.

Esto ocurre porque tu aplicación usa **Appodeal** (una plataforma de mediación de anuncios), que recopila el **Advertising ID** de Android. Appodeal utiliza múltiples redes publicitarias, incluyendo Google AdMob, para mostrar anuncios.

## Solución: Actualizar la Declaración de Seguridad de Datos

### Paso 1: Acceder a la Sección de Seguridad de Datos

1. Ve a **Google Play Console**: https://play.google.com/console
2. Selecciona tu aplicación **LightOn Noti**
3. En el menú lateral izquierdo, ve a: **Políticas y programas** → **Seguridad de los datos**
4. Haz clic en **"Empezar"** o **"Editar"** si ya tienes una declaración

### Paso 2: Declarar los Datos Recopilados

Necesitas declarar los siguientes datos que tu aplicación recopila y comparte:

#### 2.1. Dispositivo u otros IDs (Device or other IDs)

**¿Qué datos recopila tu app?**
- ✅ **SÍ** - Tu app recopila o comparte este tipo de datos

**¿Qué datos específicos?**
- ✅ **ID de publicidad** (Advertising ID)

**¿Por qué recopilas estos datos?**
- ✅ **Publicidad o marketing**
- ✅ **Análisis**

**¿Cómo se usan estos datos?**
- ✅ **Se comparten con terceros** (Appodeal y sus redes publicitarias asociadas, incluyendo Google AdMob)

**¿Se encriptan estos datos en tránsito?**
- ✅ **Sí** (los datos se transmiten por HTTPS)

**¿Se puede requerir que los usuarios proporcionen estos datos?**
- ❌ **No** (el Advertising ID se genera automáticamente por el sistema)

**¿Se puede desactivar la recopilación de estos datos?**
- ✅ **Sí** (el usuario puede desactivar la personalización de anuncios en la configuración de Android)

### Paso 3: Declarar Información del Dispositivo (Opcional pero Recomendado)

También deberías declarar:

**¿Qué datos recopila tu app?**
- ✅ **SÍ** - Información del dispositivo

**¿Qué datos específicos?**
- ✅ **Modelo del dispositivo**
- ✅ **Versión del sistema operativo**

**¿Por qué recopilas estos datos?**
- ✅ **Publicidad o marketing**
- ✅ **Análisis**

**¿Cómo se usan estos datos?**
- ✅ **Se comparten con terceros** (Appodeal y sus redes publicitarias asociadas, incluyendo Google AdMob)

### Paso 4: Declarar Datos de Interacción con Anuncios (Opcional)

**¿Qué datos recopila tu app?**
- ✅ **SÍ** - Datos de interacción con anuncios

**¿Qué datos específicos?**
- ✅ **Visualizaciones de anuncios**
- ✅ **Clics en anuncios**

**¿Por qué recopilas estos datos?**
- ✅ **Publicidad o marketing**
- ✅ **Análisis**

**¿Cómo se usan estos datos?**
- ✅ **Se comparten con terceros** (Appodeal y sus redes publicitarias asociadas, incluyendo Google AdMob)

### Paso 5: Declarar Servicios de Terceros

En la sección de **"Servicios de terceros"** o **"Compartir datos"**, declara:

- **Appodeal** - Plataforma de mediación de anuncios (que utiliza múltiples redes publicitarias, incluyendo Google AdMob)

### Paso 6: Verificar Consistencia

Asegúrate de que la declaración coincida con tu política de privacidad:

✅ **Política de privacidad URL**: `https://gastonlesbe.github.io/MiLinterna/privacy-policy.html`

✅ **Nombre de la aplicación**: LightOn Noti

✅ **Desarrollador**: gaston lesbegueris

### Paso 7: Guardar y Enviar

1. Revisa toda la información
2. Haz clic en **"Guardar"** o **"Enviar para revisión"**
3. Espera la aprobación de Google (puede tardar algunas horas)

## Resumen de lo que debes declarar:

| Tipo de Dato | Recopilado | Compartido | Propósito |
|--------------|------------|------------|-----------|
| **ID de publicidad (Advertising ID)** | ✅ Sí | ✅ Sí (Appodeal y redes asociadas) | Publicidad, Análisis |
| **Información del dispositivo** | ✅ Sí | ✅ Sí (Appodeal y redes asociadas) | Publicidad, Análisis |
| **Datos de interacción con anuncios** | ✅ Sí | ✅ Sí (Appodeal y redes asociadas) | Publicidad, Análisis |

## Notas Importantes:

1. **NO declares datos que NO recopilas**: No declares ubicación, contactos, fotos, etc., ya que tu app NO los recopila.

2. **Sé específico**: Declara exactamente lo que recopilas (Advertising ID, no "todos los IDs del dispositivo").

3. **Consistencia**: La declaración debe coincidir con tu política de privacidad.

4. **Actualización**: Si cambias los SDKs de publicidad en el futuro, actualiza esta declaración.

## Nota Técnica

Tu aplicación utiliza **solo Appodeal** como SDK de publicidad. Appodeal es una plataforma de mediación que internamente utiliza múltiples redes publicitarias (incluyendo Google AdMob) para mostrar anuncios. Por lo tanto, aunque técnicamente los datos se comparten con AdMob, esto ocurre a través de Appodeal, no directamente. En la declaración de seguridad de datos, puedes mencionar principalmente Appodeal como el servicio principal, y opcionalmente mencionar que Appodeal utiliza redes asociadas como Google AdMob.

## Si tienes dudas:

- Revisa la documentación oficial: https://support.google.com/googleplay/android-developer/answer/10787469
- Tu política de privacidad ya está actualizada y lista en: `https://gastonlesbe.github.io/MiLinterna/privacy-policy.html`

## Después de actualizar:

Una vez que actualices la declaración y Google la apruebe, el problema de "Invalid Data safety declaration" debería resolverse.
