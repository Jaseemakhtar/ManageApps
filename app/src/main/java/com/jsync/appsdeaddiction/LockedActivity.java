package com.jsync.appsdeaddiction;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class LockedActivity extends AppCompatActivity{

    private ImageView imgAppIcon;
    private TextView txtHours, txtMinutes, txtSeconds;
    private Button btnDone;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked);

        imgAppIcon = findViewById(R.id.img_app_icon);
        txtHours = findViewById(R.id.txt_hours);
        txtMinutes = findViewById(R.id.txt_minutes);
        txtSeconds = findViewById(R.id.txt_seconds);
        btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                goHome();
            }
        });

        Intent intent = getIntent();
        String icon = intent.getStringExtra(CheckAppsBackground.APP_ICON);

        imgAppIcon.setImageURI(Uri.parse(icon));

        final long remainingTime = intent.getLongExtra(CheckAppsBackground.REMAINING, 0);
        long remainingTimeInMilliSeconds = remainingTime * 60 * 1000;

        countDownTimer = new CountDownTimer(remainingTimeInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                int h = (int) ((millisUntilFinished / 1000) / 60) / 60;
                int m = (int) ((millisUntilFinished / 1000) / 60) % 60;
                int s = (int) (millisUntilFinished / 1000) % 60;

                String hours = String.format(Locale.getDefault(), "%02d", h);
                String minutes = String.format(Locale.getDefault(), "%02d", m);
                String seconds = String.format(Locale.getDefault(), "%02d", s);

                txtHours.setText(hours);
                txtMinutes.setText(minutes);
                txtSeconds.setText(seconds);
            }

            @Override
            public void onFinish() {
                finish();
            }
        };

        countDownTimer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        goHome();
    }

    public void goHome(){
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);
        finish();
    }
}
