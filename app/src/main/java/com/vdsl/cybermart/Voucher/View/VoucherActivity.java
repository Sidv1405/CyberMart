package com.vdsl.cybermart.Voucher.View;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.CategoryManagement.Adapter.CateManageAdapter;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.Adapter.VoucherListAdapter;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ActivityMainBinding;
import com.vdsl.cybermart.databinding.ActivityVoucherBinding;
import com.vdsl.cybermart.databinding.UpdateVoucherBinding;

import java.util.ArrayList;

public class VoucherActivity extends AppCompatActivity {

    ActivityVoucherBinding binding;

    VoucherListAdapter adapter;

    DatabaseReference voucherRef;

    String role;

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

        SharedPreferences preferences = VoucherActivity.this.getSharedPreferences("Users",MODE_PRIVATE);
         role =preferences.getString("role","");

        readDataVoucher();

        showAdminOption();






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

                VoucherListAdapter newAdapter = new VoucherListAdapter(options,VoucherActivity.this);

                binding.rcvVoucher.setAdapter(newAdapter);

                newAdapter.startListening();

                return false;
            }
        });


    }

    private void showDialogDetail(Voucher voucher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        UpdateVoucherBinding updateVoucherBinding = UpdateVoucherBinding.inflate(inflater);
        builder.setView(updateVoucherBinding.getRoot());
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        if (voucher != null) {
            updateVoucherBinding.edtName.setText(voucher.getTitle());
            updateVoucherBinding.edtCode.setText(voucher.getCode());
            updateVoucherBinding.edtDiscount.setText(String.valueOf(voucher.getDiscount()));
            updateVoucherBinding.edtDate.setText(voucher.getExpiryDate());
        }

        updateVoucherBinding.btnDelete.setOnClickListener(v -> {
            onClickDeleteVoucher(voucher);
            dialog.dismiss();
        });

        updateVoucherBinding.btnUpdate.setOnClickListener(v -> {
            String newTitle = updateVoucherBinding.edtName.getText().toString().trim();
            String newCode = updateVoucherBinding.edtCode.getText().toString().trim();
            String newDiscount = updateVoucherBinding.edtDiscount.getText().toString().trim();
            String newDate = updateVoucherBinding.edtDate.getText().toString().trim();

            voucher.setTitle(newTitle);

            voucher.setDiscount(Integer.parseInt(newDiscount));
            voucher.setExpiryDate(newDate);

            // Đẩy việc cập nhật dữ liệu vào bên trong phương thức onIdReceived
            getIdFromCode(voucher.getCode(), new IdFromCodeCallback() {
                @Override
                public void onIdReceived(String id) {
                    if (id != null) {
                        voucher.setCode(newCode);
                        voucherRef.child(id).updateChildren(voucher.toMap(), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                if (error == null) {
                                    // Nếu không có lỗi, hiển thị thông báo thành công
                                    General.showSuccessPopup(VoucherActivity.this, "Thành Công", "Update Sản Phẩm Thành Công", new OnDialogButtonClickListener() {
                                        @Override
                                        public void onDismissClicked(Dialog dialog) {
                                            super.onDismissClicked(dialog);
                                        }
                                    });
                                    adapter.notifyDataSetChanged();
                                } else {
                                    // Nếu có lỗi, hiển thị thông báo lỗi
                                    Log.e("updateChildren", "Error updating voucher: " + error.getMessage());
                                }
                            }
                        });
                        dialog.dismiss();
                    } else {
                        // Nếu không tìm thấy voucher, hiển thị thông báo lỗi
                        Log.e("getIdFromCode", "Voucher không tồn tại với mã code: " + voucher.getCode());
                    }
                }
            });
        });
    }


    private void showAdminOption() {

        if (role.equals("Admin")){

            Log.e("checkIfelse", "onCreate: " + role );
            adapter.setOnItemClick(new VoucherListAdapter.OnItemClick() {
                @Override
                public void onItemClick(int position, Voucher voucher) {
                    showDialogDetail(adapter.getVoucherAtPosition(position));
                }
            });
            binding.flAddVoucher.setOnClickListener(v -> {
                Intent intent = new Intent(VoucherActivity.this, VoucherAddActivity.class);
                startActivity(intent);
            });
        }else{
            binding.flAddVoucher.setVisibility(View.GONE);
        }
    }

    private void readDataVoucher() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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

        adapter = new VoucherListAdapter(options,VoucherActivity.this);
        binding.rcvVoucher.setAdapter(adapter);
    }


    private void onClickDeleteVoucher(Voucher voucher) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Bạn Có Muốn Xóa Voucher Có Mã " + voucher.getCode() + " Không ?")
                .setPositiveButton("Đồng Ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        voucherRef = database.getReference().child("Voucher");
                        getIdFromCode(voucher.getCode(), new IdFromCodeCallback() {
                            @Override
                            public void onIdReceived(String id) {
                                if (id != null) {
                                    Log.e("test2", "onDismissClicked: " + id);
                                    voucherRef.child(id).removeValue(new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                            General.showSuccessPopup(VoucherActivity.this, "Thành Công", "Bạn Đã Xóa Voucher Thành Công", new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    dialog.dismiss();
                                                }
                                            });
                                        }
                                    });
                                } else {
                                    Log.e("getIdFromCode", "Voucher không tồn tại với mã code: " + voucher.getCode());
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void getIdFromCode(String code, IdFromCodeCallback callback) {
        DatabaseReference voucherRef = FirebaseDatabase.getInstance().getReference().child("Voucher");
        voucherRef.orderByChild("code").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String voucherId = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        voucherId = snapshot.getKey();
                        Log.e("getIdFromCode", "Firebase query open: " + voucherId);
                        break;
                    }
                }
                callback.onIdReceived(voucherId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("getIdFromCode", "Firebase query cancelled: " + databaseError.getMessage());
                callback.onIdReceived(null);
            }
        });
    }

    public interface IdFromCodeCallback {
        void onIdReceived(String id);
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