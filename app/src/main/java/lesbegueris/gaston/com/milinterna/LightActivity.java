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
import lesbegueris.gaston.com.milinterna.util.AppodealHelper;


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
        // --- APPODEAL INITIALIZATION (using AppodealHelper) ---
        String appodealAppKey = getString(R.string.appodeal_app_key);
        AppodealHelper.initialize(this, appodealAppKey);
        AppodealHelper.showBanner(this, R.id.appodealBannerView);
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
