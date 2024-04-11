package com.vdsl.cybermart.Cart.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Cart.View.CartActivity;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.ItemCartDetailBinding;

import java.util.HashMap;
import java.util.Locale;

public class CartAdapter extends FirebaseRecyclerAdapter<ProductModel, CartAdapter.CartViewHolder> {
    private final SharedPreferences sharedPreferences;
    private final TotalPriceListener totalPriceListener;
    private final Context mContext;

    private  final String voucherCode;

    private double oldCartPrice = 0;
    private double cartDiscount = 0;
    boolean discountChecked = false;

    public CartAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options, Context context, TotalPriceListener totalPriceListener,String voucherCode) {
        super(options);
        sharedPreferences = context.getSharedPreferences("Users", Context.MODE_PRIVATE);
        mContext = context;
        this.totalPriceListener = totalPriceListener;
        this.voucherCode = voucherCode;
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull CartViewHolder cartViewHolder, int i, @NonNull ProductModel productModel) {

        cartViewHolder.bind(productModel);

        SharedPreferences preferences = mContext.getSharedPreferences("price",MODE_PRIVATE);
        String  discount =preferences.getString("discount","");
        String price =preferences.getString("oldPrice","");
        /*String voucherCode =preferences.getString("voucherCode","");*/




        cartViewHolder.binding.imgDelete.setOnClickListener(v -> {
            if (!discountChecked && !discount.isEmpty()){
                cartDiscount = Double.parseDouble(discount);
                discountChecked = true;
                Log.e("check58", "onBindViewHolder: " + oldCartPrice  + discountChecked);
            }
            DatabaseReference databaseReference = getRef(i);
            databaseReference.removeValue();
            double oldPrice = (productModel.getPrice() * productModel.getQuantity()) * (1-cartDiscount);

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
                    DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");

                    totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            double totalPrice = snapshot.getValue(Double.class);
                            Log.e("check58", "onDataChange: " +  totalPrice + " " + oldPrice);
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

            Log.d("check52", "onBindViewHolder: " + price + voucherCode + discount);

            if (!discountChecked && !discount.isEmpty()){
                oldCartPrice = Double.parseDouble(price);
                cartDiscount = Double.parseDouble(discount);
                discountChecked = true;
                Log.e("check58", "onBindViewHolder: " + oldCartPrice  + discountChecked);
            }

            double oldPrice = productModel.getPrice();
            DatabaseReference databaseReference = getRef(i);
            databaseReference.child("quantity").setValue(count);
            String accountId = sharedPreferences.getString("ID", "");
            String cartDetailName = "cartDetail_" + accountId;
            SharedPreferences cartSharedPreferences = mContext.getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
            String cartId = cartSharedPreferences.getString("id", "");
            DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");
            if (cartItemClickListener != null) {
                cartItemClickListener.onPlusClicked(productModel,oldPrice);
            }

            oldCartPrice += oldPrice;
            Log.e("check57", "onBindViewHolder: " + oldCartPrice );
            totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    double totalPrice = snapshot.getValue(Double.class);
                    /*totalPriceRef.setValue(totalPrice + oldPrice);*/

                    if (voucherCode != null && !voucherCode.isEmpty()){
                        double totalCartPrice = (oldCartPrice) * (1 - cartDiscount);
                        totalPriceRef.setValue(totalCartPrice);
                        totalPriceListener.onTotalPriceUpdated(totalCartPrice);
                    }else{
                        totalPriceRef.setValue(totalPrice + oldPrice);
                        totalPriceListener.onTotalPriceUpdated(totalPrice + oldPrice);
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }

            });
            Log.e("wenchala", "onDataChange: " + oldCartPrice + " " + oldPrice);
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

                if (!discountChecked && !discount.isEmpty()){
                    oldCartPrice = Double.parseDouble(price);
                    cartDiscount = Double.parseDouble(discount);
                    discountChecked = true;
                    Log.e("check58", "onBindViewHolder: " + oldCartPrice  + discountChecked);
                }

                double oldPrice = productModel.getPrice();
                DatabaseReference databaseReference = getRef(i);
                databaseReference.child("quantity").setValue(count);
                String accountId = sharedPreferences.getString("ID", "");
                String cartDetailName = "cartDetail_" + accountId;
                SharedPreferences cartSharedPreferences = mContext.getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
                String cartId = cartSharedPreferences.getString("id", "");
                DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");
                if (cartItemClickListener != null) {
                    cartItemClickListener.onMinusClicked(productModel,oldPrice);
                }

                oldCartPrice -= oldPrice;
                Log.e("check57", "onBindViewHolder: " + oldCartPrice );
                totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        double totalPrice = snapshot.getValue(Double.class);
                        if (voucherCode != null && !voucherCode.isEmpty()){
                            double totalCartPrice = (oldCartPrice) * (1 - cartDiscount);
                            totalPriceRef.setValue(totalCartPrice);
                            totalPriceListener.onTotalPriceUpdated(totalCartPrice);
                        }else{
                            totalPriceRef.setValue(totalPrice + oldPrice);
                            totalPriceListener.onTotalPriceUpdated(totalPrice + oldPrice);
                        }
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
    public interface CartItemClickListener {
        void onPlusClicked(ProductModel productModel,double oldPrice);
        void onMinusClicked(ProductModel productModel,double oldPrice);
    }

    private CartItemClickListener cartItemClickListener;

    public void setCartItemClickListener(CartItemClickListener listener) {
        this.cartItemClickListener = listener;
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

    private void updateTotalPriceAndMarkVoucherUsed(DatabaseReference totalPriceRef, DatabaseReference userVouchersRef, String voucherCode, double discount) {
        totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@android.support.annotation.NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                   /* String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalCartPrice);
                    txtTotalPrice.setText(String.format("%s $", formattedPrice));
                    General.showSuccessPopup(mContext, "Sử Dụng Voucher", "Sử Dụng Voucher thành công", new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);
                        }
                    });*/

                    // Đánh dấu voucher đã sử dụng
                    /*userVouchersRef.child(voucherCode).setValue(true);*/
                }
            }

            @Override
            public void onCancelled(@android.support.annotation.NonNull DatabaseError error) {
            }
        });
    }
}