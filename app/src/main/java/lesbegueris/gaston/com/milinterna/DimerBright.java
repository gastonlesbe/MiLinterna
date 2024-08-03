package lesbegueris.gaston.com.milinterna;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;


public class DimerBright extends Activity {

    ImageButton btnClose;
    SeekBar seekBar1;
    private int seek;
    View view;
    LinearLayout mScreen, mScreen1, myBar, myBar1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dimer);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);



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
        //updateBackground();


        mScreen.setBackgroundColor(Color.parseColor("#030303"));
        //seekBar1.setOnSeekBarChangeListener(seekBarChangeListener);


//    private SeekBar.OnSeekBarChangeListener seekBarChangeListener
        //        = new SeekBar.OnSeekBarChangeListener()
        seekBar1.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int sliderValue;



                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        sliderValue = progress;
                     //   sliderText.setText("Slider Position: " + sliderValue + "/" + seekBar1.getMax());
                        if (sliderValue <= 00) {
                            view.setBackgroundResource(R.mipmap.bulb1_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#030303"));
                           // mScreen1.setBackgroundColor(Color.parseColor("#030303"));

                        }
                        else if (sliderValue <= 25) {
                            view.setBackgroundResource(R.mipmap.bulb2_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#373737"));
                            //mScreen1.setBackgroundColor(Color.parseColor("#373737"));

                            //     playerInput.setText("Player1");
                        } else if (sliderValue <= 50) {
                            view.setBackgroundResource(R.mipmap.bulb3_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#818080"));
                            //mScreen1.setBackgroundColor(Color.parseColor("#818080"));
                            //   playerInput.setText("Player2");
                        } else if (sliderValue <= 75) {
                            view.setVisibility(View.VISIBLE);
                            view.setBackgroundResource(R.mipmap.bulb4_foreground);
                            mScreen.setBackgroundColor(Color.parseColor("#B1B1B1"));
                            //mScreen1.setBackgroundColor(Color.parseColor("#FCFCFC"));
                        }else if(sliderValue <=100){
                            view.setVisibility(View.INVISIBLE);
                            mScreen.setBackgroundColor(Color.parseColor("#FCFCFC"));
                            //mScreen1.setBackgroundColor(Color.parseColor("#FCFCFC"));
                            // playerInput.setText("Player3");
                        } else if (sliderValue <= seekBar1.getMax()) {
                            //p

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

    private void close() {
        finish();
    }
}


