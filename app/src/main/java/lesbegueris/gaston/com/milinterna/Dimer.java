package lesbegueris.gaston.com.milinterna;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.core.content.ContextCompat;

import lesbegueris.gaston.com.milinterna.util.AdMobHelper;




public class Dimer extends Activity {

    private Context mContext;
    ImageButton btnClose;
    SeekBar seekBar1;
    View view;
    LinearLayout mScreen;
    Context context;
    int brightness;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimer);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        mContext = getApplicationContext();
        
        // AdMob interstitials se manejan en AdMobHelper (no necesita setup aquí)

        btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        view = (View) findViewById(R.id.view);
        mScreen = (LinearLayout) findViewById(R.id.myBar);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        context = getApplicationContext();
        brightness = Settings.System.getInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 0);

        setDimLevel(0);

        seekBar1.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setDimLevel(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setDimLevel(int progress) {
        int bulbRes;
        int bgRes;
        int screenBrightness;

        if (progress <= 0) {
            bulbRes = R.mipmap.bulb1_foreground;
            bgRes = R.drawable.bg_dim_0;
            screenBrightness = 10;
        } else if (progress <= 25) {
            bulbRes = R.mipmap.bulb2_foreground;
            bgRes = R.drawable.bg_dim_1;
            screenBrightness = 25;
        } else if (progress <= 50) {
            bulbRes = R.mipmap.bulb3_foreground;
            bgRes = R.drawable.bg_dim_2;
            screenBrightness = 100;
        } else if (progress <= 75) {
            bulbRes = R.mipmap.bulb4_foreground;
            bgRes = R.drawable.bg_dim_3;
            screenBrightness = 180;
        } else {
            bulbRes = R.mipmap.bulb5_foreground;
            bgRes = R.drawable.bg_dim_4;
            screenBrightness = 255;
        }

        view.setVisibility(View.VISIBLE);
        view.setBackgroundResource(bulbRes);
        mScreen.setBackground(ContextCompat.getDrawable(context, bgRes));
        setScreenBrightness(screenBrightness);
    }

    private void setScreenBrightness(int brightnessValue) {
        if (brightnessValue >= 0 && brightnessValue <= 255) {
            Settings.System.putInt(
                    mContext.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS,
                    brightnessValue
            );
        }
    }


    // Intersticiales ahora usan AdMob (manejado en AdMobHelper)

    private void close() {
        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 255);

        // Mostrar el interstitial ad de AdMob (menos intrusivo)
        AdMobHelper.showInterstitial(this, new Runnable() {
            @Override
            public void run() {
                // Ad cerrado, ahora sí cerramos el Dimer
                closeDimer();
            }
        });
    }
    
    private void closeDimer() {
        finish();
    }
    
    @Override
    public void onBackPressed() {
        // Mostrar intersticial también cuando se presiona el botón de atrás
        close();
    }
}


