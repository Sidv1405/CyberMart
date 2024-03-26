package com.vdsl.cybermart.Cart.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.ItemCartDetailBinding;

import java.util.HashMap;
import java.util.Locale;

public class CartAdapter extends FirebaseRecyclerAdapter<ProductModel, CartAdapter.CartViewHolder> {
    private final SharedPreferences sharedPreferences;
    private final TotalPriceListener totalPriceListener;
    private final Context mContext;


    public CartAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context, TotalPriceListener totalPriceListener) {
        super(options);
        sharedPreferences = context.getSharedPreferences("Users", Context.MODE_PRIVATE);
        mContext = context;
        this.totalPriceListener = totalPriceListener;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull ProductModel productModel) {

        cartViewHolder.bind(productModel);

        cartViewHolder.binding.imgDelete.setOnClickListener(v -> {
            DatabaseReference databaseReference = getRef(i);
            databaseReference.removeValue();
            double oldPrice = productModel.getPrice() * productModel.getQuantity();

            String accountId = sharedPreferences.getString("ID", "");
            String cartDetailName = "cartDetail_" + accountId;
            SharedPreferences cartSharedPreferences = mContext.getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
            String cartId = cartSharedPreferences.getString("id", "");
            DatabaseReference cartDetailRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/cartDetail");
            cartDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // cartDetail exists, do nothing
                    } else {
                        cartDetailRef.setValue(new HashMap<>());
                    }

                    // Update total price
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
            String accountId = sharedPreferences.getString("ID", "");
            String cartDetailName = "cartDetail_" + accountId;
            SharedPreferences cartSharedPreferences = mContext.getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
            String cartId = cartSharedPreferences.getString("id", "");
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
                String accountId = sharedPreferences.getString("ID", "");
                String cartDetailName = "cartDetail_" + accountId;
                SharedPreferences cartSharedPreferences = mContext.getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
                String cartId = cartSharedPreferences.getString("id", "");
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

        @SuppressLint("SetTextI18n")
        public void bind(ProductModel productModel) {
            binding.cartProdName.setText(productModel.getName());
            Picasso.get().load(productModel.getImage()).into(binding.cartProdImg);
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", productModel.getPrice());
            binding.cartProdPrice.setText(String.format("%s $", formattedPrice));
            if (productModel.getQuantity() >= 0 && productModel.getQuantity() < 10) {
                binding.cartProdQuantity.setText("0" + productModel.getQuantity());
            } else {
                binding.cartProdQuantity.setText(String.valueOf(productModel.getQuantity()));
            }
        }
    }
}