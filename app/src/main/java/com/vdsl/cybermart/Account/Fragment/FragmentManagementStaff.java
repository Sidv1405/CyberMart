package com.vdsl.cybermart.Account.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Adapter.StaffMangeAdapter;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentStaffManagementBinding;

public class FragmentManagementStaff extends Fragment {

    FragmentStaffManagementBinding binding;
    DatabaseReference staffRef;

    StaffMangeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStaffManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        staffRef = FirebaseDatabase.getInstance().getReference().child("Account");

        View bottomMenu = getActivity().findViewById(R.id.nav_bottom);
        if (getActivity() != null) {
            if (bottomMenu != null) {
                bottomMenu.setVisibility(View.GONE);
            }
        }
        readDataStaff();

        binding.imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                bottomMenu.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });


        binding.btnAddStaff.setOnClickListener(v -> {
            FragmentAddStaff fragmentAddStaff = new FragmentAddStaff();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentAddStaff);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.svSearchStaff.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String searchText = newText.toLowerCase();

                Query query = staffRef.orderByChild("role").equalTo("Staff");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String fullName = dataSnapshot.child("fullName").getValue(String.class);
                            dataSnapshot.getRef().child("fullNameIndex").setValue(fullName.toLowerCase());
                        }

                        Query searchQuery = staffRef.orderByChild("fullNameIndex").startAt(searchText).endAt(searchText + "\uf8ff");

                        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                                .setQuery(searchQuery, UserModel.class)
                                .build();
                        if (requireActivity() != null) {
                            StaffMangeAdapter staffMangeAdapter = new StaffMangeAdapter(options, requireActivity());
                            binding.rcvStaff.setAdapter(staffMangeAdapter);
                            staffMangeAdapter.startListening();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                return false;
            }
        });
    }

    private void readDataStaff() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Account");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvStaff.setLayoutManager(linearLayoutManager);

        Query query = database.orderByChild("role").equalTo("Staff");
        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        adapter = new StaffMangeAdapter(options, getActivity());
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
