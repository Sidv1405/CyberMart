package com.vdsl.cybermart;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.vdsl.cybermart.Account.Fragment.FragmentAddress;
import com.vdsl.cybermart.Favourite.View.Favourite_Fragment;
import com.vdsl.cybermart.Home.View.HomeFragment;
import com.vdsl.cybermart.Message.FragmentMessage;
import com.vdsl.cybermart.Notify.Notify_Fragment;
import com.vdsl.cybermart.Person.FragmentProfile;
import com.vdsl.cybermart.databinding.ActivityMainBinding;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    String userEmail;

    FirebaseUser firebaseUser;

    ActivityMainBinding binding;
    public int MY_REQUEST_CODE=99;
    String role;
    SharedPreferences sharedPreferences;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        userEmail = firebaseUser.getEmail();
        if (firebaseUser != null) {
             userEmail = firebaseUser.getEmail();
        } else {
            userEmail=sharedPreferences.getString("email",null);
        }
        getFCMToken();

        onClickListenerNavBottom();
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container_main, new HomeFragment()).commit();

        SharedPreferences preferences = MainActivity.this.getSharedPreferences("Users", MODE_PRIVATE);
        role = preferences.getString("role", "");
    }

    private void onClickListenerNavBottom() {
        binding.navBottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_bot_home) {
                General.loadFragment(getSupportFragmentManager(), new HomeFragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_marker) {
                General.loadFragment(getSupportFragmentManager(), new Favourite_Fragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_notify) {
                General.loadFragment(getSupportFragmentManager(), new Notify_Fragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_member) {
                General.loadFragment(getSupportFragmentManager(), new FragmentProfile(), null);
            } else if (item.getItemId() == R.id.nav_bot_chat) {
                Log.e("checkIf", "onCreate: " + role);
                if (role.equals("Staff") || role.equals("Admin")) {
                    General.loadFragment(getSupportFragmentManager(), new FragmentMessage(), null);
                } else {
                    General.loadFragment(getSupportFragmentManager(), new FragmentMessage(), null);
                }
            }
            return true;
        });
    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onBackPressed() {
        View bottomMenu = findViewById(R.id.nav_bottom);
        FragmentAddress fragmentAddress = new FragmentAddress();
        Fragment currentFragment = fragmentAddress;
        if (currentFragment instanceof FragmentAddress) {
            bottomMenu.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String token = task.getResult();
                if (token != null) {
                    updateFCMToken(token);
                }
            }
        });
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

    private void updateFCMToken(String token) {
        getIdFromEmail(userEmail, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(final String id) {
                if (id != null) {
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Account").child(id);
                    userRef.child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                userRef.child("fcmToken").setValue(token)
                                        .addOnSuccessListener(aVoid -> Log.d("FragmentMessage", "FCM token created successfully"))
                                        .addOnFailureListener(e -> Log.e("FragmentMessage", "Failed to create FCM token", e));
                            } else {
                                userRef.child("fcmToken").setValue(token)
                                        .addOnSuccessListener(aVoid -> Log.d("FragmentMessage", "FCM token updated successfully: " + token))
                                        .addOnFailureListener(e -> Log.e("FragmentMessage", "Failed to update FCM token", e));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e("FragmentMessage", "Database error: " + databaseError.getMessage());
                        }
                    });
                } else {
                    Log.d("FragmentMessage", "No user found with this email");
                }
            }
        });
    }

    private void status(String status) {
        getIdFromEmail(userEmail, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                reference = FirebaseDatabase.getInstance().getReference("Account").child(id);
                Log.d("TAG", "status: " + id);
                if (id != null) {
                    reference = FirebaseDatabase.getInstance().getReference("Account").child(id);
                    Log.d("TAG", "onIdReceived: " + id);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("status",status);
                    reference.updateChildren(hashMap);
                } else {
                    Log.d("FragmentMessage", "No user found with this email");
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        status(("online"));
    }

    @Override
    public void onPause() {
        super.onPause();
        status("offline");
    }
}