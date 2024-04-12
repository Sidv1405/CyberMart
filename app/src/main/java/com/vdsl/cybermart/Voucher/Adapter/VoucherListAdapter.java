package com.vdsl.cybermart.Voucher.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vdsl.cybermart.Cart.View.CartActivity;
import com.vdsl.cybermart.Order.Adapter.OrderListAdapter;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ItemOrderRowBinding;
import com.vdsl.cybermart.databinding.ItemVoucherBinding;

import java.util.List;

public class VoucherListAdapter extends RecyclerView.Adapter<VoucherListAdapter.ViewHolderVoucher> {
    private List<Voucher> voucherList;

    Context context;

    private FirebaseRecyclerOptions<Voucher> options;

    /*public VoucherListAdapter(@NonNull FirebaseRecyclerOptions<Voucher> options) {
        super(options);
    }*/

    public VoucherListAdapter(List<Voucher> voucherList, Context context) {
        this.voucherList = voucherList;
        this.context = context;
    }


    public interface OnItemClick {
        void onItemClick(int position, Voucher voucher);
    }

    private VoucherListAdapter.OnItemClick mListener;

    public void updateList(List<Voucher> newList) {
        voucherList.clear();
        voucherList.addAll(newList);
        notifyDataSetChanged();
    }

    public void setOnItemClick(VoucherListAdapter.OnItemClick listener) {
        mListener = listener;
    }
    @NonNull
    @Override
    public ViewHolderVoucher onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemVoucherBinding binding= ItemVoucherBinding.inflate(inflater,parent,false);
        return new ViewHolderVoucher(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderVoucher holder, int position) {
        Voucher voucher = voucherList.get(position);
        Log.e("VoucherListAdapter", "Binding Voucher at position " + position + ": " + voucher.toString());
        holder.bind(voucher);

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(position, voucher);
            }
        });

        holder.binding.btnVoucher.setOnClickListener(v -> {
            String voucherCode = voucher.getCode();
            Intent intent = new Intent(context, CartActivity.class);
            intent.putExtra("voucherCode", voucherCode);
            context.startActivity(intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        Log.d("VoucherListAdapter", "getItemCount: " + voucherList.size());
        return voucherList.size();
    }

    public static class ViewHolderVoucher extends RecyclerView.ViewHolder {

        ItemVoucherBinding binding;

        public ViewHolderVoucher(ItemVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
        public void bind(Voucher voucher) {
            binding.tvId.setText(voucher.getCode());
            binding.tvName.setText(voucher.getTitle());
            binding.tvPrice.setText(voucher.getDiscount() + "%");
            binding.tvDate.setText(voucher.getExpiryDate());
        }
    }

    public Voucher getVoucherAtPosition(int position) {
        if (position >= 0 && position < getItemCount()) {
            return voucherList.get(position);
        }
        return null;
    }


}
