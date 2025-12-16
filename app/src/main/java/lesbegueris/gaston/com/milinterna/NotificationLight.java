package lesbegueris.gaston.com.milinterna;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.IBinder;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Created by gaston on 28/06/17.
 */
@SuppressWarnings("deprecation")
public class NotificationLight extends IntentService {
    Camera camera1;
    Camera.Parameters parameters1;
    boolean isOn1 = false;
    boolean isFlash1 = false;
    boolean isNotiOn, shakeOn;
    public static final String CHANNEL_ID = "MiLinterna";

    // Unique identifier for notification
    public static final int NOTIFICATION_ID = 101;

    public NotificationLight() {

        super("NotificationLight");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (camera1 != null) {

            camera1.release();
        }


        isFlash1 = true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Intent intent1 = new Intent(this, LightActivity.class);
        intent1.putExtra("isNotiOn", true);
        PendingIntent pIntent1 = PendingIntent.getActivity(this,
                (int) System.currentTimeMillis(), intent1, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        sendBroadcast(intent1);

        Intent notificationIntent = new Intent(this, NotificationLight.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, (int) System.currentTimeMillis(),
                notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        CharSequence titulo = getText(R.string.app_name);



        if (isFlash1) {
            if (!isOn1) {

                camera1 = Camera.open();

                parameters1 = camera1.getParameters();


 // CharSequence titulo = getText(R.string.app_name);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setContentText(titulo);
        builder.setSmallIcon(R.drawable.notioff);
        builder.setContentIntent(pIntent1);
        builder.setOngoing(true);
        builder.addAction(R.drawable.buttonon, "ON", pendingIntent);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        Notification notification = builder.build();


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(304, notification);


                Toast.makeText(getApplicationContext(), "Light On", Toast.LENGTH_LONG).show();
                parameters1.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera1.setParameters(parameters1);
                camera1.startPreview();
                isOn1 = true;
            } else {
                Notification notification = new NotificationCompat.Builder(this)
                        .setContentText(titulo)
                        .setSmallIcon(R.drawable.notioff)
                        .setContentIntent(pIntent1)
                        .setOngoing(true)
                        .addAction(R.drawable.buttonon, "ON", pendingIntent)
                        .setChannelId(CHANNEL_ID)
                        .build();

//This is what will will issue the notification i.e.notification will be visible


                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify(33, notification);

                Toast.makeText(getApplicationContext(), "Light Off", Toast.LENGTH_LONG).show();
                parameters1.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera1.setParameters(parameters1);
                camera1.stopPreview();
                camera1.release();
                isOn1 = false;
            }


        }

        return START_NOT_STICKY;
    }

/*
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
*/
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    public void turnOn() {
        if (isFlash1) {
            if (!isOn1) {

                parameters1.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera1.setParameters(parameters1);
                camera1.startPreview();
                isOn1 = true;
            } else {

                parameters1.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera1.setParameters(parameters1);
                camera1.stopPreview();
                isOn1 = false;
            }


        }

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "NotificationLight Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

}
