package com.vdsl.cybermart.Order.Fragment;

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
import com.vdsl.cybermart.databinding.FragmentProgressingOrderBinding;

public class ProcessingOrderFragment extends Fragment {
    FragmentProgressingOrderBinding binding;
    OrderListAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProgressingOrderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Query query = FirebaseDatabase.getInstance().getReference("Orders")
                .orderByChild("status").equalTo("canceled");
        FirebaseRecyclerOptions<Order> options = new FirebaseRecyclerOptions.Builder<Order>()
                .setQuery(query, Order.class).build();
        adapter = new OrderListAdapter(options, order -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("Order",order);
            General.loadFragment(getParentFragmentManager(),new OrderDetailFragment(),bundle);
        });
        binding.rvProcessingList.setAdapter(adapter);
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