package lesbegueris.gaston.com.milinterna.util;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.AdListener;

/**
 * Helper class for AdMob ads (banner and interstitial)
 * Replaces Appodeal banners and interstitials with AdMob
 */
public class AdMobHelper {
    private static final String TAG = "AdMobHelper";
    private static InterstitialAd mInterstitialAd = null;
    private static AdView mBannerAd = null;
    private static boolean isInitialized = false;
    
    // AdMob Ad Unit IDs
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9841764898906750/9797523615";
    private static final String BANNER_AD_UNIT_ID = "ca-app-pub-9841764898906750/8090071538";
    private static final String STARTUP_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9841764898906750/3480675774";
    
    private static InterstitialAd mStartupInterstitialAd = null;
    
    /**
     * Initialize AdMob SDK
     */
    public static void initialize(Activity activity) {
        if (isInitialized) {
            Log.d(TAG, "AdMob already initialized");
            return;
        }
        
        try {
            MobileAds.initialize(activity, initializationStatus -> {
                isInitialized = true;
                Log.d(TAG, "AdMob initialized successfully");
                // Cargar el primer intersticial
                loadInterstitial(activity);
            });
        } catch (Exception e) {
            Log.e(TAG, "Error initializing AdMob", e);
        }
    }
    
    /**
     * Load interstitial ad
     */
    public static void loadInterstitial(Activity activity) {
        if (!isInitialized) {
            initialize(activity);
            return;
        }
        
        AdRequest adRequest = new AdRequest.Builder().build();
        
        InterstitialAd.load(activity, INTERSTITIAL_AD_UNIT_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    Log.d(TAG, "Interstitial ad loaded");
                }
                
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mInterstitialAd = null;
                    Log.w(TAG, "Interstitial ad failed to load: " + loadAdError.getMessage());
                }
            });
    }
    
    /**
     * Show interstitial ad with callback
     * @param activity The activity where interstitial will be shown
     * @param onAdClosed Runnable to execute when ad is closed
     */
    public static void showInterstitial(Activity activity, Runnable onAdClosed) {
        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed");
                    mInterstitialAd = null;
                    // Recargar el siguiente intersticial
                    loadInterstitial(activity);
                    // Ejecutar callback
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }
                
                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e(TAG, "Interstitial ad failed to show: " + adError.getMessage());
                    mInterstitialAd = null;
                    // Recargar el siguiente intersticial
                    loadInterstitial(activity);
                    // Ejecutar callback aunque falle
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }
                
                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad showed");
                }
            });
            
            mInterstitialAd.show(activity);
        } else {
            Log.w(TAG, "Interstitial ad not loaded, executing callback");
            // Si no hay ad cargado, ejecutar callback directamente
            if (onAdClosed != null) {
                onAdClosed.run();
            }
            // Intentar cargar uno para la próxima vez
            loadInterstitial(activity);
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
        return mInterstitialAd != null;
    }
    
    /**
     * Show banner ad in the specified container
     * @param activity The activity where banner will be shown
     * @param containerId The ID of the container ViewGroup (e.g., FrameLayout)
     */
    public static void showBanner(Activity activity, int containerId) {
        if (!isInitialized) {
            Log.w(TAG, "AdMob not initialized. Call initialize() first.");
            initialize(activity);
        }
        
        try {
            ViewGroup container = activity.findViewById(containerId);
            if (container == null) {
                Log.e(TAG, "Container not found with ID: " + containerId);
                return;
            }
            
            // Remove existing banner if any
            if (mBannerAd != null && mBannerAd.getParent() != null) {
                ((ViewGroup) mBannerAd.getParent()).removeView(mBannerAd);
            }
            
            // Create new AdView
            mBannerAd = new AdView(activity);
            mBannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
            mBannerAd.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
            
            // Set layout parameters
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            mBannerAd.setLayoutParams(params);
            
            // Set ad listener
            mBannerAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    Log.d(TAG, "Banner ad loaded successfully");
                    // Make sure banner is visible
                    if (mBannerAd != null) {
                        mBannerAd.setVisibility(android.view.View.VISIBLE);
                    }
                }
                
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    Log.w(TAG, "Banner ad failed to load: " + loadAdError.getMessage());
                    Log.w(TAG, "Error code: " + loadAdError.getCode());
                }
                
                @Override
                public void onAdClicked() {
                    Log.d(TAG, "Banner ad clicked");
                }
                
                @Override
                public void onAdOpened() {
                    Log.d(TAG, "Banner ad opened");
                }
                
                @Override
                public void onAdClosed() {
                    Log.d(TAG, "Banner ad closed");
                }
            });
            
            // Add to container
            container.addView(mBannerAd);
            mBannerAd.setVisibility(android.view.View.VISIBLE);
            
            // Load ad
            AdRequest adRequest = new AdRequest.Builder().build();
            mBannerAd.loadAd(adRequest);
            Log.d(TAG, "Banner ad load requested");
        } catch (Exception e) {
            Log.e(TAG, "Error showing banner", e);
        }
    }
    
    /**
     * Hide banner ad
     */
    public static void hideBanner(Activity activity) {
        try {
            if (mBannerAd != null) {
                mBannerAd.setVisibility(android.view.View.GONE);
                if (mBannerAd.getParent() != null) {
                    ((ViewGroup) mBannerAd.getParent()).removeView(mBannerAd);
                }
                mBannerAd.destroy();
                mBannerAd = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error hiding banner", e);
        }
    }
    
    /**
     * Load startup interstitial ad (shown on first run)
     */
    public static void loadStartupInterstitial(Activity activity) {
        Log.d(TAG, "loadStartupInterstitial called, isInitialized: " + isInitialized);
        if (!isInitialized) {
            Log.d(TAG, "AdMob not initialized, initializing now...");
            MobileAds.initialize(activity, initializationStatus -> {
                isInitialized = true;
                Log.d(TAG, "AdMob initialized in loadStartupInterstitial, loading ad...");
                loadStartupInterstitialAd(activity);
            });
            return;
        }
        Log.d(TAG, "AdMob already initialized, loading startup ad directly...");
        loadStartupInterstitialAd(activity);
    }
    
    private static void loadStartupInterstitialAd(Activity activity) {
        Log.d(TAG, "loadStartupInterstitialAd called with Ad Unit ID: " + STARTUP_INTERSTITIAL_AD_UNIT_ID);
        AdRequest adRequest = new AdRequest.Builder().build();
        
        InterstitialAd.load(activity, STARTUP_INTERSTITIAL_AD_UNIT_ID, adRequest,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(InterstitialAd interstitialAd) {
                    mStartupInterstitialAd = interstitialAd;
                    Log.d(TAG, "Startup interstitial ad loaded successfully");
                }
                
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    mStartupInterstitialAd = null;
                    Log.e(TAG, "Startup interstitial ad failed to load. Error code: " + loadAdError.getCode() + ", Message: " + loadAdError.getMessage());
                    Log.e(TAG, "Domain: " + loadAdError.getDomain() + ", Response Info: " + loadAdError.getResponseInfo());
                }
            });
    }
    
    /**
     * Show startup interstitial ad with callback
     * @param activity The activity where interstitial will be shown
     * @param onAdClosed Runnable to execute when ad is closed
     */
    public static void showStartupInterstitial(Activity activity, Runnable onAdClosed) {
        if (mStartupInterstitialAd != null) {
            mStartupInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Startup interstitial ad dismissed");
                    mStartupInterstitialAd = null;
                    // Ejecutar callback
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }
                
                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    Log.e(TAG, "Startup interstitial ad failed to show: " + adError.getMessage());
                    mStartupInterstitialAd = null;
                    // Ejecutar callback aunque falle
                    if (onAdClosed != null) {
                        onAdClosed.run();
                    }
                }
                
                @Override
                public void onAdShowedFullScreenContent() {
                    Log.d(TAG, "Startup interstitial ad showed");
                }
            });
            
            mStartupInterstitialAd.show(activity);
        } else {
            Log.w(TAG, "Startup interstitial ad not loaded, executing callback");
            // Si no hay ad cargado, ejecutar callback directamente
            if (onAdClosed != null) {
                onAdClosed.run();
            }
        }
    }
}
