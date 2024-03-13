package com.vdsl.cybermart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class General {


    public static void loadFragment(FragmentManager fragmentManager, Fragment fragment, Bundle bundle) {
        if (bundle != null) fragment.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.frag_container_main, fragment)
                .addToBackStack(null) // Cho phép quay lại fragment trước đó nếu cần
                .commit();
    }
}
