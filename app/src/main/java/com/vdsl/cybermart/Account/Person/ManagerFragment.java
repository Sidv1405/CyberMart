package com.vdsl.cybermart.Account.Person;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.vdsl.cybermart.Account.Fragment.FragmentManagementStaff;
import com.vdsl.cybermart.CategoryManagement.View.CategoryManagementActivity;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.ProductManagement.View.ProductManagementActivity;
import com.vdsl.cybermart.Statistic.Fragment.StatisticFragment;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.databinding.FragmentManagerBinding;


public class ManagerFragment extends Fragment {

    FragmentManagerBinding binding;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        showItemMenu();
        binding.imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        binding.CvCreateStaff.setOnClickListener(v ->
                General.loadFragment(getParentFragmentManager(), new FragmentManagementStaff(), null));
        binding.cvCateManage.setOnClickListener(v ->
                startActivity(new Intent(getContext(), CategoryManagementActivity.class)));
        binding.cvProManage.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ProductManagementActivity.class)));
        binding.btnMyVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VoucherActivity.class);
            startActivity(intent);
        });
        binding.btnStatistic.setOnClickListener(v ->
                General.loadFragment(getParentFragmentManager(), new StatisticFragment(), null));

    }

    private void showItemMenu() {
        String role = sharedPreferences.getString("role", "");
        Log.d("TAG", "showItemMenu: " + role);
        if (role.equals("Admin")) {
            binding.btnMyVoucher.setVisibility(View.VISIBLE);
            binding.btnStatistic.setVisibility(View.VISIBLE);
            binding.CvCreateStaff.setVisibility(View.VISIBLE);
        }
    }
    
}