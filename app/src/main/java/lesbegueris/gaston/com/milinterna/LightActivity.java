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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import static lesbegueris.gaston.com.milinterna.NotificationLight.CHANNEL_ID;

// AdMob removido - Appodeal ya incluye AdMob en su mediation

// Your other imports
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.appodeal.ads.utils.Log.LogLevel;

import java.util.List;

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

    // --- Appodeal Banner variable ---
    private FrameLayout appodealBannerView;

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
        // --- START OF APPODEAL BANNER CODE ---

        // 1. Find the FrameLayout for Appodeal banner
        appodealBannerView = findViewById(R.id.appodealBannerView);

        // 2. Initialize Appodeal SDK
        String appodealAppKey = "77043cce5169a8ba14f2b2a43e009d4853f76330ed9b8d11";
        
        // Habilitar modo de prueba para ver ads de prueba
        Appodeal.setTesting(true);
        Appodeal.setLogLevel(LogLevel.verbose);
        
        // Verificar que el FrameLayout existe
        if (appodealBannerView == null) {
            Log.e("Appodeal", "ERROR: appodealBannerView es NULL!");
        } else {
            Log.d("Appodeal", "appodealBannerView encontrado correctamente");
            appodealBannerView.setVisibility(View.VISIBLE); // Asegurar que sea visible
        }
        
        // 3. Configurar el ID del banner view ANTES de inicializar
        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Log.d("Appodeal", "BannerViewId configurado: " + R.id.appodealBannerView);
        
        // 4. Set banner callbacks ANTES de inicializar (importante)
        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int height, boolean isPrecache) {
                // Banner loaded successfully - mostrar automáticamente
                Log.d("Appodeal", "Banner loaded, height: " + height + ", isPrecache: " + isPrecache);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Mostrar el banner automáticamente cuando esté cargado
                        if (Appodeal.isLoaded(Appodeal.BANNER)) {
                            Appodeal.show(LightActivity.this, Appodeal.BANNER);
                            Log.d("Appodeal", "Intentando mostrar banner después de cargar");
                        }
                    }
                });
            }

            @Override
            public void onBannerFailedToLoad() {
                // Banner failed to load
                Log.e("Appodeal", "Banner failed to load - verifica tu App Key y conexión a internet");
            }

            @Override
            public void onBannerShown() {
                // Banner shown
                Log.d("Appodeal", "Banner shown - ¡Éxito!");
            }

            @Override
            public void onBannerShowFailed() {
                // Banner show failed
                Log.e("Appodeal", "Banner show failed");
            }

            @Override
            public void onBannerClicked() {
                // Banner clicked
                Log.d("Appodeal", "Banner clicked");
            }

            @Override
            public void onBannerExpired() {
                // Banner expired
                Log.d("Appodeal", "Banner expired");
            }
        });
        
        // 4. Set interstitial callbacks ANTES de inicializar
        Appodeal.setInterstitialCallbacks(new com.appodeal.ads.InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                Log.d("Appodeal", "Interstitial loaded, isPrecache: " + isPrecache);
            }

            @Override
            public void onInterstitialFailedToLoad() {
                Log.e("Appodeal", "Interstitial failed to load");
            }

            @Override
            public void onInterstitialShown() {
                Log.d("Appodeal", "Interstitial shown");
            }

            @Override
            public void onInterstitialShowFailed() {
                Log.e("Appodeal", "Interstitial show failed");
            }

            @Override
            public void onInterstitialClicked() {
                Log.d("Appodeal", "Interstitial clicked");
            }

            @Override
            public void onInterstitialClosed() {
                Log.d("Appodeal", "Interstitial closed");
            }

            @Override
            public void onInterstitialExpired() {
                Log.d("Appodeal", "Interstitial expired");
            }
        });
        
        // 5. Initialize Appodeal SDK with BANNER and INTERSTITIAL
        Log.d("Appodeal", "Inicializando Appodeal con App Key: " + appodealAppKey);
        Appodeal.initialize(this, appodealAppKey, Appodeal.BANNER | Appodeal.INTERSTITIAL, new ApdInitializationCallback() {
            @Override
            public void onInitializationFinished(List<ApdInitializationError> errors) {
                if (errors == null || errors.isEmpty()) {
                    // Appodeal initialized successfully
                    Log.d("Appodeal", "Appodeal inicializado correctamente");
                    // Cargar el banner - se mostrará automáticamente cuando esté listo (en onBannerLoaded)
                    Log.d("Appodeal", "Iniciando cache de Banner...");
                    Appodeal.cache(LightActivity.this, Appodeal.BANNER);
                    // Pre-cargar interstitial para uso futuro
                    Log.d("Appodeal", "Iniciando cache de Interstitial...");
                    Appodeal.cache(LightActivity.this, Appodeal.INTERSTITIAL);
                } else {
                    // Handle initialization errors
                    Log.e("Appodeal", "Errores de inicialización:");
                    for (ApdInitializationError error : errors) {
                        Log.e("Appodeal", "Error: " + error.toString());
                    }
                }
            }
        });

        // --- END OF APPODEAL BANNER CODE ---
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
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        if (isFirstRun) {
            // Code to run once
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();
            //  presentShowcaseSequence();

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
        isNotiOn = intent.getBooleanExtra("inNotiOn", isNotiOn);
        btnFlash = (ImageButton) findViewById((R.id.btnFlash));
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        isNotiOn = sharedpreferences.getBoolean("isNotiOn", isNotiOn);
        if (!isNotiOn) {
            btnFlash.setBackgroundResource(R.mipmap.ic_switch3off_foreground);
        } else {
            btnFlash.setBackgroundResource(R.mipmap.ic_switch3on_foreground);
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
        startNotification(this);

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
        return true;
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
        PendingIntent pIntent1 = PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
        sendBroadcast(intent1);
        intent1.putExtra("isNotiOn", true);

        Intent intent2 = new Intent(this, DimerBright.class);
        PendingIntent pIntent2 = PendingIntent.getActivity(this, 0, intent2, PendingIntent.FLAG_IMMUTABLE);
        sendBroadcast(intent2);
        intent1.putExtra("isNotiOn", true);

        Intent intentAction = new Intent(this, ActionReceiver.class);
        intentAction.putExtra("action", "action1");
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        CharSequence titulo = getText(R.string.app_name);
        if (isFlash) {
            if (!isOn) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setSmallIcon(R.drawable.bulbon);
                builder.addAction(R.drawable.buttonon, "APP", pIntent1);
                builder.addAction(R.drawable.bulbon, "DIMMER", pIntent2);
                builder.setOngoing(true);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelName = "My App Channel";
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Appodeal handles lifecycle automatically for banners
        // Mostrar el banner si está cargado
        if (Appodeal.isLoaded(Appodeal.BANNER)) {
            Appodeal.show(this, Appodeal.BANNER);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Appodeal handles lifecycle automatically for banners
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Appodeal maneja el ciclo de vida automáticamente
    }
}
