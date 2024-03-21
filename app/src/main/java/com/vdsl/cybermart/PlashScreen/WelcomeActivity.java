package com.vdsl.cybermart.PlashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.vdsl.cybermart.Account.Activity.LoginActivity;
import com.vdsl.cybermart.R;

public class WelcomeActivity extends AppCompatActivity {

    TextView txtWelcome,txtName;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lottie = findViewById(R.id.lottie);
        txtName = findViewById(R.id.txtName);
        txtWelcome = findViewById(R.id.txtWelcome);

        txtWelcome.animate().translationY(-100).setDuration(2700).setStartDelay(0);
        txtName.animate().translationY(-80).setDuration(2700).setStartDelay(0);
        lottie.animate().translationX(200).setDuration(2700).setStartDelay(2900);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        },4000);
    }
}