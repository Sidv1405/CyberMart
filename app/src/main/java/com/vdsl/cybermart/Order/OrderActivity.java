package com.vdsl.cybermart.Order;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Fragment.FragmentAddress;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.Order.Adapter.ProductsListAdapterInOrder;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;
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
        binding.rdoPayment.check(R.id.rdoCredit);
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            String voucher = intent.getStringExtra("voucher");
            if (bundle != null) {
                CartModel cartModel = (CartModel) bundle.getSerializable("cart");
                if (cartModel != null) {
                    getProductInOrder(cartModel);
                    SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
                    SharedPreferences addressPref = getSharedPreferences("addressPref", Context.MODE_PRIVATE);
                    String address = addressPref.getString("address", null);
                    String fullName = sharedPreferences.getString("fullName", "");
                    String phoneNumber = sharedPreferences.getString("phoneNumber", "");
                    Log.d("address", "onCreate: "+address+"\n"+addressPref);
                    if (address == null) {
                        binding.edtAddress.setText(null);
                    } else {
                        binding.edtAddress.setText(address);
                    }
                    binding.edtFullName.setText(fullName);
                    binding.edtPhone.setText(phoneNumber);
                    updateTotalPrice(cartModel);
                    binding.btnCheckOutCart.setOnClickListener(v -> orderOnclick(cartModel, voucher));
                    binding.btnCancel.setOnClickListener(v -> finish());
                    binding.imgChooseAddress.setOnClickListener(v -> {
                        binding.txtTitle.setVisibility(View.GONE);
                        binding.edtFullName.setVisibility(View.GONE);
                        binding.lnAddress.setVisibility(View.GONE);
                        binding.edtPhone.setVisibility(View.GONE);
                        binding.rvProduct.setVisibility(View.GONE);
                        binding.lnPayMethod.setVisibility(View.GONE);
                        binding.containerGroup.setVisibility(View.GONE);
                        binding.lnbutton.setVisibility(View.GONE);
                        binding.frOrder.setVisibility(View.VISIBLE);
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frOrder, new FragmentAddress());
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    });
                }
            }
        }

    }

    private void updateTotalPrice(CartModel cartModel) {
        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(cartModel.getCartId());
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                CartModel cartModelNew = snapshot.getValue(CartModel.class);
                if (cartModelNew != null) {
                    binding.textTotalPrice.setText(cartModelNew.getTotalPrice() + "");
                }
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }

    private void orderOnclick(CartModel cartModel, String voucher) {
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
                            //Lấy cart từ firebase
                            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference("carts").child(cartModel.getCartId());
                            cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                                    CartModel cartModelNew = snapshot.getValue(CartModel.class);
                                    if (cartModelNew != null) {
                                        binding.textTotalPrice.setText(cartModelNew.getTotalPrice() + "");
                                        Order order = new Order(id, address, phoneNumber, "Prepare", payment, voucher, cartModelNew, "Prepare" + cartModel.getAccountId());
                                        newOrderRef.setValue(order);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(OrderActivity.this);
                                        builder.setTitle("Thông báo");
                                        builder.setMessage("Thao tác thành công");
                                        builder.setNegativeButton("OK", ((dialog, which) -> finish()));
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }

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
