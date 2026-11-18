package lesbegueris.gaston.com.milinterna;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

/**
 * Handles the brightness -/+ actions of the flashlight notification: steps the saved
 * torch level and re-applies it if the torch is currently on.
 */
public class TorchLevelReceiver extends BroadcastReceiver {

    public static final String ACTION_LEVEL_DOWN = "lesbegueris.gaston.com.milinterna.TORCH_LEVEL_DOWN";
    public static final String ACTION_LEVEL_UP = "lesbegueris.gaston.com.milinterna.TORCH_LEVEL_UP";
    private static final long TORCH_STATE_TIMEOUT_MS = 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context appContext = context.getApplicationContext();
        String action = intent.getAction();
        int step;
        if (ACTION_LEVEL_UP.equals(action)) {
            step = 1;
        } else if (ACTION_LEVEL_DOWN.equals(action)) {
            step = -1;
        } else {
            return;
        }

        int max = TorchStrength.getMaxLevel(appContext);
        if (max <= 1) return;
        final int level = Math.max(1, Math.min(max, TorchStrength.getSavedLevel(appContext) + step));
        TorchStrength.saveLevel(appContext, level);

        final CameraManager cm = (CameraManager) appContext.getSystemService(Context.CAMERA_SERVICE);
        final String cameraId = TorchStrength.findFlashCameraId(cm);
        if (cm == null || cameraId == null) {
            refreshNotification(appContext);
            return;
        }

        // The current torch state is only reported through TorchCallback (it fires right after registering).
        final PendingResult pendingResult = goAsync();
        final Handler handler = new Handler(Looper.getMainLooper());
        final boolean[] done = {false};
        final CameraManager.TorchCallback callback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String id, boolean enabled) {
                if (id.equals(cameraId)) finish(enabled);
            }

            @Override
            public void onTorchModeUnavailable(@NonNull String id) {
                if (id.equals(cameraId)) finish(false);
            }

            void finish(boolean torchOn) {
                if (done[0]) return;
                done[0] = true;
                cm.unregisterTorchCallback(this);
                if (torchOn) {
                    TorchStrength.applyLevel(cm, cameraId, level);
                }
                refreshNotification(appContext);
                pendingResult.finish();
            }
        };
        cm.registerTorchCallback(callback, handler);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (done[0]) return;
                done[0] = true;
                cm.unregisterTorchCallback(callback);
                refreshNotification(appContext);
                pendingResult.finish();
            }
        }, TORCH_STATE_TIMEOUT_MS);
    }

    /** Re-posts the flashlight notification (new level in its text) only if it is still showing. */
    private static void refreshNotification(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        boolean showing = false;
        for (StatusBarNotification sbn : nm.getActiveNotifications()) {
            if (sbn.getId() == LightActivity.FLASH_NOTIFICATION_ID) {
                showing = true;
                break;
            }
        }
        if (!showing) return;
        try {
            NotificationManagerCompat.from(context)
                    .notify(LightActivity.FLASH_NOTIFICATION_ID, LightActivity.buildFlashNotification(context));
        } catch (SecurityException e) {
            Log.e("TorchLevelReceiver", "Notification permission missing", e);
        }
    }
}
