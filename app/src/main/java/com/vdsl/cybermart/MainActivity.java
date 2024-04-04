package com.vdsl.cybermart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vdsl.cybermart.Favourite.View.Favourite_Fragment;
import com.vdsl.cybermart.Home.View.HomeFragment;
import com.vdsl.cybermart.Notify.Notify_Fragment;
import com.vdsl.cybermart.Person.FragmentProfile;
import com.vdsl.cybermart.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        onClickListenerNavBottom();
        getSupportFragmentManager().beginTransaction().add(R.id.frag_container_main, new HomeFragment()).commit();

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
            }
            return true;
        });
    }

}