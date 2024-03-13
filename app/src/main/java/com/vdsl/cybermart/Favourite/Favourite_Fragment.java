package com.vdsl.cybermart.Favourite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.vdsl.cybermart.databinding.FragmentFavouriteBinding;
import com.vdsl.cybermart.databinding.FragmentNotifyBinding;

public class Favourite_Fragment extends Fragment {
    FragmentFavouriteBinding binding;

    public Favourite_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
