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
import android.preference.PreferenceManager;
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


import com.appodeal.ads.initializing.ApdInitializationCallback;
import com.google.android.gms.ads.AdRequest;


import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.initializing.ApdInitializationError;
import com.appodeal.ads.utils.Log.LogLevel;

import java.util.List;


/**
 * Created by gaston on 24/06/17.
 */

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

    private AdView adView;
    private AdView mAdView;

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

        Appodeal.setTesting(true);
        Appodeal.setBannerViewId(R.id.appodeal_banner_view);
        Appodeal.initialize(LightActivity.this, "77043cce5169a8ba14f2b2a43e009d4853f76330ed9b8d11", Appodeal.BANNER, new ApdInitializationCallback() {
            @Override
            public void onInitializationFinished(@Nullable List<ApdInitializationError> errors) {
                // Appodeal initialization finished
            }
        });
        Appodeal.isLoaded(Appodeal.BANNER);
        Appodeal.setAutoCache(Appodeal.BANNER, false);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });



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
                                    }
        );
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
            //createNotificationChannel();
            boolean settingsCanWrite = Settings.System.canWrite(this);

            if (!settingsCanWrite) {
                Intent d = new Intent(LightActivity.this, DimerBright.class);
                startActivity(d);
                //close();
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
        // Intent e = new Intent(LightActivity.this, tutoActivity.class);
        //startActivity(e);

    }

    private void share() {
        Intent share = new Intent(Intent.ACTION_SEND);

        // If you want to share a png image only, you can do:
        // setType("image/png"); OR for jpeg: setType("image/jpeg");
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
                // releaseCameraAndPreview();


                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mCameraManager.setTorchMode(mCameraId, true);
                        //playOnOffSound();
                        //mTorchOnOffButton.setImageResource(R.drawable.on);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isOn = true;
            } else {
                imagebutton.setBackgroundResource(R.mipmap.ic_switch3off_foreground);


                // Toast.makeText(getApplicationContext(), R.string.Notification, Toast.LENGTH_LONG).show();
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        mCameraManager.setTorchMode(mCameraId, false);
                        // playOnOffSound();
                        //mTorchOnOffButton.setImageResource(R.drawable.off);

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
                // playOnOffSound();
                //mTorchOnOffButton.setImageResource(R.drawable.on);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
                // playOnOffSound();
                //mTorchOnOffButton.setImageResource(R.drawable.off);

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
            //  btnFlash.setBackgroundResource(R.mipmap.switchoff);

        } else {
            endNotification();
            //btnFlash.setBackgroundResource(R.mipmap.switchon);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("isNotiOn", false);
            editor.apply();
            editor.commit();
            isNotiOn = false;

        }

    }


    public void endNotification() {

        btnFlash.setBackgroundResource(R.mipmap.ic_switch3off_foreground);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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

        //This is the intent of PendingIntent
        Intent intentAction = new Intent(this, ActionReceiver.class);

        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action", "action1");
        //intentAction.putExtra("action2","action2");
        PendingIntent pIntentlogin = PendingIntent.getBroadcast(this, 1, intentAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        CharSequence titulo = getText(R.string.app_name);
        if (isFlash) {
            if (!isOn) {

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                builder.setSmallIcon(R.drawable.bulbon);
                //builder.setContentIntent(launchIntent);

                builder.addAction(R.drawable.buttonon, "APP", pIntent1);
                builder.addAction(R.drawable.bulbon, "DIMMER", pIntent2);
                builder.setOngoing(true);
                // builder.addAction(R.drawable.ic_notion,"OPEN", pIntent1);
                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                Notification notification = builder.build();

                //builder.setContentIntent(launchIntent);
                //builder.addAction(R.drawable.bulbon, "DIMMER", pIntent2);
                //builder.addAction(R.drawable.buttonon, "LIGHT", pIntent1);


                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                notificationManagerCompat.notify(304, notification);


                isOn = true;
            }
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public class ActionReceiver extends BroadcastReceiver {

        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();

            String action=intent.getStringExtra("action");
            if(action.equals("action1")){
                performAction1();
            }
            else if(action.equals("action2")){
                performAction2();

            }
            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(it);
        }

        public void performAction1(){
            Context context;
           // Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        }

        public void performAction2(){

        }
    }

    public PendingIntent getLaunchIntent(int notificationId, Context context) {


        //This is the intent of PendingIntent
        Intent intentAction = new Intent(this, ActionReceiver.class);
        intentAction.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //This is optional if you have more than one buttons and want to differentiate between two
        intentAction.putExtra("action","actionName");
        intentAction.putExtra("notificationId", notificationId);

        //Intent intent1 = new Intent(context, LightActivity.class);
        //Intent intent2 = new Intent(context, DimerBright.class);
        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //intent1.putExtra("notificationId", notificationId);
        return PendingIntent.getActivity(context, 0, intentAction, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(isOn){
            lightOn();

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isOn){
            lightOn();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isOn){
            lightOn();
        }
    }


            // @Override
            public void onClick (View v){
                // TODO Auto-generated method stub
                if (v.getId() == R.id.btnNoti || v.getId() == R.id.btnFlash || v.getId() == R.id.btnShake
                        || v.getId() == R.id.ibtnMenu) {



                } else if (v.getId() == R.id.ibtnMenu) {
                }

            }


        }


