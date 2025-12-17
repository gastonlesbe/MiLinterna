# Instrucciones para Encontrar el Keystore Correcto

## Situación Actual

Tu app usa **"Firma de apps de Play"** (App Signing by Google Play), lo que significa que hay dos claves:

1. **Upload Key (Upload Keystore)**: La clave que usas para firmar el AAB antes de subirlo
2. **App Signing Key**: La clave que Google Play usa para firmar la versión final (administrada por Google)

El SHA1 que Google Play espera es: `54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD`

Este SHA1 corresponde a tu **Upload Key**.

## Pasos para Encontrar el Keystore Correcto

### 1. Verificar en Google Play Console

1. Ve a **Google Play Console**
2. Selecciona tu app **MiLinterna**
3. Ve a **Configuración** → **Integridad de la app** → **Firma de apps de Play**
4. Busca la sección **"Certificado de carga"** o **"Upload certificate"**
5. Ahí deberías ver el SHA1 del certificado de carga registrado

### 2. Verificar el SHA1 del AAB que Subiste

El AAB que subiste tiene SHA1: `62:B6:6F:1D:92:87:BF:4C:76:8A:3C:D0:24:39:90:C3:11:15:05:34`

Este SHA1 **NO coincide** con el esperado, lo que significa que usaste un keystore diferente.

### 3. Buscar el Keystore Correcto

El keystore correcto debe tener el SHA1: `54:D8:F1:41:64:71:8A:16:63:B5:43:E1:A9:07:A9:B8:91:BD:1F:AD`

**Opciones:**

1. **Buscar en otros proyectos:**
   - Revisa otros proyectos Android que tengas
   - Busca archivos `.jks` o `.keystore` en todo tu sistema

2. **Buscar en backups:**
   - Revisa backups antiguos del proyecto
   - Busca en discos externos o servicios de backup

3. **Buscar en documentación:**
   - Revisa notas o documentación del proyecto
   - Busca en gestores de contraseñas

4. **Usar el script interactivo:**
   ```bash
   ./verificar_manual.sh
   ```
   Te permite probar contraseñas manualmente

### 4. Si No Encuentras el Keystore

Si realmente no puedes encontrar el keystore correcto:

1. **Contacta a Google Play Support:**
   - Ve a Google Play Console → Ayuda → Contactar con el equipo de Play Console
   - Explica que perdiste tu upload keystore
   - Google puede ayudarte a resetear la upload key

2. **Último recurso:**
   - Crear una nueva app en Play Store (pierdes descargas y reviews)

## Scripts Disponibles

- `./verificar_manual.sh` - Script interactivo para probar contraseñas manualmente
- `./verificar_todas_claves.sh` - Prueba todas las contraseñas del archivo encontrado

## Contraseñas Probadas (sin éxito)

- Hipo0809
- Octopus2317522
- goldfish210809
- gsl23175
- glechu23175
- .Fil211114, Fil211114
- til210809, .til210809, Til210809, .Til210809
- Fede211114
- Til211114, .Til211114
- Til21082009
- Y todas sus variaciones

