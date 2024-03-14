package com.vdsl.cybermart;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vdsl.cybermart.Category.CategoryAdapter;
import com.vdsl.cybermart.Category.Category_ElementAdapter;
import com.vdsl.cybermart.Favourite.Favourite_Fragment;
import com.vdsl.cybermart.Home.HomeFragment;
import com.vdsl.cybermart.Person.Fragment_Profile;
import com.vdsl.cybermart.Notify.Notify_Fragment;
import com.vdsl.cybermart.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    CategoryAdapter adapter;
    Category_ElementAdapter elementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater()); // Initialize binding
        setContentView(binding.getRoot());

        onClickListenerNavBottom();
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
                General.loadFragment(getSupportFragmentManager(), new Fragment_Profile(), null);
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.frag_container_main, new HomeFragment()).commit();
            }
            return true;
        });
    }

}