package lesbegueris.gaston.com.milinterna.util;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.appodeal.ads.BannerCallbacks;

/**
 * Helper class for Appodeal banner ads integration
 * Replaces AdMob banner functionality
 * Based on Caretemplate implementation
 */
public class AppodealHelper {
    private static final String TAG = "AppodealHelper";
    private static boolean isInitialized = false;
    private static String initializedAppKey = null;
    private static int currentContainerId = -1;
    private static Activity currentActivity = null;

    /**
     * Initialize Appodeal SDK with app key
     * Call this once in Application class or MainActivity onCreate
     */
    public static void initialize(Activity activity, String appKey) {
        // Allow re-initialization if app key changed
        if (isInitialized && initializedAppKey != null && initializedAppKey.equals(appKey)) {
            Log.d(TAG, "Appodeal already initialized with same app key");
            return;
        }
        
        // If initialized with different app key, reset and re-initialize
        if (isInitialized && initializedAppKey != null && !initializedAppKey.equals(appKey)) {
            Log.d(TAG, "Appodeal initialized with different app key, re-initializing...");
            isInitialized = false;
            initializedAppKey = null;
        }

        try {
            Log.d(TAG, "Initializing Appodeal with app key: " + (appKey != null ? appKey.substring(0, Math.min(10, appKey.length())) + "..." : "null"));
            
            // Enable test mode for debug builds
            try {
                boolean isDebug = (activity.getApplicationInfo().flags & android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
                Appodeal.setTesting(isDebug);
                if (isDebug) {
                    Log.d(TAG, "Appodeal test mode enabled (debug build)");
                } else {
                    Log.d(TAG, "Appodeal test mode disabled (release build)");
                }
            } catch (Exception e) {
                Log.w(TAG, "Could not set Appodeal test mode", e);
            }
            
            // Set banner callbacks BEFORE initialization (important!)
            Appodeal.setBannerCallbacks(new BannerCallbacks() {
                @Override
                public void onBannerLoaded(int height, boolean isPrecache) {
                    Log.d(TAG, "Banner loaded successfully, height: " + height + ", isPrecache: " + isPrecache);
                    // Try to show banner when it's loaded and add to container
                    try {
                        String packageName = activity.getPackageName();
                        Log.d(TAG, "Attempting to show banner for package: " + packageName);
                        
                        // Si hay un contenedor configurado, agregar el banner
                        if (currentContainerId != -1 && currentActivity != null) {
                            activity.runOnUiThread(() -> {
                                try {
                                    ViewGroup container = currentActivity.findViewById(currentContainerId);
                                    if (container != null) {
                                        removeBanner(container);
                                        BannerView bannerView = Appodeal.getBannerView(currentActivity);
                                        if (bannerView != null) {
                                            container.addView(bannerView);
                                            Log.d(TAG, "Banner view added to container in callback");
                                        }
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Error adding banner to container in callback", e);
                                }
                            });
                        }
                        
                        Appodeal.show(activity, Appodeal.BANNER);
                    } catch (Exception e) {
                        Log.e(TAG, "Error showing banner after load", e);
                    }
                }

                @Override
                public void onBannerFailedToLoad() {
                    Log.w(TAG, "Banner failed to load - Possible causes:");
                    Log.w(TAG, "  1. App key not registered for this package name in Appodeal dashboard");
                    Log.w(TAG, "  2. App not active/approved in Appodeal");
                    Log.w(TAG, "  3. No ads available for this app");
                    Log.w(TAG, "  Package name: " + activity.getPackageName());
                    // Try to cache banner again after a delay
                    activity.getWindow().getDecorView().postDelayed(() -> {
                        try {
                            Log.d(TAG, "Retrying to cache banner...");
                            Appodeal.cache(activity, Appodeal.BANNER);
                        } catch (Exception e) {
                            Log.e(TAG, "Error retrying banner cache", e);
                        }
                    }, 5000);
                }

                @Override
                public void onBannerShown() {
                    Log.d(TAG, "Banner shown");
                }

                @Override
                public void onBannerShowFailed() {
                    Log.w(TAG, "Banner show failed");
                }

                @Override
                public void onBannerClicked() {
                    Log.d(TAG, "Banner clicked");
                }

                @Override
                public void onBannerExpired() {
                    Log.d(TAG, "Banner expired");
                }
            });
            
            // Initialize Appodeal with BANNER only (interstitials now use AdMob)
            Appodeal.initialize(activity, appKey, Appodeal.BANNER);
            isInitialized = true;
            initializedAppKey = appKey;
            Log.d(TAG, "Appodeal initialized successfully with app key: " + (appKey != null ? appKey.substring(0, Math.min(10, appKey.length())) + "..." : "null"));
            
            // Try to cache banner immediately after initialization
            try {
                Log.d(TAG, "Attempting to cache banner after initialization...");
                Appodeal.cache(activity, Appodeal.BANNER);
            } catch (Exception e) {
                Log.w(TAG, "Error caching banner after initialization", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Appodeal", e);
        }
    }

    /**
     * Show banner ad in the specified container
     * @param activity The activity where banner will be shown
     * @param containerId The ID of the container ViewGroup (e.g., FrameLayout)
     */
    public static void showBanner(Activity activity, int containerId) {
        if (!isInitialized) {
            Log.w(TAG, "Appodeal not initialized. Call initialize() first.");
            Log.w(TAG, "Package name: " + activity.getPackageName());
            return;
        }

        try {
            ViewGroup container = activity.findViewById(containerId);
            if (container == null) {
                Log.e(TAG, "Container not found with ID: " + containerId);
                Log.e(TAG, "Package name: " + activity.getPackageName());
                return;
            }

            // Guardar referencia al contenedor y actividad para el callback
            currentContainerId = containerId;
            currentActivity = activity;

            Log.d(TAG, "Attempting to show banner in container ID: " + containerId);
            Log.d(TAG, "Package name: " + activity.getPackageName());
            Log.d(TAG, "App key: " + (initializedAppKey != null ? initializedAppKey.substring(0, Math.min(10, initializedAppKey.length())) + "..." : "null"));
            
            // Remove any existing banner
            removeBanner(container);

            // Check if banner is loaded
            boolean isLoaded = Appodeal.isLoaded(Appodeal.BANNER);
            Log.d(TAG, "Banner loaded status: " + isLoaded);

            // If banner is not loaded, try to cache it first
            if (!isLoaded) {
                Log.d(TAG, "Banner not loaded, attempting to cache...");
                try {
                    Appodeal.cache(activity, Appodeal.BANNER);
                    Log.d(TAG, "Banner cache requested");
                } catch (Exception e) {
                    Log.w(TAG, "Error caching banner", e);
                }
            }

            // Try to get banner view and add to container
            BannerView bannerView = Appodeal.getBannerView(activity);
            if (bannerView != null) {
                Log.d(TAG, "Banner view obtained, adding to container");
                container.addView(bannerView);
                // Show banner - Appodeal will display it when ready
                Appodeal.show(activity, Appodeal.BANNER);
                Log.d(TAG, "Banner show() called");
            } else {
                Log.w(TAG, "Banner view is null, trying to show anyway (Appodeal will load automatically)");
                // Even if bannerView is null, Appodeal.show() will load and show the banner automatically
                Appodeal.show(activity, Appodeal.BANNER);
                Log.d(TAG, "Banner show() called (will load automatically)");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing banner", e);
        }
    }

    /**
     * Remove banner from container
     */
    private static void removeBanner(ViewGroup container) {
        for (int i = 0; i < container.getChildCount(); i++) {
            if (container.getChildAt(i) instanceof BannerView) {
                container.removeViewAt(i);
                break;
            }
        }
    }

    /**
     * Hide banner ad
     */
    public static void hideBanner(Activity activity) {
        try {
            Appodeal.hide(activity, Appodeal.BANNER);
        } catch (Exception e) {
            Log.e(TAG, "Error hiding banner", e);
        }
    }
}
