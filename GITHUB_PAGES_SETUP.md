# Configuración de GitHub Pages para app-ads.txt

## ¿Qué es esto?

GitHub Pages te permite alojar archivos estáticos (como `app-ads.txt`) de forma gratuita y con HTTPS, que es lo que necesitas para cumplir con los requisitos de Google Play.

## Pasos para configurar GitHub Pages:

### 1. Crear la carpeta `docs/` (ya está creada)
   - El archivo `app-ads.txt` ya está en `/docs/app-ads.txt`

### 2. Habilitar GitHub Pages en tu repositorio:

   1. Ve a tu repositorio en GitHub: https://github.com/gastonlesbe/MiLinterna
   2. Haz clic en **Settings** (Configuración)
   3. En el menú lateral, busca **Pages** (páginas)
   4. En **Source** (Fuente), selecciona:
      - **Branch**: `main` (o `master`)
      - **Folder**: `/docs`
   5. Haz clic en **Save** (Guardar)

### 3. Esperar a que GitHub publique tu sitio
   - GitHub te dará una URL como: `https://gastonlesbe.github.io/MiLinterna/`
   - Esto puede tardar unos minutos

### 4. Verificar que funciona
   - Una vez publicado, deberías poder acceder a:
     `https://gastonlesbe.github.io/MiLinterna/app-ads.txt`
   - Abre esa URL en tu navegador para verificar que el archivo se muestra correctamente

### 5. Configurar en Google Play Console

   En Google Play Console, necesitarás proporcionar la URL base de tu dominio:
   - **URL base**: `https://gastonlesbe.github.io/MiLinterna`
   - Google buscará automáticamente: `https://gastonlesbe.github.io/MiLinterna/app-ads.txt`

## Nota importante:

Si tu repositorio es **privado**, GitHub Pages **NO funcionará** con el plan gratuito. Necesitarías:
- Hacer el repositorio público, O
- Usar GitHub Pro (de pago), O
- Usar otro servicio de hosting gratuito (como Netlify, Vercel, etc.)

## Alternativa si el repositorio es privado:

Puedes usar servicios gratuitos como:
- **Netlify**: https://www.netlify.com (arrastra y suelta la carpeta `docs/`)
- **Vercel**: https://vercel.com
- **Firebase Hosting**: https://firebase.google.com/docs/hosting

## Verificación final:

Una vez configurado, verifica que el archivo es accesible públicamente:
```bash
curl https://gastonlesbe.github.io/MiLinterna/app-ads.txt
```

Si devuelve el contenido del archivo, ¡está funcionando correctamente!

