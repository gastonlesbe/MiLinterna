package lesbegueris.gaston.com.milinterna;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Notification.Builder;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static lesbegueris.gaston.com.milinterna.NotificationLight.CHANNEL_ID;

// Your other imports
import lesbegueris.gaston.com.milinterna.util.AppodealHelper;
import lesbegueris.gaston.com.milinterna.util.AdMobHelper;


/**
 * Created by gaston on 24/06/17.
 */
@SuppressWarnings("deprecation")
public class LightActivity extends AppCompatActivity {

    ImageButton btnFlash, btnDimer, btnShake, btnNoti;
    ImageButton ibtnShare, ibtnRate, ibtnEmail, ibtnHelp, ibtnMenu, imagebutton;
    private CameraManager mCameraManager;
    private String mCameraId;
    boolean isFlash = false;
    boolean isOn = false;
    boolean isNotiOn;
    boolean shakeOn;
    boolean restoredText = false;
    private Boolean isTorchOn;


    private int counter = 0;
    private int camId = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "channel_id";

    public static final String MyPREFERENCES = "miNoti";
    SharedPreferences sharedpreferences;

    String appIntro;

    private static final String SHOWCASE_ID = "sequence example";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // =====================================================================
        // AdMob removido - Appodeal ya incluye AdMob en su mediation
        // =====================================================================

        // =====================================================================
        // --- ADMOB INITIALIZATION FIRST (for interstitials and startup ad) ---
        AdMobHelper.initialize(this);
        // =====================================================================
        
        // =====================================================================
        // --- APPODEAL INITIALIZATION (for banners) - AFTER AdMob ---
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        // Delay banner to ensure AdMob initializes first
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AppodealHelper.showBanner(LightActivity.this, R.id.appodealBannerView);
            }
        }, 500);
        // =====================================================================

        isTorchOn = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            //If authorisation not granted for camera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                //ask for authorisation
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 50);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCameraId = mCameraManager.getCameraIdList()[0];
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


        isFlash = true;

        SharedPreferences wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        // TEMPORAL: Para testing, puedes cambiar esto a true para forzar el primer run
        // boolean forceFirstRun = true; // Descomenta esta línea para forzar el anuncio de inicio
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        Log.d("LightActivity", "isFirstRun: " + isFirstRun);
        if (isFirstRun) {
            Log.d("LightActivity", "First run detected! Setting up welcome ad...");
            // Code to run once
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
            
            // Inicializar AdMob primero y luego cargar el anuncio de inicio
            Log.d("LightActivity", "Initializing AdMob for startup ad...");
            AdMobHelper.initialize(this);
            // Esperar un poco para que AdMob se inicialice antes de cargar el anuncio
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("LightActivity", "Loading startup interstitial ad...");
                    AdMobHelper.loadStartupInterstitial(LightActivity.this);
                    // Mostrar diálogo de agradecimiento y luego el anuncio
                    Log.d("LightActivity", "Showing welcome dialog...");
                    showWelcomeDialog();
                }
            }, 2000); // Aumentado a 2 segundos para dar más tiempo a AdMob
        } else {
            Log.d("LightActivity", "Not first run, skipping welcome ad");
        }
        btnDimer = (ImageButton) findViewById(R.id.btnDimer);
        btnDimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDimer();
            }
        });
        ibtnMenu = (ImageButton) findViewById(R.id.ibtnMenu);
        Intent intent = getIntent();
        isNotiOn = intent.getBooleanExtra("inNotiOn", false); // Por defecto false
        btnFlash = (ImageButton) findViewById((R.id.btnFlash));
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        // Leer el estado de la notificación (por defecto false - OFF)
        isNotiOn = sharedpreferences.getBoolean("isNotiOn", false);
        if (!isNotiOn) {
            btnFlash.setBackgroundResource(R.mipmap.ic_switch3off_foreground);
        } else {
            btnFlash.setBackgroundResource(R.mipmap.ic_switch3on_foreground);
            // Solo iniciar notificación si está activada
            startNotification(this);
        }

        shakeOn = sharedpreferences.getBoolean("shakeOn", shakeOn);

        final boolean hasFlash = this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        imagebutton = (ImageButton) findViewById(R.id.btnNoti);
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasFlash) {
                    Toast.makeText(getApplicationContext(), "No Flash", Toast.LENGTH_LONG).show();
                } else {
                    lightOn();
                }
            }
        });
        // Removido: startNotification(this) - solo se llama si isNotiOn es true

        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOn();
            }
        });
    }

    // --- The rest of your file remains unchanged below this point ---

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem versionItem = menu.findItem(R.id.version);
        if (versionItem != null) {
            try {
                versionItem.setTitle("v" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
            } catch (PackageManager.NameNotFoundException e) {
                versionItem.setTitle("v1.9.2");
            }
            versionItem.setEnabled(false);
        }
        return true;
    }
    
    /**
     * Show welcome dialog on first run, then show startup ad
     */
    private void showWelcomeDialog() {
        Log.d("LightActivity", "showWelcomeDialog called");
        // Esperar un poco para que la UI esté lista
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("LightActivity", "Showing welcome dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(LightActivity.this);
                builder.setTitle(getString(R.string.app_name));
                builder.setMessage("¡Gracias por instalar la app!\n\n" +
                        "Mientras terminamos la configuración inicial, mostraremos un anuncio para mantener la app en funcionamiento.");
                builder.setPositiveButton("Continuar", (dialog, which) -> {
                    Log.d("LightActivity", "User clicked Continuar, showing startup ad");
                    dialog.dismiss();
                    // Mostrar el anuncio de inicio después de cerrar el diálogo
                    showStartupAd();
                });
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }, 500);
    }
    
    /**
     * Show startup interstitial ad
     */
    private void showStartupAd() {
        Log.d("LightActivity", "showStartupAd called");
        AdMobHelper.showStartupInterstitial(this, new Runnable() {
            @Override
            public void run() {
                // Anuncio cerrado, continuar con la app normalmente
                Log.d("LightActivity", "Startup ad closed, app ready");
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rate:
                rateMe();
                return true;
            case R.id.share:
                share();
                return true;
            case R.id.emailme:
                mandarEmail();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startDimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            boolean settingsCanWrite = Settings.System.canWrite(this);

            if (!settingsCanWrite) {
                Intent d = new Intent(LightActivity.this, DimerBright.class);
                startActivity(d);
            } else {
                Intent dd = new Intent(LightActivity.this, Dimer.class);
                startActivity(dd);
            }
        } else {
            Intent d = new Intent(LightActivity.this, DimerBright.class);
            startActivity(d);
        }
    }

    private void help() {
        //  presentShowcaseSequence();
    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=lesbegueris.gaston.com.milinterna");
        startActivity(Intent.createChooser(share, "¡¡¡Compartime!!!"));
    }

    private void rateMe() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=lesbegueris.gaston.com.milinterna")));
    }

    private void mandarEmail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"lesberweb@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Desde My Lupa");
        startActivity(Intent.createChooser(intent, ""));
    }

    private void lightOn() {
        try {
            if (isTorchOn) {
                imagebutton.setBackgroundResource((R.mipmap.ic_switch3on_foreground));
                turnOffFlashLight();
                isTorchOn = false;
            } else {
                imagebutton.setBackgroundResource(R.mipmap.ic_switch3off_foreground);
                turnOnFlashLight();
                isTorchOn = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isFlash) {
            if (!isOn) {
                imagebutton.setBackgroundResource((R.mipmap.ic_switch3on_foreground));
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mCameraManager.setTorchMode(mCameraId, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOn = true;
            } else {
                imagebutton.setBackgroundResource(R.mipmap.ic_switch3off_foreground);
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mCameraManager.setTorchMode(mCameraId, false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOn = false;
            }
        }
    }

    public void turnOnFlashLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOffFlashLight() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void turnOn() {
        if (!isNotiOn) {
            isNotiOn = true;
            SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean("isNotiOn", true);
            editor.apply();
            editor.commit();
            startNotification(this);
            finish();
            startActivity(getIntent());
        } else {
            endNotification();
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("isNotiOn", false);
            editor.apply();
            editor.commit();
            isNotiOn = false;
        }
    }

    public void endNotification() {
        btnFlash.setBackgroundResource(R.mipmap.ic_switch3off_foreground);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(304);
        SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        isNotiOn = false;
        editor.putBoolean("isNotiOn", isNotiOn);
        editor.apply();
        editor.commit();
    }

    public void startNotification(Context context) {
        btnFlash.setBackgroundResource(R.mipmap.ic_switch3on_foreground);
        Intent intent1 = new Intent(this, LightActivity.class);
        intent1.putExtra("isNotiOn", true);
        PendingIntent pIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intent2 = new Intent(this, DimerBright.class);
        PendingIntent pIntent2 = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentAction = new Intent(this, ActionReceiver.class);
        intentAction.putExtra("action", "action1");
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        CharSequence titulo = getText(R.string.app_name);
        
        // Asegurar que el canal de notificación existe (para Android O+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
        
        // Verificar permisos de notificación (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                // Solicitar permiso
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
                Log.w("LightActivity", "Notification permission not granted, requesting...");
                return; // Salir y esperar a que se conceda el permiso
            }
        }
        
        // Crear y mostrar la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentTitle(titulo);
       // builder.setContentText("Linterna activa - Toca para controlar");
        // Usar icono de la app - Android requiere iconos monocromáticos para notificaciones
        builder.setSmallIcon(R.drawable.bulbon); // Usar drawable en lugar de mipmap
        builder.setContentIntent(pIntent1);
        builder.addAction(R.drawable.buttonon, "APP", pIntent1);
        builder.addAction(R.drawable.buttonon, "DIMMER", pIntent2);
        builder.setOngoing(true);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setAutoCancel(false);
        builder.setShowWhen(false);
        builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        
        Notification notification = builder.build();
        
        // Mostrar la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        try {
            if (notificationManager.areNotificationsEnabled()) {
                notificationManager.notify(304, notification);
                Log.d("LightActivity", "Notification shown successfully");
            } else {
                Log.w("LightActivity", "Notifications are disabled by user");
                // Abrir configuración de notificaciones
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                startActivity(intent);
            }
        } catch (SecurityException e) {
            Log.e("LightActivity", "SecurityException showing notification", e);
            // Si no hay permiso de notificación, solicitarlo
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 100);
            }
        } catch (Exception e) {
            Log.e("LightActivity", "Error showing notification", e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelName = getString(R.string.channel_name);
        String channelDescription = getString(R.string.channel_description);
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        channel.enableVibration(false);
        channel.setShowBadge(true);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Show banner when activity resumes
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Hide banner when activity pauses
        AppodealHelper.hideBanner(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hide banner when activity is destroyed
        AppodealHelper.hideBanner(this);
    }
}
