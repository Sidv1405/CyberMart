package com.vdsl.cybermart.Cart.View;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Cart.Adapter.CartAdapter;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.Order.OrderActivity;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.ActivityCartBinding;

import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.TotalPriceListener {
    CartModel cart;
    ImageView btnBack;
    Button btnCheckOut;
    RadioGroup radioGroup;
    RadioButton rdoCash, rdoCredit;
    ActivityCartBinding binding;
    private CartAdapter adapter;
    private TextView txtTotalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViews();
        readData();

        btnBack.setOnClickListener(v -> finish());
        //thanh toán tạo hóa đơn
        btnCheckOut.setOnClickListener(v -> {
            if (cart.getCartDetail() != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("cart", cart);
                Intent intent = new Intent(this, OrderActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo");
                builder.setMessage("Giỏ hàng đang trống");
                builder.setNegativeButton("OK", ((dialog, which) -> {
                }));
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
    }


    private void readData() {
        RecyclerView rcvCart = findViewById(R.id.rcv_cart);
        rcvCart.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        String cartDetailName = "cartDetail_" + accountId;
        SharedPreferences cartSharedPreferences = getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
        String cartId = cartSharedPreferences.getString("id", "");

        adapter = new CartAdapter(new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("carts").child(cartId).child("cartDetail"), ProductModel.class)
                .build(), this, this);
        rcvCart.setAdapter(adapter);

        txtTotalPrice = findViewById(R.id.text_total_price);
        DatabaseReference cartsRef = FirebaseDatabase.getInstance().getReference().child("carts");
        cartsRef.orderByChild("accountId").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalCartPrice = 0.0;
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    cart = cartSnapshot.getValue(CartModel.class);
                    if (cart != null) {
                        totalCartPrice += cart.getTotalPrice();
                    }
                }
                String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalCartPrice);
                txtTotalPrice.setText(String.format("%s $", formattedPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onTotalPriceUpdated(double totalPriceSum) {
        String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalPriceSum);
        txtTotalPrice.setText(String.format("%s $", formattedPrice));
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readData();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void findViews() {
        btnBack = findViewById(R.id.c_ic_back);
        btnCheckOut = findViewById(R.id.btn_check_out_cart);
        radioGroup = findViewById(R.id.rdoPayment);
        rdoCash = findViewById(R.id.rdoCash);
        rdoCredit = findViewById(R.id.rdoCredit);
    }
}
