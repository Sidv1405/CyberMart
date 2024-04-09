package com.vdsl.cybermart.Order;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Order.Adapter.ProductsListAdapterInOrder;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ActivityOrderBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {
    ActivityOrderBinding binding;
    Query query;
    ProductsListAdapterInOrder adapter;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.rdoPayment.check(R.id.rdoCredit);
        Intent intentRec = getIntent();
        if (intentRec != null) {
            Bundle bundle = intentRec.getExtras();
            if (bundle != null) {
                CartModel cartModel = (CartModel) bundle.getSerializable("cart");
                if (cartModel != null) {
                    getProductInOrder(cartModel);
                    SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
                    String address = sharedPreferences.getString("address", "");
                    String fullName = sharedPreferences.getString("fullName", "");
                    String phoneNumber = sharedPreferences.getString("phoneNumber", "");
                    binding.edtAddress.setText(address);
                    binding.edtFullName.setText(fullName);
                    binding.edtPhone.setText(phoneNumber);
                    binding.textTotalPrice.setText(cartModel.getTotalPrice() + "");
                    binding.btnCheckOutCart.setOnClickListener(v -> orderOnclick(cartModel));
                    binding.btnCancel.setOnClickListener(v -> finish());
                    binding.btnVoucher.setOnClickListener(v1 -> {
                        String promoCode = binding.textPromoCode.getText().toString().trim();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Voucher");
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                    /*String voucherCode = snapshot1.child("code").getValue(String.class);*/
                                    Voucher voucher = snapshot1.getValue(Voucher.class);
                                    if (voucher.getCode() != null && voucher.getCode().equals(promoCode)) {
                                        applyVoucher(voucher);
                                        return;
                                    }
                                }
                                Log.d("CartActivity", "Invalid voucher code or voucher does not exist");
                            }

                            @Override
                            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                            }
                        });
                    });

                    binding.btnListVoucher.setOnClickListener(v -> {
                        Intent intent = new Intent(OrderActivity.this, VoucherActivity.class);
                        startActivity(intent);
                    });
                    String reCode = getIntent().getStringExtra("voucherCode");
                    Log.e("check34", "onCreate: " + reCode);
                    binding.textPromoCode.setText(reCode);
                }
            }
        }

    }

    private void orderOnclick(CartModel cartModel) {
        String address = binding.edtAddress.getText().toString();
        String fullName = binding.edtFullName.getText().toString();
        String phoneNumber = binding.edtPhone.getText().toString();
        if (address.isEmpty() || fullName.isEmpty() || phoneNumber.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Thông báo");
            builder.setMessage("Nhập đầy đủ thông tin nhận hàng");
            builder.setNegativeButton("OK", ((dialog, which) -> {
            }));
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            if (phoneNumber.length() < 10 || !phoneNumber.startsWith("0")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo");
                builder.setMessage("Nhập số điện thoại đúng định dạng");
                builder.setNegativeButton("OK", ((dialog, which) -> {
                }));
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo");
                builder.setMessage("Xác nhận đặt hàng?");
                builder.setNegativeButton("Không", ((dialog, which) -> {
                }));
                builder.setPositiveButton("Có", (dialog, which) -> {
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders");
                    orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                            DatabaseReference newOrderRef = orderRef.push();
                            String id = newOrderRef.getKey();
                            String payment = binding.rdoCash.isChecked() ? "Cash" : "Credit Card";
                            String voucher = binding.textPromoCode.getText().toString().isEmpty() ? "0" : binding.textPromoCode.getText().toString();
                            //Lấy cart từ firebase
                            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(cartModel.getCartId());
                            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    CartModel cartModelNew = snapshot.getValue(CartModel.class);
                                    Order order = new Order(id, address, phoneNumber, "Prepare", payment, voucher, cartModelNew, "Prepare" + cartModel.getAccountId());
                                    newOrderRef.setValue(order);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                        }
                    });
                    dialog.cancel();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        }

    }

    private void getProductInOrder(CartModel cartModel) {
        query = FirebaseDatabase.getInstance().getReference().child("carts")
                .child(cartModel.getCartId()).child("cartDetail");

        FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class).build();
        adapter = new ProductsListAdapterInOrder(options);
        binding.rvProduct.setAdapter(adapter);
    }

    private void applyVoucher(Voucher voucher) {
        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        String cartDetailName = "cartDetail_" + accountId;
        SharedPreferences cartSharedPreferences = getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
        String cartId = cartSharedPreferences.getString("id", "");
        String voucherCode = voucher.getCode();
        double discount = ((double) voucher.getDiscount() / 100);
        Date currentDate = new Date();
        DatabaseReference userVouchersRef = FirebaseDatabase.getInstance().getReference("UserVouchers").child(accountId);
        DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");

        String dateString = voucher.getExpiryDate();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date expiryDate = dateFormat.parse(dateString);

            if (expiryDate != null && expiryDate.before(currentDate)) {
                Log.d("check30", "applyVoucher: " + currentDate + expiryDate);
                Log.d("CartActivity", "Voucher has expired");
                General.showFailurePopup(OrderActivity.this, "Sử Dụng Voucher", "Voucher đã hết hạn!", new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        super.onDismissClicked(dialog);
                    }
                });
                return;
            }
        } catch (ParseException e) {
            Log.e("CartActivity", "Error parsing expiry date", e);
        }
        userVouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(voucherCode).exists()) {
                        // Nếu voucher đã được sử dụng, hiển thị thông báo
                        Log.d("CartActivity", "Voucher đã được sử dụng trước đó");
                        General.showFailurePopup(OrderActivity.this, "Sử Dụng Voucher", "Bạn đã dùng voucher này!", new OnDialogButtonClickListener() {
                            @Override
                            public void onDismissClicked(Dialog dialog) {
                                super.onDismissClicked(dialog);
                            }
                        });
                    } else {
                        updateTotalPriceAndMarkVoucherUsed(totalPriceRef, userVouchersRef, voucherCode, discount);
                    }
                } else {
                    userVouchersRef.child(voucherCode).setValue(true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateTotalPriceAndMarkVoucherUsed(totalPriceRef, userVouchersRef, voucherCode, discount);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateTotalPriceAndMarkVoucherUsed(DatabaseReference
                                                            totalPriceRef, DatabaseReference userVouchersRef, String voucherCode, double discount) {
        totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalPrice = snapshot.getValue(Double.class);
                    double totalCartPrice = totalPrice * (1 - discount);
                    totalPriceRef.setValue(totalCartPrice);
                    String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalCartPrice);
                    binding.textTotalPrice.setText(String.format("%s $", formattedPrice));
                    General.showSuccessPopup(OrderActivity.this, "Sử Dụng Voucher", "Sử Dụng Voucher thành công", new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);
                        }
                    });

                    // Đánh dấu voucher đã sử dụng
                    userVouchersRef.child(voucherCode).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
