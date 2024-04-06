package com.vdsl.cybermart.Voucher.Adapter;

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

public class VoucherListAdapter extends FirebaseRecyclerAdapter<Voucher,VoucherListAdapter.ViewHolderVoucher> {
    private List<Voucher> voucherList;

    Context context;

    private FirebaseRecyclerOptions<Voucher> options;

    /*public VoucherListAdapter(@NonNull FirebaseRecyclerOptions<Voucher> options) {
        super(options);
    }*/

    public VoucherListAdapter(@NonNull FirebaseRecyclerOptions<Voucher> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolderVoucher viewHolderVoucher, int i, @NonNull Voucher voucher) {
        Log.e("VoucherListAdapter", "Binding Voucher at position " + i + ": " + voucher.toString());
        viewHolderVoucher.bind(voucher);

        viewHolderVoucher.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(i, voucher);
            }
        });

        viewHolderVoucher.binding.btnVoucher.setOnClickListener(v -> {
            String voucherCode = voucher.getCode();
            Intent intent = new Intent(context, CartActivity.class);
            intent.putExtra("voucherCode", voucherCode);
            context.startActivity(intent);
        });
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
            return getItem(position);
        }
        return null;
    }


}
