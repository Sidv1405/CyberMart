package com.vdsl.cybermart.Product.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProductDetailActivity extends AppCompatActivity {
    TextView productNameTextView;
    ImageView productImageView;
    TextView productPriceTextView;
    TextView productDescriptionTextView;


    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        productNameTextView = findViewById(R.id.product_title);
        productImageView = findViewById(R.id.product_image);
        productPriceTextView = findViewById(R.id.product_price);
        productDescriptionTextView = findViewById(R.id.product_description);

        double productPrice;
        String productName;
        String productDescription;
        String productImage;
        Intent intent = getIntent();
        productName = intent.getStringExtra("productName");
        productImage = intent.getStringExtra("productImage");
        productPrice = intent.getDoubleExtra("productPrice", 0.0);
        productDescription = intent.getStringExtra("productDescription");

        productNameTextView.setText(productName);
        Picasso.get().load(productImage).into(productImageView);
        productPriceTextView.setText("$ " + productPrice);
        productDescriptionTextView.setText(productDescription);

        back();

        ImageView imgMinus, imgPlus;
        TextView txtQuantity;

        imgMinus = findViewById(R.id.img_minus);
        imgPlus = findViewById(R.id.img_plus);
        txtQuantity = findViewById(R.id.txt_quantity);

        imgMinus.setOnClickListener(v -> {
            int count = Integer.parseInt(txtQuantity.getText().toString());
            count -= 1;
            if (count <= 1) {
                txtQuantity.setText("01");
            } else if (count < 10) {
                txtQuantity.setText("0" + count);
            } else {
                txtQuantity.setText(String.valueOf(count));
            }
        });
        imgPlus.setOnClickListener(v -> {
            int count = Integer.parseInt(txtQuantity.getText().toString());
            count += 1;
            if (count < 10) {
                txtQuantity.setText("0" + count);
            } else {
                txtQuantity.setText(String.valueOf(count));
            }
        });

        ImageView imgHeart = findViewById(R.id.ic_heart);
        AtomicBoolean check = new AtomicBoolean(false);
        imgHeart.setOnClickListener(v -> {
            if (!check.get()) {
                imgHeart.setImageDrawable(getDrawable(R.drawable.ic_heart_red));
                check.set(true);
            } else {
                imgHeart.setImageDrawable(getDrawable(R.drawable.ic_heart_black));
                check.set(false);
            }
        });

// add to cart
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(v -> {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts");

            SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
            String accountId = sharedPreferences.getString("ID", "");

            ProductModel productDetail = new ProductModel(productNameTextView.getText().toString(), Double.parseDouble(productPriceTextView.getText().toString().substring(1)), Integer.parseInt(txtQuantity.getText().toString()), productImage);
            productDetail.setImage(productImage);
            Map<String, ProductModel> cartDetail = new HashMap<>();

            cartDetail.put(productDetail.getName(), productDetail);

            double totalMoney = productDetail.getPrice() * productDetail.getQuantity();

            Date currentDate = new Date();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = dateFormat.format(currentDate);

            cartRef.orderByChild("accountId").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            String cartId = cartSnapshot.getKey();
                            CartModel existingCart = cartSnapshot.getValue(CartModel.class);

                            existingCart.getCartDetail().put(productDetail.getName(), productDetail);

                            existingCart.setTotalPrice(existingCart.getTotalPrice() + totalMoney);
                            SharedPreferences sharedPreferences = getSharedPreferences("cartDetail", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("id", cartId);
                            editor.apply();

                            cartRef.child(cartId).setValue(existingCart);
                            finish();
                            break;
                        }
                    } else {
                        DatabaseReference newCartRef = cartRef.push();
                        String cartId = newCartRef.getKey();
                        CartModel cartModel = new CartModel(cartId, accountId, cartDetail, totalMoney, date);
                        SharedPreferences sharedPreferences = getSharedPreferences("cartDetail", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("id", cartId);
                        editor.apply();

                        newCartRef.setValue(cartModel);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDetail() {

    }

    private void back() {
        ImageView btnBack = findViewById(R.id.container_back);
        btnBack.setOnClickListener(v -> finish());
    }

}