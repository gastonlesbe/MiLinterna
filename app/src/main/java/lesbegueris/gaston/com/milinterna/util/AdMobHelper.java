package lesbegueris.gaston.com.milinterna.util;

import android.app.Activity;
import android.util.Log;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

/**
 * Helper class for AdMob interstitial ads
 * Replaces Appodeal interstitials with AdMob (less intrusive)
 */
public class AdMobHelper {
    private static final String TAG = "AdMobHelper";
    private static InterstitialAd mInterstitialAd = null;
    private static boolean isInitialized = false;
    
    // AdMob Interstitial Ad Unit ID
    private static final String INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-9841764898906750/8552541624";
    
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
}
