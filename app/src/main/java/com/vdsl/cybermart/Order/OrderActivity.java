package com.vdsl.cybermart.Order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.Order.Adapter.ProductsListAdapterInOrder;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.ActivityOrderBinding;

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
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
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
            if (phoneNumber.length()<10||!phoneNumber.startsWith("0")){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo");
                builder.setMessage("Nhập số điện thoại đúng định dạng");
                builder.setNegativeButton("OK", ((dialog, which) -> {
                }));
                AlertDialog dialog = builder.create();
                dialog.show();
            }else {
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
                                    Order order = new Order(id, address,phoneNumber, "Prepare", payment, voucher, cartModelNew, "Prepare" + cartModel.getAccountId());
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
