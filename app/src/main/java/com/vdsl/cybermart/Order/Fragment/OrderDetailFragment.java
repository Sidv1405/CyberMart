package com.vdsl.cybermart.Order.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.databinding.FragmentOrderDetailBinding;

public class OrderDetailFragment extends Fragment {
    FragmentOrderDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null){
            Order order = (Order) bundle.getSerializable("Order");
            if (order != null) {
                binding.txtSeriNumber.setText(order.getSeri());
                DatabaseReference referenceNameUser = FirebaseDatabase.getInstance().
                        getReference("Account").child(order.getIdUser());
                referenceNameUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            String nameUser = snapshot.child("FullName").getValue(String.class);
                            binding.txtCustomerName.setText(nameUser);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("DATA", "onDataChange: Error");
                    }
                });
            }
        }
    }
}