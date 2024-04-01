/*
package com.vdsl.cybermart.Account.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Message.Adapter.AccountAdapter;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.databinding.FragmentStaffManagementBinding;

public class FragmentManagementStaff extends Fragment {

    FragmentStaffManagementBinding binding;
    DatabaseReference staffRef;

    AccountAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStaffManagementBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readDataStaff();
    }

    private void readDataStaff() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvStaff.setLayoutManager(linearLayoutManager);

        Query query = database.getReference("Account").orderByChild("Role").equalTo("Staff");
        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();




        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Lặp qua các phần tử trong dataSnapshot để log dữ liệu
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Log dữ liệu của mỗi phần tử
                        Log.d("VoucherActivity", "Voucher data received from Firebase: " + snapshot.getValue());
                    }
                } else {
                    Log.d("VoucherActivity", "No Voucher data found in Firebase.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("VoucherActivity", "Firebase query cancelled: " + databaseError.getMessage());
            }
        });
        adapter = new AccountAdapter(options);
        binding.rcvStaff.setAdapter(adapter);

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
*/
