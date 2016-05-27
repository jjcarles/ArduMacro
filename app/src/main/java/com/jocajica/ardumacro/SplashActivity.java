package com.jocajica.ardumacro;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends MacroActivity {

    static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Abre la actividad
                Intent i = new Intent(SplashActivity.this, ConexionActivity.class);
                startActivity(i);

                // Cierra la actividad
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
