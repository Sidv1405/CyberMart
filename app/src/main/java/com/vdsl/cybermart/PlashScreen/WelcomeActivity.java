package com.vdsl.cybermart.PlashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Activity.LoginActivity;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.MainActivity;
import com.vdsl.cybermart.Message.FragmentMessage;
import com.vdsl.cybermart.Message.MessageActivity;
import com.vdsl.cybermart.R;

public class WelcomeActivity extends AppCompatActivity {

    TextView txtWelcome,txtName;
    LottieAnimationView lottie;

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static boolean isLoggedIn(){
        if(currentUserId()!=null){
            return true;
        }
        return false;
    }

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

       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },4000);*/

        if (getIntent().getExtras() != null ){
            String userEmail = getIntent().getStringExtra("email");
            Log.e("check37", "onCreate: "+ userEmail );
            Log.e("check35", "onCreate: "+ getIntent().getExtras());

           getIdFromEmail(userEmail, new FragmentMessage.OnIdReceivedListener() {
               @Override
               public void onIdReceived(String id) {
                   DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Account").child(id);
                   usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                       @Override
                       public void onDataChange(@NonNull DataSnapshot snapshot) {
                           if (snapshot.exists()) {
                               UserModel model = snapshot.getValue(UserModel.class);

                               Intent mainIntent = new Intent(WelcomeActivity.this, MainActivity.class);
                               mainIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                               startActivity(mainIntent);

                               Intent chatIntent = new Intent(WelcomeActivity.this, MessageActivity.class);
                               chatIntent.putExtra("username",model.getFullName());
                               chatIntent.putExtra("userEmail",model.getEmail());
                               chatIntent.putExtra("fcmToken",model.getFcmToken());
                               Log.e("check38", "onDataChange: " + model.getFcmToken() );
                               chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                               startActivity(chatIntent);

                               finish();
                           }
                       }

                       @Override
                       public void onCancelled(@NonNull DatabaseError error) {

                       }
                   });
               }
           });
        }else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(isLoggedIn()){
                        startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                    }else{
                        Intent intent=new Intent(WelcomeActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    finish();
                }
            },4000);
        }
    }

    private void getIdFromEmail(String email, final FragmentMessage.OnIdReceivedListener listener) {
        DatabaseReference peopleReference = FirebaseDatabase.getInstance().getReference().child("Account");
        peopleReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String accountId = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        accountId = dataSnapshot1.getKey();
                        Log.d("TAG", "onDataChange: sdone" + accountId);
                        break;
                    }
                }
                listener.onIdReceived(accountId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onIdReceived(null);
            }
        });
    }

    public interface OnIdReceivedListener {
        void onIdReceived(String id);
    }
}