package com.vdsl.cybermart.Cart.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;

import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.TotalPriceListener {
    private CartAdapter adapter;
    private TextView txtTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        readData();

        ImageView btnBack = findViewById(R.id.c_ic_back);
        btnBack.setOnClickListener(v -> finish());
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
                if (snapshot.exists() && snapshot.hasChildren()) {
                    double totalCartPrice = 0.0;
                    for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                        CartModel cart = cartSnapshot.getValue(CartModel.class);
                        if (cart != null) {
                            totalCartPrice += cart.getTotalPrice();
                        }
                    }
                    String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalCartPrice);
                    txtTotalPrice.setText(String.format("%s $", formattedPrice));
                } else {
                    Log.d("CartActivity", "Cart detail is empty");
                }
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
}
