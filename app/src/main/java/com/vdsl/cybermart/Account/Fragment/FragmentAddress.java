package com.vdsl.cybermart.Account.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Adapter.AddressAdapter;
import com.vdsl.cybermart.Account.Model.AddressModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentAddressBinding;

public class FragmentAddress extends Fragment {

    FragmentAddressBinding binding;
    DatabaseReference databaseReference;
    AddressAdapter addressAdapter;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            View bottomMenu = getActivity().findViewById(R.id.nav_bottom);
            if (bottomMenu != null) {
                bottomMenu.setVisibility(View.GONE);
            }
        }

        binding.rcAddress.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Account");

        binding.imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        if (currentUser != null) {
            usersRef.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            if (userId != null) {
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Account").child(userId).child("address");
                                setupRecyclerView();
                            } else {
                            }
                        }
                    } else {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }

        binding.btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void setupRecyclerView() {
        FirebaseRecyclerOptions<AddressModel> options =
                new FirebaseRecyclerOptions.Builder<AddressModel>()
                        .setQuery(databaseReference, AddressModel.class)
                        .build();
        addressAdapter = new AddressAdapter(options, getActivity());
        binding.rcAddress.setAdapter(addressAdapter);
        addressAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (addressAdapter != null) {
            addressAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        addressAdapter.stopListening();
    }
}