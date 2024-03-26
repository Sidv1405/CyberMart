package com.vdsl.cybermart.Cart.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.ItemCartDetailBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class CartAdapter extends FirebaseRecyclerAdapter<ProductModel, CartAdapter.CartViewHolder> {
    private SharedPreferences sharedPreferences;
    private TotalPriceListener totalPriceListener;


    public CartAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context, TotalPriceListener totalPriceListener) {
        super(options);
        sharedPreferences = context.getSharedPreferences("cartDetail", Context.MODE_PRIVATE);
        this.totalPriceListener = totalPriceListener;
    }


    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull ProductModel productModel) {

        cartViewHolder.bind(productModel);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference rootReference = firebaseDatabase.getReference();
        DatabaseReference reference = rootReference.child("carts");

        cartViewHolder.binding.imgDelete.setOnClickListener(v -> {
            DatabaseReference databaseReference = getRef(i);
            databaseReference.removeValue();
            double oldPrice = productModel.getPrice() * productModel.getQuantity();
            String cartId = sharedPreferences.getString("id", "");
            DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");
            totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalPrice = snapshot.getValue(Double.class);
                    totalPriceRef.setValue(totalPrice - oldPrice);
                    totalPriceListener.onTotalPriceUpdated(totalPrice - oldPrice);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });


        cartViewHolder.binding.imgPlusCart.setOnClickListener(v -> {
            int count = Integer.parseInt(cartViewHolder.binding.cartProdQuantity.getText().toString());
            count += 1;
            if (count < 10) {
                cartViewHolder.binding.cartProdQuantity.setText("0" + count);
            } else {
                cartViewHolder.binding.cartProdQuantity.setText(String.valueOf(count));
            }
            DatabaseReference databaseReference = getRef(i);
            databaseReference.child("quantity").setValue(count);
            double oldPrice = productModel.getPrice();
            String cartId = sharedPreferences.getString("id", "");
            DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");
            totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalPrice = snapshot.getValue(Double.class);
                    totalPriceRef.setValue(totalPrice + oldPrice);
                    totalPriceListener.onTotalPriceUpdated(totalPrice + oldPrice);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        });

        cartViewHolder.binding.imgMinusCart.setOnClickListener(v -> {
            int count = Integer.parseInt(cartViewHolder.binding.cartProdQuantity.getText().toString());
            if (count > 1) {
                count -= 1;
                if (count < 10) {
                    cartViewHolder.binding.cartProdQuantity.setText("0" + count);
                } else {
                    cartViewHolder.binding.cartProdQuantity.setText(String.valueOf(count));
                }

                DatabaseReference databaseReference = getRef(i);
                databaseReference.child("quantity").setValue(count);
                double oldPrice = productModel.getPrice();
                String cartId = sharedPreferences.getString("id", "");
                DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");
                totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double totalPrice = snapshot.getValue(Double.class);
                        totalPriceRef.setValue(totalPrice - oldPrice);
                        totalPriceListener.onTotalPriceUpdated(totalPrice - oldPrice);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                cartViewHolder.binding.cartProdQuantity.setText("01");
                DatabaseReference databaseReference = getRef(i);
                databaseReference.child("quantity").setValue(1);
            }
        });
    }

    public interface TotalPriceListener {
        void onTotalPriceUpdated(double totalPriceSum);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCartDetailBinding binding = ItemCartDetailBinding.inflate(layoutInflater, parent, false);
        View view = binding.getRoot();
        return new CartViewHolder(view, binding);

    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartDetailBinding binding;

        public CartViewHolder(@NonNull View itemView, ItemCartDetailBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        int i = 0;

        public void bind(ProductModel productModel) {
            binding.cartProdName.setText(productModel.getName());
            Picasso.get().load(productModel.getImage()).into(binding.cartProdImg);
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", productModel.getPrice());
            binding.cartProdPrice.setText(String.format("%s $", formattedPrice));
            if (productModel.getQuantity() >= 0 && productModel.getQuantity() < 10) {
                binding.cartProdQuantity.setText("0" + String.valueOf(productModel.getQuantity()));
            } else {
                binding.cartProdQuantity.setText(String.valueOf(productModel.getQuantity()));
            }
        }
    }
}


