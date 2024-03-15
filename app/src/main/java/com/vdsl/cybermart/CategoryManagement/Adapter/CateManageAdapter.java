package com.vdsl.cybermart.CategoryManagement.Adapter;

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
import com.vdsl.cybermart.databinding.ItemCategoryManagementBinding;

public class CateManageAdapter extends FirebaseRecyclerAdapter<CategoryModel, CateManageAdapter.CateManageViewHolder> {

    public CateManageAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CateManageViewHolder cateManageViewHolder, int i, @NonNull CategoryModel categoryModel) {
        cateManageViewHolder.bind(categoryModel.getTitle(), categoryModel.getImage());

    }

    @NonNull
    @Override
    public CateManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCategoryManagementBinding binding = ItemCategoryManagementBinding.inflate(layoutInflater, parent, false);
        View view = binding.getRoot();
        return new CateManageAdapter.CateManageViewHolder(view, binding);
    }

    public static class CateManageViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryManagementBinding cateManaBinding;

        public CateManageViewHolder(@NonNull View itemView, ItemCategoryManagementBinding cateManaBinding) {
            super(itemView);
            this.cateManaBinding = cateManaBinding;
        }

        public void bind(String categoryName, String categoryImage) {
            cateManaBinding.titleCategory.setText(categoryName);
            Picasso.get().load(categoryImage).into(cateManaBinding.imgCategory);
        }
    }
}
