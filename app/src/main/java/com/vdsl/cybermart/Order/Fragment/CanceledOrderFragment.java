package com.vdsl.cybermart.Order.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Order.Adapter.OrderListAdapter;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.databinding.FragmentCanceledOrderBinding;


public class CanceledOrderFragment extends Fragment {
    OrderListAdapter adapter;
    FragmentCanceledOrderBinding binding;
    SharedPreferences sharedPreferences;
    Query query;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCanceledOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("ID", "");
        String role = sharedPreferences.getString("Role", "");
        if (role.equals("Customers")) {
            query = FirebaseDatabase.getInstance().getReference("Orders")
                    .orderByChild("status").equalTo("canceled");
            query.orderByChild("idUser").equalTo(id);
        } else {
            query = FirebaseDatabase.getInstance().getReference("Orders")
                    .orderByChild("status").equalTo("delivered");
        }
        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class).build();
        adapter = new OrderListAdapter(options, order -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Order",order);
            General.loadFragment(getParentFragmentManager(),new OrderDetailFragment(),bundle);
        });
        binding.rvCanceledList.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}