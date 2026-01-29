package lesbegueris.gaston.com.milinterna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import lesbegueris.gaston.com.milinterna.util.AdMobHelper;




public class Dimer extends Activity {

    private Context mContext;
    ImageButton btnClose;
    SeekBar seekBar1;
    private int seek;
    View view;
    LinearLayout mScreen, mScreen1, myBar, myBar1;
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

        view =(View)findViewById(R.id.view);
        mScreen = (LinearLayout) findViewById(R.id.myBar);
        mScreen1 = (LinearLayout) findViewById(R.id.myBar1);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        context = getApplicationContext();
        brightness =
                Settings.System.getInt(context.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 0);

        Settings.System.putInt(this.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, 10);
        //updateBackground();

        //seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);


//    private SeekBar.OnSeekBarChangeListener seekBarChangeListener
        //        = new SeekBar.OnSeekBarChangeListener()
        seekBar1.setOnSeekBarChangeListener(
                new OnSeekBarChangeListener() {
                    int sliderValue;



                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        Settings.System.putInt(context.getContentResolver(),
                                Settings.System.SCREEN_BRIGHTNESS, progress);
                        sliderValue = progress;
                     //   sliderText.setText("Slider Position: " + sliderValue + "/" + seekBar1.getMax());
                        if (sliderValue <= 00) {
                            view.setBackgroundResource(R.mipmap.bulb1_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#030303"));
                           setScreenBrightness(10);
                        }
                        else if (sliderValue <= 25) {
                            view.setBackgroundResource(R.mipmap.bulb2_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#373737"));
                           setScreenBrightness(25);
                       //     playerInput.setText("Player1");
                        } else if (sliderValue <= 50) {
                            view.setBackgroundResource(R.mipmap.bulb3_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#818080"));
                            setScreenBrightness(100);
                         //   playerInput.setText("Player2");
                        } else if (sliderValue <= 75) {
                            view.setVisibility(View.VISIBLE);
                            view.setBackgroundResource(R.mipmap.bulb4_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#B1B1B1"));
                            setScreenBrightness(150);
                        }else if(sliderValue <=100){
                           // view.setBackgroundResource(R.mipmap.bulb5_foreground);
                            view.setVisibility(View.INVISIBLE);
                            mScreen.setBackgroundColor(Color.parseColor("#FCFCFC"));
                            setScreenBrightness(255);
                           // playerInput.setText("Player3");
                        } else if (sliderValue <= seekBar1.getMax()) {
                            //playerInput.setText("Player4");
                        }


/*
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
// TODO Auto-generated method stub
        updateBackground();
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
// TODO Auto-generated method stub
    }
};

private void updateBackground()
        {
        seek = seekBar1.getProgress();
        mScreen.setBackgroundColor(
        0xff000000
        + seek * 0x10000 + seek * 0x100 + seek * 0x1
        );
        }
*/
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }

                });
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


