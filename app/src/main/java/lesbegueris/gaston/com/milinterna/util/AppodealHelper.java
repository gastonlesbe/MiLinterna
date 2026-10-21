package lesbegueris.gaston.com.milinterna.util;

import android.app.Activity;
import android.util.Log;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;

public class AppodealHelper {
    private static final String TAG = "AppodealHelper";

    public static void initialize(Activity activity, String appKey) {
        try {
            // Test mode in debug builds (res bool overridden in src/debug) — never live ads from dev/emulator runs
            Appodeal.setTesting(activity.getResources().getBoolean(lesbegueris.gaston.com.milinterna.R.bool.appodeal_testing));
            Appodeal.initialize(activity, appKey, Appodeal.BANNER);
            Log.d(TAG, "Appodeal initialized");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Appodeal", e);
        }
    }

    public static void showBanner(Activity activity) {
        try {
            Appodeal.setBannerCallbacks(new BannerCallbacks() {
                @Override
                public void onBannerLoaded(int height, boolean isPrecache) {
                    Log.d(TAG, "Banner loaded (height=" + height + "), showing...");
                    activity.runOnUiThread(() -> {
                        boolean shown = Appodeal.show(activity, Appodeal.BANNER);
                        Log.d(TAG, "Appodeal.show returned: " + shown);
                    });
                }

                @Override
                public void onBannerFailedToLoad() {
                    Log.w(TAG, "Banner failed to load");
                }

                @Override
                public void onBannerShown() {
                    Log.d(TAG, "Banner shown successfully");
                }

                @Override
                public void onBannerShowFailed() {
                    Log.w(TAG, "Banner show failed");
                }

                @Override
                public void onBannerClicked() {}

                @Override
                public void onBannerExpired() {}
            });
            Appodeal.cache(activity, Appodeal.BANNER);
            Log.d(TAG, "Banner cache requested");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up banner", e);
        }
    }

    public static void hideBanner(Activity activity) {
        try {
            Appodeal.hide(activity, Appodeal.BANNER);
        } catch (Exception e) {
            Log.e(TAG, "Error hiding banner", e);
        }
    }
}
