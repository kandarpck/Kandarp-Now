package com.kandarp.launcher.now.places;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.kandarp.launcher.now.R;

public class Splash extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.places_splash);

        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Intent i = new Intent("com.example.myplaces.Menu");
                    startActivity(i);
                }

            }

        };
        t.start();

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }


}
