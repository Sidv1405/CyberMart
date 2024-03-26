package com.vdsl.cybermart.Product.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Product.View.ProductDetailActivity;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.ItemProductBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends FirebaseRecyclerAdapter<ProductModel, ProductAdapter.ProdViewHolder> {
    private final List<ProductModel> visibleItems = new ArrayList<>();

    public ProductAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options) {
        super(options);
        updateVisibleItems();
    }

    @Override
    protected void onBindViewHolder(@NonNull ProdViewHolder prodViewHolder, int i, @NonNull ProductModel model) {
        ProductModel productModel = visibleItems.get(i);

        prodViewHolder.bind(productModel.getName(), productModel.getImage(), productModel.getPrice(), productModel.getStatus());

        viewProductDetail(prodViewHolder, i);
    }

    private void viewProductDetail(@NonNull ProductAdapter.ProdViewHolder prodViewHolder, int i) {
        prodViewHolder.itemView.setOnClickListener(v -> {
            ProductModel clickedItem = getItem(i);
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("productName", clickedItem.getName());
            intent.putExtra("productImage", clickedItem.getImage());
            intent.putExtra("productPrice", clickedItem.getPrice());
            intent.putExtra("productDescription", clickedItem.getDescription());
            v.getContext().startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ProdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding productBinding = ItemProductBinding.inflate(layoutInflater, parent, false);
        View view = productBinding.getRoot();
        return new ProdViewHolder(view, productBinding);
    }

    @Override
    public int getItemCount() {
        return visibleItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDataChanged() {
        super.onDataChanged();
        updateVisibleItems();
        notifyDataSetChanged();
    }

    private void updateVisibleItems() {
        visibleItems.clear();
        for (ProductModel item : getSnapshots()) {
            if (item.getStatus()) {
                visibleItems.add(item);
            }
        }
    }

    public static class ProdViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding productBinding;

        public ProdViewHolder(@NonNull View itemView, ItemProductBinding productBinding) {
            super(itemView);
            this.productBinding = productBinding;
        }

        public void bind(String productName, String productImage, Double productPrice, boolean status) {
            productBinding.nameProduct.setText(productName);
            Picasso.get().load(productImage).into(productBinding.imgProduct);
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", productPrice);
            productBinding.priceProduct.setText(String.format("%s $", formattedPrice));

            if (!status) {
                itemView.setVisibility(View.GONE);
            } else {
                itemView.setVisibility(View.VISIBLE);
            }
        }
    }
}
