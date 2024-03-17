package com.vdsl.cybermart.Order.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vdsl.cybermart.Order.Adapter.ViewPager2Adapter;
import com.vdsl.cybermart.databinding.FragmentContainerBinding;


public class FragmentContainer extends Fragment {
    FragmentContainerBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContainerBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewPager2Adapter adapter = new ViewPager2Adapter(requireActivity());
        binding.vpOrder.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout,binding.vpOrder,((tab, i) -> {
            switch (i){
                case 0:{
                    tab.setText("Delivered");
                    break;
                }
                case 1:{
                    tab.setText("Processing");
                    break;
                }
                case 2:{
                    tab.setText("Canceled");
                    break;
                }
            }
        }));
        mediator.attach();
    }
}