# Cómo verificar y configurar GitHub Pages

## ✅ El archivo ya está en GitHub

El archivo `docs/app-ads.txt` ya está en la rama `master` de tu repositorio.

## 🔧 Pasos para habilitar GitHub Pages:

### 1. Ve a la configuración de GitHub Pages:
   - URL directa: https://github.com/gastonlesbe/MiLinterna/settings/pages
   - O manualmente:
     - Ve a tu repositorio: https://github.com/gastonlesbe/MiLinterna
     - Haz clic en **Settings** (Configuración)
     - En el menú lateral izquierdo, busca y haz clic en **Pages**

### 2. Configura la fuente:
   - En la sección **Source** (Fuente):
     - **Branch**: Selecciona `master`
     - **Folder**: Selecciona `/docs`
   - Haz clic en **Save** (Guardar)

### 3. Espera a que GitHub publique:
   - Puede tardar **1-5 minutos** en publicar
   - Verás un mensaje verde que dice: "Your site is live at..."
   - La URL será: `https://gastonlesbe.github.io/MiLinterna/`

### 4. Verifica que funciona:
   - Abre en tu navegador: `https://gastonlesbe.github.io/MiLinterna/app-ads.txt`
   - Deberías ver el contenido del archivo

## ⚠️ Posibles problemas:

### Si sigue dando 404 después de 5 minutos:

1. **Verifica que el repositorio NO sea privado:**
   - GitHub Pages gratuito NO funciona con repositorios privados
   - Si es privado, necesitas hacerlo público o usar GitHub Pro

2. **Verifica la URL correcta:**
   - La URL debe ser exactamente: `https://gastonlesbe.github.io/MiLinterna/app-ads.txt`
   - Nota: Es `.github.io` (no `.github.com`)

3. **Verifica que el archivo esté en la rama correcta:**
   - El archivo debe estar en la rama `master` (ya está ✅)
   - Debe estar en la carpeta `docs/` (ya está ✅)

4. **Limpia la caché del navegador:**
   - Presiona `Ctrl + Shift + R` (o `Cmd + Shift + R` en Mac) para recargar sin caché

## 🔍 Verificación rápida:

Puedes verificar si el archivo está accesible con este comando:

```bash
curl https://gastonlesbe.github.io/MiLinterna/app-ads.txt
```

Si devuelve el contenido del archivo, está funcionando. Si da 404, GitHub Pages aún no está configurado o el repositorio es privado.

## 📝 Para Google Play Console:

Una vez que funcione, en Google Play Console proporciona:
- **URL base del dominio**: `https://gastonlesbe.github.io/MiLinterna`
- Google buscará automáticamente: `https://gastonlesbe.github.io/MiLinterna/app-ads.txt`

