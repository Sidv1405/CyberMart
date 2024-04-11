package com.vdsl.cybermart.Product.View;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.Cart.View.CartActivity;
import com.vdsl.cybermart.Favourite.Model.FavoriteModel;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductDetailActivity extends AppCompatActivity {
    TextView productNameTextView;
    ImageView productImageView;
    TextView productPriceTextView;
    TextView productDescriptionTextView;
    ImageView imgHeart;


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

// add to cart
        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnAddToCart.setOnClickListener(v -> {
            DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("carts");

            SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
            String accountId = sharedPreferences.getString("ID", "");
            String proName = productNameTextView.getText().toString();
            Log.d("TAG", "onDataChange:proName " + proName);
            Query query = FirebaseDatabase.getInstance().getReference("products").orderByChild("name").equalTo(proName);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            ProductModel curPro = s.getValue(ProductModel.class);
                            if (curPro != null && curPro.getName().equals(proName)) {
                                Log.d("TAG", "onDataChange:curPro " + curPro.getProdId());
                                double productPrice = Double.parseDouble(productPriceTextView.getText().toString().substring(1));
                                int quantity = Integer.parseInt(txtQuantity.getText().toString());

                                ProductModel productDetail = new ProductModel(curPro.getProdId(), proName, productPrice, quantity, productImage);
                                productDetail.setImage(productImage);

                                double totalMoney = productPrice * quantity;
                                Date currentDate = new Date();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                                String date = dateFormat.format(currentDate);
                                Log.d("TAG", "onCreate: " + date);

                                cartRef.orderByChild("accountId").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                                                String cartId = cartSnapshot.getKey();
                                                CartModel existingCart = cartSnapshot.getValue(CartModel.class);
                                                if (existingCart != null) {
                                                    if (existingCart.getCartDetail() != null) {
                                                        if (!existingCart.getCartDetail().containsKey(productDetail.getName())) {
                                                            existingCart.getCartDetail().put(productDetail.getName(), productDetail);
                                                            existingCart.setTotalPrice(existingCart.getTotalPrice() + totalMoney);
                                                        } else {
                                                            ProductModel existingProduct = existingCart.getCartDetail().get(productDetail.getName());
                                                            existingProduct.setQuantity(existingProduct.getQuantity() + quantity);
                                                            existingCart.setTotalPrice(existingCart.getTotalPrice() + totalMoney);
                                                        }
                                                    } else {
                                                        Map<String, ProductModel> cartDetail = new HashMap<>();
                                                        cartDetail.put(productDetail.getName(), productDetail);
                                                        existingCart.setCartDetail(cartDetail);
                                                        existingCart.setTotalPrice(totalMoney);
                                                    }

                                                    String cartDetailName = "cartDetail_" + accountId;
                                                    SharedPreferences cartSharedPreferences = getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
                                                    SharedPreferences.Editor cartEditor = cartSharedPreferences.edit();
                                                    cartEditor.putString("id", cartId);
                                                    cartEditor.apply();

                                                    cartRef.child(cartId).setValue(existingCart);

                                                    General.showSuccessPopup(ProductDetailActivity.this, "Successfully", "Add successfully product to cart!", new OnDialogButtonClickListener() {
                                                        @Override
                                                        public void onDismissClicked(Dialog dialog) {
                                                            super.onDismissClicked(dialog);
                                                            finish();
                                                        }
                                                    });
                                                }
                                            }
                                        } else {
                                            DatabaseReference newCartRef = cartRef.push();
                                            String cartId = newCartRef.getKey();
                                            Map<String, ProductModel> cartDetail = new HashMap<>();
                                            cartDetail.put(productDetail.getName(), productDetail);

                                            CartModel cartModel = new CartModel(cartId, accountId, cartDetail, totalMoney, date);

                                            String cartDetailName = "cartDetail_" + accountId;
                                            SharedPreferences cartSharedPreferences = getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor cartEditor = cartSharedPreferences.edit();
                                            cartEditor.putString("id", cartId);
                                            cartEditor.apply();

                                            newCartRef.setValue(cartModel);
                                            General.showSuccessPopup(ProductDetailActivity.this, "Successfully", "Add successfully product to cart!", new OnDialogButtonClickListener() {
                                                @Override
                                                public void onDismissClicked(Dialog dialog) {
                                                    super.onDismissClicked(dialog);
                                                    finish();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        //    add to favorites

        imgHeart = findViewById(R.id.ic_heart);
        SharedPreferences sharedPreferences = getSharedPreferences("Users", MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        imgHeart.setOnClickListener(v -> {
            DatabaseReference favRef = FirebaseDatabase.getInstance().getReference().child("favorites");

            ProductModel productDetailF = new ProductModel(productNameTextView.getText().toString(), productDescription.toString(), Double.parseDouble(productPriceTextView.getText().toString().substring(1)), productImage);
            productDetailF.setImage(productImage);
            Map<String, ProductModel> listFav = new HashMap<>();
            listFav.put(productDetailF.getName(), productDetailF);

            favRef.orderByChild("accountId").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot favoritesSnapshot : snapshot.getChildren()) {
                            String favoritesId = favoritesSnapshot.getKey();
                            FavoriteModel existingFavorites = favoritesSnapshot.getValue(FavoriteModel.class);

                            Map<String, ProductModel> listFav = existingFavorites.getListFavorites();
                            if (listFav == null) {
                                listFav = new HashMap<>();
                                existingFavorites.setListFavorites(listFav);
                            }

                            if (listFav.containsKey(productDetailF.getName())) {
                                listFav.remove(productDetailF.getName());
                                imgHeart.setImageDrawable(getDrawable(R.drawable.ic_heart_black));
                                favRef.child(favoritesId).setValue(existingFavorites);
                                General.showSuccessPopup(ProductDetailActivity.this, "Remove Successfully", "Remove successfully product from favourites!", new OnDialogButtonClickListener() {
                                    @Override
                                    public void onDismissClicked(Dialog dialog) {
                                        super.onDismissClicked(dialog);
                                    }
                                });
                            } else {
                                listFav.put(productDetailF.getName(), productDetailF);
                                imgHeart.setImageDrawable(getDrawable(R.drawable.ic_heart_red));
                                General.showSuccessPopup(ProductDetailActivity.this, "Add Successfully", "Add successfully product to favourites!", new OnDialogButtonClickListener() {
                                    @Override
                                    public void onDismissClicked(Dialog dialog) {
                                        super.onDismissClicked(dialog);
                                    }
                                });
                            }
                            String favoritesDetailName = "favoritesDetail_" + accountId;
                            SharedPreferences favoritesSharedPreferences = getSharedPreferences(favoritesDetailName, Context.MODE_PRIVATE);
                            SharedPreferences.Editor favoritesEditor = favoritesSharedPreferences.edit();
                            favoritesEditor.putString("favoritesId", favoritesId);
                            favoritesEditor.apply();

                            favRef.child(favoritesId).setValue(existingFavorites);

                            break;
                        }
                    } else {
                        DatabaseReference newFavoritesRef = favRef.push();
                        String favoritesId = newFavoritesRef.getKey();
                        FavoriteModel favoritesModel = new FavoriteModel(favoritesId, accountId, listFav);

                        String favoritesDetailName = "favoritesDetail_" + accountId;
                        SharedPreferences favoritesSharedPreferences = getSharedPreferences(favoritesDetailName, Context.MODE_PRIVATE);
                        SharedPreferences.Editor favoritesEditor = favoritesSharedPreferences.edit();
                        favoritesEditor.putString("favoritesId", favoritesId);
                        favoritesEditor.apply();

                        newFavoritesRef.setValue(favoritesModel);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        String favoritesDetailName = "favoritesDetail_" + accountId;
        SharedPreferences sharedPreferences2 = getSharedPreferences(favoritesDetailName, MODE_PRIVATE);
        String favoritesId = sharedPreferences2.getString("favoritesId", "");

        DatabaseReference favRef = FirebaseDatabase.getInstance().getReference()
                .child("favorites")
                .child(favoritesId)
                .child("listFavorites")
                .child(productNameTextView.getText().toString());

        favRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imgHeart.setImageDrawable(getDrawable(R.drawable.ic_heart_red));
                } else {
                    imgHeart.setImageDrawable(getDrawable(R.drawable.ic_heart_black));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void back() {
        ImageView btnBack = findViewById(R.id.container_back);
        btnBack.setOnClickListener(v -> finish());
    }


}