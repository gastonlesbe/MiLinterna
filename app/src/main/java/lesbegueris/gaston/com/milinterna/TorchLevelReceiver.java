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
 * Handles the brightness -/+ actions of the flashlight notification. "-" at the lowest level
 * turns the torch off and "+" while off turns it on at the lowest level (see step()).
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

        final int max = TorchStrength.getMaxLevel(appContext);
        if (max <= 1) return;
        final int stepFinal = step;

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
                step(appContext, cm, cameraId, torchOn, stepFinal, max);
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
                // No callback: fall back to the last known state.
                step(appContext, cm, cameraId, TorchStrength.isTorchOn(appContext), stepFinal, max);
                refreshNotification(appContext);
                pendingResult.finish();
            }
        }, TORCH_STATE_TIMEOUT_MS);
    }

    /**
     * "+" while off turns the torch on at level 1; "+" while on raises the level (up to max).
     * "-" while on lowers the level; "-" at level 1 turns the torch off; "-" while off does nothing.
     */
    private static void step(Context context, CameraManager cm, String cameraId, boolean torchOn, int step, int max) {
        int saved = TorchStrength.getSavedLevel(context);
        if (step > 0) {
            int level = torchOn ? Math.min(max, saved + 1) : 1;
            TorchStrength.saveLevel(context, level);
            TorchStrength.applyLevel(cm, cameraId, level);
            TorchStrength.setTorchOn(context, true);
        } else if (torchOn) {
            if (saved <= 1) {
                TorchStrength.turnOff(cm, cameraId);
                TorchStrength.setTorchOn(context, false);
            } else {
                int level = saved - 1;
                TorchStrength.saveLevel(context, level);
                TorchStrength.applyLevel(cm, cameraId, level);
            }
        }
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
