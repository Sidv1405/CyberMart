package com.vdsl.cybermart.Product.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProductDetailActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
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


        String productName, productImage, productDescription;
        double productPrice;

        getDetail();


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

//

        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(v -> {
            DatabaseReference cartRef;
            cartRef = FirebaseDatabase.getInstance().getReference().child("carts");


            SharedPreferences sharedPreferences = getSharedPreferences("Users", v.getContext().MODE_PRIVATE);
            String accountId = sharedPreferences.getString("ID", "");
            Log.d("acountIdzz", "onCreate: " + accountId);
        });

    }

    private void getDetail() {
        double productPrice;
        String productName;
        String productDescription;
        String productImage;
        Intent intent = getIntent();
        productName = intent.getStringExtra("productName");
        productImage = intent.getStringExtra("productImage");
        productPrice = intent.getDoubleExtra("productPrice", 0.0);
        productDescription = intent.getStringExtra("productDescription");


        TextView productNameTextView = findViewById(R.id.product_title);
        ImageView productImageView = findViewById(R.id.product_image);
        TextView productPriceTextView = findViewById(R.id.product_price);
        TextView productDescriptionTextView = findViewById(R.id.product_description);

        productNameTextView.setText(productName);
        Picasso.get().load(productImage).into(productImageView);
        productPriceTextView.setText("$ " + productPrice);
        productDescriptionTextView.setText(productDescription);
    }

    private void back() {
        ImageView btnBack = findViewById(R.id.container_back);
        btnBack.setOnClickListener(v -> finish());
    }
}