package com.vdsl.cybermart.Person;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.vdsl.cybermart.databinding.FragmentNotifyBinding;
import com.vdsl.cybermart.databinding.FragmentPersonBinding;

public class Person_Fragment extends Fragment {

    FragmentPersonBinding binding;

    public Person_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
