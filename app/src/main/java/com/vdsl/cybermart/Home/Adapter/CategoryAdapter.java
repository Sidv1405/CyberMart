package com.vdsl.cybermart.Home.Adapter;

import android.annotation.SuppressLint;
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
import java.util.List;

public class CategoryAdapter extends FirebaseRecyclerAdapter<CategoryModel, CategoryAdapter.CateViewHolder> {

    private final List<CategoryModel> visibleItems = new ArrayList<>();

    public CategoryAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options) {
        super(options);
        updateVisibleItems();
    }

    @Override
    protected void onBindViewHolder(@NonNull CateViewHolder holder, int position, @NonNull CategoryModel model) {
        CategoryModel categoryModel = visibleItems.get(position);
        holder.bind(categoryModel.getTitle(), categoryModel.getImage(), categoryModel.isStatus());
    }

    @NonNull
    @Override
    public CateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCategoryBinding categoryBinding = ItemCategoryBinding.inflate(layoutInflater, parent, false);
        View view = categoryBinding.getRoot();
        return new CateViewHolder(view, categoryBinding);
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
        for (CategoryModel item : getSnapshots()) {
            if (item.isStatus()) {
                visibleItems.add(item);
            }
        }
    }

    public static class CateViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryBinding categoryBinding;

        public CateViewHolder(@NonNull View itemView, ItemCategoryBinding categoryBinding) {
            super(itemView);
            this.categoryBinding = categoryBinding;
        }

        public void bind(String categoryName, String categoryImage, boolean status) {
            categoryBinding.titleCategory.setText(categoryName);
            Picasso.get().load(categoryImage).into(categoryBinding.imgCategory);

            if (!status) {
                itemView.setVisibility(View.GONE);
            } else {
                itemView.setVisibility(View.VISIBLE);
            }
        }
    }
}