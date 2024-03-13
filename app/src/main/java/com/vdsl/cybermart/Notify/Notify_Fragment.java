package com.vdsl.cybermart.Notify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.vdsl.cybermart.databinding.FragmentNotifyBinding;

public class Notify_Fragment extends Fragment {

    FragmentNotifyBinding binding;

    public Notify_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotifyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

}

