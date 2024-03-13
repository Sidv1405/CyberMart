package com.vdsl.cybermart.Category;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.play.integrity.internal.b;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.ItemCategoryElementBinding;

import java.util.List;

public class Category_ElementAdapter extends RecyclerView.Adapter<Category_ElementAdapter.CategoryElementViewHolder> {

    private List<Category_Element> list;
    private int selectedItemPosition = RecyclerView.NO_POSITION; // Chỉ số của item được chọn, ban đầu không có item nào được chọn

    public void setData(List<Category_Element> list){
        this.list = list;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryElementBinding binding = ItemCategoryElementBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new CategoryElementViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryElementViewHolder holder, int position) {
        Category_Element category = list.get(position);

        holder.binding.imgClothes.setImageResource(category.getImage());
        holder.binding.titleClothes.setText(category.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(position);
                }

                if (selectedItemPosition != RecyclerView.NO_POSITION) {
                    Category_Element previouslySelectedItem = list.get(selectedItemPosition);
                    previouslySelectedItem.setSelected(false);
                    notifyItemChanged(selectedItemPosition);
                }

                selectedItemPosition = position;
                category.setSelected(true);
                notifyItemChanged(position);
            }
        });

        if (category.isSelected()) {
            holder.binding.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
            holder.binding.titleClothes.setTextColor(Color.parseColor("white"));

        } else {
            holder.binding.cardView.setCardBackgroundColor(Color.WHITE);
            holder.binding.titleClothes.setTextColor(Color.parseColor("#242424")); // Đặt màu văn bản mặc định cho tiêu đề
        }
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return  list.size();
        }
        return 0;
    }

    public static class CategoryElementViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryElementBinding binding;
        public CategoryElementViewHolder(ItemCategoryElementBinding binding) {
            super(binding.getRoot());
            this.binding= binding;
        }
    }
}
