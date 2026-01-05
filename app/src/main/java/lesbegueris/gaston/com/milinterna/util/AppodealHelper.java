package lesbegueris.gaston.com.milinterna.util;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.BannerView;
import com.appodeal.ads.InterstitialCallbacks;

/**
 * Helper class for Appodeal banner ads integration
 * Replaces AdMob banner functionality
 */
public class AppodealHelper {
    private static final String TAG = "AppodealHelper";
    private static boolean isInitialized = false;

    /**
     * Initialize Appodeal SDK with app key
     * Call this once in Application class or MainActivity onCreate
     */
    public static void initialize(Activity activity, String appKey) {
        if (isInitialized) {
            Log.d(TAG, "Appodeal already initialized");
            return;
        }

        try {
            // Initialize Appodeal with BANNER only (interstitials now use AdMob)
            Appodeal.initialize(activity, appKey, Appodeal.BANNER);
            isInitialized = true;
            Log.d(TAG, "Appodeal initialized successfully (BANNER only)");
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
            return;
        }

        try {
            ViewGroup container = activity.findViewById(containerId);
            if (container == null) {
                Log.e(TAG, "Container not found with ID: " + containerId);
                return;
            }

            // Remove any existing banner
            removeBanner(container);

            // Configurar callbacks para saber cuando el banner está listo
            Appodeal.setBannerCallbacks(new BannerCallbacks() {
                @Override
                public void onBannerLoaded(int height, boolean isPrecache) {
                    Log.d(TAG, "Banner loaded, height: " + height);
                    // Cuando el banner se carga, agregarlo al contenedor
                    activity.runOnUiThread(() -> {
                        try {
                            ViewGroup container = activity.findViewById(containerId);
                            if (container != null) {
                                removeBanner(container);
                                BannerView bannerView = Appodeal.getBannerView(activity);
                                if (bannerView != null) {
                                    container.addView(bannerView);
                                    Log.d(TAG, "Banner added to container");
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error adding banner to container", e);
                        }
                    });
                }

                @Override
                public void onBannerFailedToLoad() {
                    Log.w(TAG, "Banner failed to load");
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

            // Mostrar el banner - Appodeal manejará la carga automáticamente
            Appodeal.show(activity, Appodeal.BANNER, containerId);
            Log.d(TAG, "Banner show requested");
            
            // También intentar obtener el banner view si ya está disponible
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        ViewGroup container = activity.findViewById(containerId);
                        if (container != null && Appodeal.isLoaded(Appodeal.BANNER)) {
                            removeBanner(container);
                            BannerView bannerView = Appodeal.getBannerView(activity);
                            if (bannerView != null && bannerView.getParent() == null) {
                                container.addView(bannerView);
                                Log.d(TAG, "Banner added to container (delayed)");
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error in delayed banner add", e);
                    }
                }
            }, 2000);
            
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

    /**
     * Show interstitial ad with callback
     * @param activity The activity where interstitial will be shown
     * @param onAdClosed Runnable to execute when ad is closed
     */
    public static void showInterstitial(Activity activity, Runnable onAdClosed) {
        if (!isInitialized) {
            Log.w(TAG, "Appodeal not initialized");
            if (onAdClosed != null) {
                onAdClosed.run();
            }
            return;
        }

        try {
            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                // Set callback for when ad is closed
                Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
                    @Override
                    public void onInterstitialLoaded(boolean isPrecache) {
                        Log.d(TAG, "Interstitial loaded");
                    }

                    @Override
                    public void onInterstitialFailedToLoad() {
                        Log.d(TAG, "Interstitial failed to load");
                        if (onAdClosed != null) {
                            onAdClosed.run();
                        }
                    }

                    @Override
                    public void onInterstitialShown() {
                        Log.d(TAG, "Interstitial shown");
                    }

                    @Override
                    public void onInterstitialClosed() {
                        Log.d(TAG, "Interstitial closed");
                        if (onAdClosed != null) {
                            onAdClosed.run();
                        }
                    }

                    @Override
                    public void onInterstitialClicked() {
                        Log.d(TAG, "Interstitial clicked");
                    }

                    @Override
                    public void onInterstitialExpired() {
                        Log.d(TAG, "Interstitial expired");
                    }

                    @Override
                    public void onInterstitialShowFailed() {
                        Log.d(TAG, "Interstitial show failed");
                        if (onAdClosed != null) {
                            onAdClosed.run();
                        }
                    }
                });
                
                Appodeal.show(activity, Appodeal.INTERSTITIAL);
            } else {
                Log.d(TAG, "Interstitial ad not loaded yet, executing callback");
                if (onAdClosed != null) {
                    onAdClosed.run();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error showing interstitial", e);
            if (onAdClosed != null) {
                onAdClosed.run();
            }
        }
    }

    /**
     * Show interstitial ad (simple version without callback)
     */
    public static void showInterstitial(Activity activity) {
        showInterstitial(activity, null);
    }

    /**
     * Check if interstitial is loaded
     */
    public static boolean isInterstitialLoaded() {
        return isInitialized && Appodeal.isLoaded(Appodeal.INTERSTITIAL);
    }
}
