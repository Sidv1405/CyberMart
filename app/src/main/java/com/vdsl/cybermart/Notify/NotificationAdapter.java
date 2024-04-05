package com.vdsl.cybermart.Notify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.cybermart.databinding.ItemNotificationBinding;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private List<NotifyModel> list;

    private Context context;

    private int type;

    public NotificationAdapter(List<NotifyModel> list) {
        this.list = list;
    }

    public NotificationAdapter(List<NotifyModel> list, Context context, int type) {
        this.list = list;
        this.context = context;
        this.type = type;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(inflater, parent, false);
        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotifyModel notifyModel = list.get(position);
        holder.bind(notifyModel);
        if (type == 1){
            holder.binding.txtTitle.setText(notifyModel.getTitle() + " đã gửi bạn một tin nhắn");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        ItemNotificationBinding binding;

        public NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NotifyModel notifyModel){
            binding.txtMessage.setText(notifyModel.getBody());
        }
    }

}
