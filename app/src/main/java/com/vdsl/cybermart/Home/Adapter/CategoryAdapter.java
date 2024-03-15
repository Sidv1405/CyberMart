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
import com.vdsl.cybermart.Home.Model.CategoryModel;
import com.vdsl.cybermart.databinding.ItemCategoryBinding;

import java.util.ArrayList;

public class CategoryAdapter extends FirebaseRecyclerAdapter<CategoryModel,CategoryAdapter.CateViewHolder> {


    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CateViewHolder cateViewHolder, int i, @NonNull CategoryModel categoryModel) {
        Log.d("CategoryAdapterzzzzzzzz", "Title: " + categoryModel.getTitle() + ", Image: " + categoryModel.getImage());
        cateViewHolder.bind(categoryModel.getTitle(), categoryModel.getImage());
    }

    @NonNull
    @Override
    public CateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCategoryBinding categoryBinding = ItemCategoryBinding.inflate(layoutInflater, parent, false);
        View view = categoryBinding.getRoot();
        return new CateViewHolder(view, categoryBinding);
    }

    public static class CateViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding categoryBinding;

        public CateViewHolder(@NonNull View itemView, ItemCategoryBinding categoryBinding) {
            super(itemView);
            this.categoryBinding = categoryBinding;
        }

        public void bind(String categoryName, String categoryImage) {
            categoryBinding.titleCategory.setText(categoryName);
            Picasso.get().load(categoryImage).into(categoryBinding.imgCategory);
        }
    }
}
