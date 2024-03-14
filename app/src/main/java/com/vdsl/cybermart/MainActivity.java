package com.vdsl.cybermart;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.cybermart.Category.Category;
import com.vdsl.cybermart.Category.CategoryAdapter;
import com.vdsl.cybermart.Category.Category_Element;
import com.vdsl.cybermart.Category.Category_ElementAdapter;
import com.vdsl.cybermart.Favourite.Favourite_Fragment;
import com.vdsl.cybermart.Person.Fragment_Profile;
import com.vdsl.cybermart.Notify.Notify_Fragment;
import com.vdsl.cybermart.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

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

        getSupportFragmentManager().beginTransaction().add(R.id.frag_container_main,new Home_Fragment()).commit();

    }



    private void onClickListenerNavBottom() {
        binding.navBottom.setOnItemSelectedListener(item -> {



            if (item.getItemId() == R.id.nav_bot_home) {
                General.loadFragment(getSupportFragmentManager(), new Home_Fragment(), null);
            } else if (item.getItemId() == R.id.nav_bot_marker) {
                General.loadFragment(getSupportFragmentManager(), new Favourite_Fragment(), null);

            } else if (item.getItemId() == R.id.nav_bot_notify) {
                General.loadFragment(getSupportFragmentManager(), new Notify_Fragment(), null);

//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.frag_container_main, new Bill_Fragment())
//                        .commit();
//                translayout();
//                loadFragment(billFragment);
            }else if (item.getItemId() == R.id.nav_bot_member) {
                General.loadFragment(getSupportFragmentManager(), new Fragment_Profile(), null);

            }
            /*binding.tbMain.setTitle(item.getTitle());*/
            return true;
        });
    }

    /*private void translayout() {
        binding.bgrDashBoard.setVisibility(View.GONE);
        binding.layoutDashboard.setVisibility(View.GONE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) binding.bgrDashBoardParent.getLayoutParams();
        layoutParams.width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        layoutParams.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        binding.bgrDashBoardParent.setLayoutParams(layoutParams);
    }*/
}