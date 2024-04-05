package com.vdsl.cybermart.Order.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.ItemProductInDetailOrderBinding;

public class ProductsListAdapterInOrder extends FirebaseRecyclerAdapter<ProductModel, ProductsListAdapterInOrder.ViewHolder> {

    public ProductsListAdapterInOrder(@NonNull FirebaseRecyclerOptions<ProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull ProductModel productModel) {
        viewHolder.bind(productModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemProductInDetailOrderBinding binding = ItemProductInDetailOrderBinding.inflate(inflater,parent,false );
        return new ViewHolder(binding);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemProductInDetailOrderBinding binding;
        public ViewHolder(@NonNull ItemProductInDetailOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        @SuppressLint("SetTextI18n")
        public void bind(ProductModel productModel){
            Picasso.get().load(productModel.getImage()).into(binding.imgProduct);
            binding.nameProduct.setText(productModel.getName());
            binding.priceProduct.setText(productModel.getPrice()+"");
            binding.quantity.setText(productModel.getQuantity()+"");
        }
    }
}
