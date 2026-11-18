package lesbegueris.gaston.com.milinterna;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Log;

/**
 * Flashlight brightness (torch strength) helper.
 * On Android 13+ (API 33) devices whose flash reports FLASH_INFO_STRENGTH_MAXIMUM_LEVEL > 1
 * the torch is turned on with turnOnTorchWithStrengthLevel(); everywhere else it falls back
 * to the plain setTorchMode() on/off behavior.
 */
public final class TorchStrength {

    private static final String TAG = "TorchStrength";
    private static final String KEY_LEVEL = "torchStrengthLevel";

    private TorchStrength() {
    }

    /** Camera id used for the torch: the first camera id (legacy behavior) if it has a flash, else the first one that does. */
    public static String findFlashCameraId(CameraManager cm) {
        if (cm == null) return null;
        try {
            String[] ids = cm.getCameraIdList();
            if (ids.length == 0) return null;
            for (String id : ids) {
                Boolean hasFlash = cm.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                if (Boolean.TRUE.equals(hasFlash)) return id;
            }
            return ids[0];
        } catch (Exception e) {
            Log.e(TAG, "Error finding flash camera", e);
            return null;
        }
    }

    /** Max torch strength level, or 1 when the device/API level does not support dimming. */
    public static int getMaxLevel(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return 1;
        CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String id = findFlashCameraId(cm);
        if (id == null) return 1;
        try {
            Integer max = cm.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL);
            return max != null && max > 1 ? max : 1;
        } catch (Exception e) {
            Log.e(TAG, "Error reading max torch level", e);
            return 1;
        }
    }

    public static boolean isSupported(Context context) {
        return getMaxLevel(context) > 1;
    }

    /** Device default torch level (only meaningful when supported). */
    public static int getDefaultLevel(Context context) {
        int max = getMaxLevel(context);
        if (max <= 1 || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return max;
        CameraManager cm = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        String id = findFlashCameraId(cm);
        try {
            Integer def = cm.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_STRENGTH_DEFAULT_LEVEL);
            return def != null ? clamp(def, max) : max;
        } catch (Exception e) {
            return max;
        }
    }

    /** Last level chosen by the user, clamped to 1..max; defaults to the device default level. */
    public static int getSavedLevel(Context context) {
        int max = getMaxLevel(context);
        if (max <= 1) return 1;
        SharedPreferences prefs = context.getSharedPreferences(LightActivity.MyPREFERENCES, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_LEVEL)) return getDefaultLevel(context);
        return clamp(prefs.getInt(KEY_LEVEL, max), max);
    }

    public static void saveLevel(Context context, int level) {
        context.getSharedPreferences(LightActivity.MyPREFERENCES, Context.MODE_PRIVATE)
                .edit().putInt(KEY_LEVEL, level).apply();
    }

    /** Turns the torch on with the saved level when supported, otherwise plain setTorchMode(true). */
    public static void turnOn(Context context, CameraManager cm, String cameraId) {
        if (cm == null || cameraId == null) return;
        if (isSupported(context)) {
            applyLevel(cm, cameraId, getSavedLevel(context));
            return;
        }
        try {
            cm.setTorchMode(cameraId, true);
        } catch (Exception e) {
            Log.e(TAG, "Error turning torch on", e);
        }
    }

    /** Turns the torch on at the given level (API 33+); falls back to setTorchMode(true) on failure. */
    public static void applyLevel(CameraManager cm, String cameraId, int level) {
        if (cm == null || cameraId == null) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                cm.turnOnTorchWithStrengthLevel(cameraId, Math.max(1, level));
            } else {
                cm.setTorchMode(cameraId, true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error applying torch level " + level, e);
            try {
                cm.setTorchMode(cameraId, true);
            } catch (Exception ex) {
                Log.e(TAG, "Error turning torch on", ex);
            }
        }
    }

    private static int clamp(int level, int max) {
        return Math.max(1, Math.min(level, max));
    }
}
