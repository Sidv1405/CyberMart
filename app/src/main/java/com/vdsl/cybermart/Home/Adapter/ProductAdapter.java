package com.vdsl.cybermart.Home.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Home.Model.ProductModel;
import com.vdsl.cybermart.databinding.ItemProductBinding;

import java.util.Locale;

public class ProductAdapter extends FirebaseRecyclerAdapter<ProductModel, ProductAdapter.ProdViewHolder> {
    public ProductAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProdViewHolder prodViewHolder, int i, @NonNull ProductModel productModel) {
        Log.d("Productzzzzzzzz", "Title: " + productModel.getName() + ", Image: " + productModel.getImage() + ", Price: " + productModel.getPrice());
        prodViewHolder.bind(productModel.getName(), productModel.getImage(), productModel.getPrice());
    }

    @NonNull
    @Override
    public ProdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding productBinding = ItemProductBinding.inflate(layoutInflater, parent, false);
        View view = productBinding.getRoot();
        return new ProdViewHolder(view, productBinding);
    }

    public static class ProdViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding productBinding;

        public ProdViewHolder(@NonNull View itemView, ItemProductBinding productBinding) {
            super(itemView);
            this.productBinding = productBinding;
        }

        public void bind(String productName, String productImage, Double productPrice) {
            productBinding.nameProduct.setText(productName);
            Picasso.get().load(productImage).into(productBinding.imgProduct);
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", productPrice);
            productBinding.priceProduct.setText(formattedPrice);
        }
    }
}
