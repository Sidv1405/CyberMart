package com.vdsl.cybermart.Voucher.View;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;
import com.vdsl.cybermart.CategoryManagement.Adapter.CateManageAdapter;
import com.vdsl.cybermart.Home.Model.CategoryModel;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.Adapter.VoucherListAdapter;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ActivityMainBinding;
import com.vdsl.cybermart.databinding.ActivityVoucherBinding;

import java.util.ArrayList;

public class VoucherActivity extends AppCompatActivity {

    ActivityVoucherBinding binding;

    VoucherListAdapter adapter;

    DatabaseReference voucherRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voucher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding = ActivityVoucherBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        showAdminOption();

        readDataVoucher();
        

        binding.btnBack.setOnClickListener(v -> {
            super.onBackPressed();
        });

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Query query = FirebaseDatabase.getInstance().getReference("Voucher")
                        .orderByChild("code").startAt(newText).endAt(newText + "\uf8ff");
                FirebaseRecyclerOptions<Voucher> options = new FirebaseRecyclerOptions.Builder<Voucher>()
                        .setQuery(query, Voucher.class).build();

                VoucherListAdapter newAdapter = new VoucherListAdapter(options);

                binding.rcvVoucher.setAdapter(newAdapter);

                newAdapter.startListening();

                return false;
            }
        });


    }

    private void showAdminOption() {
        Query query = FirebaseDatabase.getInstance().getReference("Account")
                .orderByChild("Role").equalTo("Admin");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String role = snapshot.child("Role").getValue(String.class);
                        if ( role.equals("Admin")) {
                            Log.d("Admin Account", snapshot.getValue().toString());
                            binding.flAddVoucher.setOnClickListener(v -> {
                                Intent intent = new Intent(VoucherActivity.this, VoucherAddActivity.class);
                                startActivity(intent);
                            });
                        }else{
                            binding.flAddVoucher.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Log.d("AdminOption", "No admin accounts found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi truy vấn bị hủy
                Log.d("AdminOption", "Firebase query cancelled: " + databaseError.getMessage());
            }
        });
    }

    private void readDataVoucher() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.rcvVoucher.setLayoutManager(linearLayoutManager);

        voucherRef = database.getReference().child("Voucher");
        FirebaseRecyclerOptions<Voucher> options =
                new FirebaseRecyclerOptions.Builder<Voucher>()
                        .setQuery(voucherRef, Voucher.class)
                        .build();

        Log.d("VoucherActivity", "Querying Firebase for vouchers...");

        voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d("VoucherActivity", "Vouchers data received from Firebase." + voucherRef);
                } else {
                    Log.d("VoucherActivity", "No Voucher data found in Firebase.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("VoucherActivity", "Firebase query cancelled: " + databaseError.getMessage());
            }
        });

        adapter = new VoucherListAdapter(options);
        binding.rcvVoucher.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

}