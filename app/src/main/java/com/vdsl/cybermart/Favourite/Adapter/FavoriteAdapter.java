package com.vdsl.cybermart.Favourite.Adapter;

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
import com.vdsl.cybermart.Product.Adapter.ProductAdapter;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.Product.View.ProductDetailActivity;
import com.vdsl.cybermart.databinding.ItemFavoritesBinding;


public class FavoriteAdapter extends FirebaseRecyclerAdapter<ProductModel, FavoriteAdapter.FavViewHolder> {

    public FavoriteAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull FavViewHolder favViewHolder, int i, @NonNull ProductModel productModel) {
        favViewHolder.bind(productModel);
        viewProductDetail(favViewHolder,i);
    }
    private void viewProductDetail(@NonNull FavoriteAdapter.FavViewHolder favViewHolder, int i) {
        favViewHolder.itemView.setOnClickListener(v -> {
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
    public FavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemFavoritesBinding binding = ItemFavoritesBinding.inflate(layoutInflater, parent, false);
        View view = binding.getRoot();
        return new FavoriteAdapter.FavViewHolder(view, binding);
    }

    public static class FavViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoritesBinding binding;

        public FavViewHolder(@NonNull View itemView, ItemFavoritesBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        @SuppressLint("SetTextI18n")
        public void bind(ProductModel productModel) {
            binding.favProdName.setText(productModel.getName());
            Picasso.get().load(productModel.getImage()).into(binding.favProdImg);
        }
    }
}
