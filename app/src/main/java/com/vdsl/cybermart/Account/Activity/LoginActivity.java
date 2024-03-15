package com.vdsl.cybermart.Account.Activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vdsl.cybermart.Account.Fragment.FragmentLogIn;
import com.vdsl.cybermart.R;

public class LoginActivity extends AppCompatActivity {

//    TextView txt_SignUp,txt_forgotPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

//        txt_SignUp = findViewById(R.id.txt_signUp);
//        txt_forgotPass = findViewById(R.id.txt_forgotPass);
//
//        txt_SignUp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
//            }
//        });
//        txt_forgotPass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this, ForgotPassActivity.class));
//            }
//        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fr_framemain, new FragmentLogIn()).commit();


    }
}