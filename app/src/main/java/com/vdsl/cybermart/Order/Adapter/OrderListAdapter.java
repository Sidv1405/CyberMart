package com.vdsl.cybermart.Order.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.databinding.ItemOrderRowBinding;

public class OrderListAdapter extends FirebaseRecyclerAdapter<Order, OrderListAdapter.ViewHolder> {
    private final OnDetailListener listener;
    public OrderListAdapter(@NonNull FirebaseRecyclerOptions<Order> options,OnDetailListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder viewHolder, int i, @NonNull Order order) {
        viewHolder.bind(order,listener);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemOrderRowBinding binding= ItemOrderRowBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

   public static class ViewHolder extends RecyclerView.ViewHolder{
        ItemOrderRowBinding binding;
        public ViewHolder(@NonNull ItemOrderRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Order order,OnDetailListener listener) {
            binding.seriNumber.setText(order.getSeri());
            binding.date.setText(order.getCartModel().getDate());
            binding.status.setText(order.getStatus());
            binding.btnDetail.setOnClickListener(v -> {
                listener.onOrderClick(order);
            });
        }
    }
    public interface OnDetailListener {
        void onOrderClick(Order order);
    }
}
