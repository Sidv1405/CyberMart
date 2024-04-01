package com.vdsl.cybermart.Message;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.Message.Adapter.AccountAdapter;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.databinding.FragmentListCustomBinding;

import java.util.ArrayList;

public class ListCustomFragment extends Fragment {

    FragmentListCustomBinding binding;

    AccountAdapter adapter;

    String role;

    ArrayList<UserModel> list = new ArrayList<>();

    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListCustomBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readDataStaff();
    }

    private void readDataStaff() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvUser.setLayoutManager(linearLayoutManager);

        SharedPreferences preferences = getContext().getSharedPreferences("Users", MODE_PRIVATE);
        role = preferences.getString("role", "");
        Query query;
        Log.d("mycheck", "readDataStaff: " + role);
        if (role.equals("Customer")) {
            query = database.getReference("Account").orderByChild("role").equalTo("Staff");
        } else {
            query = database.getReference("Account").orderByChild("role").equalTo("Customer");
        }

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);
                    if (!user.getEmail().equals(firebaseUser.getEmail())) {
                        list.add(user);
                    }
                }
                // Sau khi cập nhật danh sách người dùng, cần thông báo cho Adapter
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("mycheck", "onCancelled: " + error.getMessage());
            }
        });

        adapter = new AccountAdapter(getContext(), list,false);
        binding.rcvUser.setAdapter(adapter);
    }


   /* @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }*/

}