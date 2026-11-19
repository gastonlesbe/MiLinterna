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

    /** Shows the Appodeal banner without a fallback. */
    public static void showBanner(Activity activity) {
        showBanner(activity, 0);
    }

    /**
     * Shows the Appodeal banner. If Appodeal has no fill and fallbackContainerId is not 0,
     * an AdMob banner is loaded once into that container instead. Once the AdMob fallback is
     * showing, a later Appodeal banner is not shown, so two banners never appear at the same time.
     */
    public static void showBanner(Activity activity, int fallbackContainerId) {
        final boolean[] admobFallbackShown = {false};
        try {
            Appodeal.setBannerCallbacks(new BannerCallbacks() {
                @Override
                public void onBannerLoaded(int height, boolean isPrecache) {
                    if (admobFallbackShown[0]) {
                        Log.d(TAG, "Banner loaded but AdMob fallback is already showing; skipping Appodeal");
                        return;
                    }
                    Log.d(TAG, "Banner loaded (height=" + height + "), showing...");
                    activity.runOnUiThread(() -> {
                        boolean shown = Appodeal.show(activity, Appodeal.BANNER);
                        Log.d(TAG, "Appodeal.show returned: " + shown);
                    });
                }

                @Override
                public void onBannerFailedToLoad() {
                    Log.w(TAG, "Banner failed to load");
                    if (fallbackContainerId != 0 && !admobFallbackShown[0]) {
                        admobFallbackShown[0] = true;
                        activity.runOnUiThread(() -> {
                            if (activity.isFinishing() || activity.isDestroyed()) return;
                            Log.d(TAG, "No Appodeal fill: showing AdMob banner fallback");
                            AdMobHelper.showBanner(activity, fallbackContainerId);
                        });
                    }
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
