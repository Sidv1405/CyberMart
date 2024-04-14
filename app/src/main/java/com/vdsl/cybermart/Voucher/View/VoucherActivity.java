package com.vdsl.cybermart.Voucher.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.Cart.View.CartActivity;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.Adapter.VoucherListAdapter;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ActivityVoucherBinding;
import com.vdsl.cybermart.databinding.UpdateVoucherBinding;

import java.util.ArrayList;
import java.util.Objects;

public class VoucherActivity extends AppCompatActivity {

    ActivityVoucherBinding binding;

    VoucherListAdapter adapter;

    DatabaseReference voucherRef;

    ArrayList<Voucher> voucherList;

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
        voucherList = new ArrayList<>();

        SharedPreferences preferences = VoucherActivity.this.getSharedPreferences("Users", MODE_PRIVATE);
        role = preferences.getString("role", "");



        readDataVoucher();

        showAdminOption();

        writeUserVoucher();

        binding.btnBack.setOnClickListener(v ->{
            startActivity(new Intent(this, CartActivity.class));
            finish();
        });

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchVoucher(newText);
                return false;
            }
        });


    }

    private void searchVoucher(String query) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference("Voucher");

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                voucherList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Voucher voucher = snapshot1.getValue(Voucher.class);
                    if (voucher != null && voucher.getCode().contains(query)) {
                        voucherList.add(voucher);
                    }
                }
                adapter = new VoucherListAdapter(voucherList, VoucherActivity.this);
                binding.rcvVoucher.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialogDetail(Voucher voucher) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        UpdateVoucherBinding updateVoucherBinding = UpdateVoucherBinding.inflate(inflater);
        builder.setView(updateVoucherBinding.getRoot());
        Dialog dialog = builder.create();

        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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

        if (role.equals("Admin")) {

            Log.e("checkIfelse", "onCreate: " + role);
            if (adapter != null) {
                adapter.setOnItemClick(new VoucherListAdapter.OnItemClick() {
                    @Override
                    public void onItemClick(int position, Voucher voucher) {
                        showDialogDetail(adapter.getVoucherAtPosition(position));
                    }
                });
            }
            binding.flAddVoucher.setOnClickListener(v -> {
                Intent intent = new Intent(VoucherActivity.this, VoucherAddActivity.class);
                startActivity(intent);
            });
        } else {
            binding.flAddVoucher.setVisibility(View.GONE);
        }
    }

    private void readDataVoucher() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        DatabaseReference voucherRef = database.getReference().child("Voucher");
        DatabaseReference userVoucherRef = database.getReference().child("UserVouchers").child(accountId);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VoucherActivity.this, LinearLayoutManager.VERTICAL, false);
        binding.rcvVoucher.setLayoutManager(linearLayoutManager);

        if (role.equals("Admin")) {
            voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    voucherList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Voucher voucher = dataSnapshot.getValue(Voucher.class);
                        if (voucher != null) {
                            voucherList.add(voucher);
                            Log.e("check63", "onDataChange: " + voucherList.toString());
                        }
                    }
                    // Sau khi đã thêm hết dữ liệu vào danh sách, set adapter cho RecyclerView
                    Log.e("check68", "onDataChange: " + voucherList.toString() + voucherList.size());
                    adapter = new VoucherListAdapter(voucherList, VoucherActivity.this);
                    binding.rcvVoucher.setAdapter(adapter);
                    adapter.notifyDataSetChanged(); // Bắt buộc gọi hàm này để cập nhật giao diện
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("readDataVoucher", "Database Error: " + error.getMessage());
                }
            });
        } else {
            voucherCustomer();
        }
    }


    private void writeUserVoucher() {

        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        DatabaseReference vouchersRef = FirebaseDatabase.getInstance().getReference("Voucher");
        DatabaseReference userVoucherRef = FirebaseDatabase.getInstance().getReference("UserVouchers");

        // Lấy danh sách tất cả các voucher
        vouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot voucherSnapshot : snapshot.getChildren()) {
                        String voucherCode = voucherSnapshot.child("code").getValue(String.class);
                        Log.e("check61", "writeUserVoucher: " + voucherCode);
                        Log.e("check62", "writeUserVoucher: " + accountId);

                        // Kiểm tra xem mã code đã tồn tại trong bảng UserVoucher chưa
                        userVoucherRef.child(accountId).child(voucherCode).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.exists()) {
                                    // Nếu mã code chưa tồn tại, thêm vào bảng UserVoucher
                                    userVoucherRef.child(accountId).child(voucherCode).setValue(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("writeUserVoucher", "Database Error: " + databaseError.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("writeUserVoucher", "Database Error: " + error.getMessage());
            }
        });
    }


    public void voucherCustomer() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        DatabaseReference voucherRef = database.getReference().child("Voucher");
        DatabaseReference userVoucherRef = database.getReference().child("UserVouchers").child(accountId);
        userVoucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot voucherSnapshot : dataSnapshot.getChildren()) {
                    String voucherCode = voucherSnapshot.getKey(); // Lấy mã code của voucher
                    boolean isUsed = voucherSnapshot.getValue(Boolean.class); // Kiểm tra xem nó có bằng false không

                    Log.e("check64", "onDataChange: " + voucherCode + isUsed);

                    if (!isUsed && voucherCode != null) {
                        voucherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot voucherSnapshot : snapshot.getChildren()) {
                                    if (snapshot.exists()) {
                                        Voucher voucher = voucherSnapshot.getValue(Voucher.class);
                                        if (voucher != null && voucher.getCode().equals(voucherCode)) {
                                            // Nếu trùng khớp, thêm vào danh sách
                                            voucherList.add(voucher);
                                            Log.e("check65", "onDataChange: " + voucherCode + isUsed + voucher);
                                            Log.e("check66", "onDataChange: " + voucherList.toString());
                                            /*break;*/
                                        }
                                    }
                                }
                                Log.e("check67", "onDataChange: " + voucherList.toString());
                                adapter = new VoucherListAdapter(voucherList, VoucherActivity.this);
                                binding.rcvVoucher.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("readDataVoucher", "Database Error: " + error.getMessage());
                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("readDataVoucher", "Database Error: " + error.getMessage());
            }
        });
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


}