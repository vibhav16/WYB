package com.wyb;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends Activity {
    ImageView imageView;
    SharedPreferences preferences;
    public String name;

    // Splash screen timer
    private long splashDelay = 2000; //2 seconds splash timeout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageView=(ImageView)findViewById(R.id.imgLogo);
        imageView.animate().rotationBy(360f).setDuration(1000);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                finish();
                Intent mainIntent = new Intent().setClass(SplashScreen.this, Logingoogle.class);
                    //overridePendingTransition(0, 0);
                    startActivity(mainIntent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);

            }
        };
        Timer timer = new Timer();
        timer.schedule(task, splashDelay);

    }

}
