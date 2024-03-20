package com.vdsl.cybermart.ProductDetail;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.R;

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

        back();

        getDetail();

        ImageView imgMinus, imgPlus;
        TextView txtQuantity;

        imgMinus = findViewById(R.id.img_minus);
        imgPlus = findViewById(R.id.img_plus);
        txtQuantity = findViewById(R.id.txt_quantity);
//        if (txtQuantity.getText().toString())

    }

    private void back() {
        ImageView btnBack = findViewById(R.id.container_back);
        btnBack.setOnClickListener(v -> finish());
    }

    @SuppressLint("SetTextI18n")
    private void getDetail() {
        Intent intent = getIntent();
        String productName = intent.getStringExtra("productName");
        String productImage = intent.getStringExtra("productImage");
        double productPrice = intent.getDoubleExtra("productPrice", 0.0);
        String productDescription = intent.getStringExtra("productDescription");

        TextView productNameTextView = findViewById(R.id.product_title);
        ImageView productImageView = findViewById(R.id.product_image);
        TextView productPriceTextView = findViewById(R.id.product_price);
        TextView productDescriptionTextView = findViewById(R.id.product_description);

        productNameTextView.setText(productName);
        Picasso.get().load(productImage).into(productImageView);
        productPriceTextView.setText("$ " + productPrice);
        productDescriptionTextView.setText(productDescription);
    }
}