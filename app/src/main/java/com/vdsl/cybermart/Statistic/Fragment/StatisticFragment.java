package com.vdsl.cybermart.Statistic.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayoutMediator;
import com.vdsl.cybermart.Order.Adapter.ViewPager2Adapter;
import com.vdsl.cybermart.Order.Fragment.CanceledOrderFragment;
import com.vdsl.cybermart.Order.Fragment.DeliveredFragment;
import com.vdsl.cybermart.Order.Fragment.ProcessingOrderFragment;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentStatisticBinding;

import java.util.ArrayList;

public class StatisticFragment extends Fragment {
    ArrayList<Fragment> fragments;
    FragmentStatisticBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStatisticBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragments = new ArrayList<>();
        fragments.add(new RevenueFragment());
        fragments.add(new ProcessingOrderFragment());
        ViewPager2Adapter adapter = new ViewPager2Adapter(requireActivity(),fragments);
        binding.vpOrder.setAdapter(adapter);
        TabLayoutMediator mediator = new TabLayoutMediator(binding.tabLayout,binding.vpOrder,((tab, i) -> {
            switch (i){
                case 0:{
                    tab.setText("Revenue");
                    break;
                }
                case 1:{
                    tab.setText("Processing");
                    break;
                }
            }
        }));
        mediator.attach();
    }
}