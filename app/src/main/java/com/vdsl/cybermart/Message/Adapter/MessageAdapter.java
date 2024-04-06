package com.vdsl.cybermart.Message.Adapter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.Message.MessageActivity;
import com.vdsl.cybermart.Message.Model.Chat;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.databinding.ChatItemLeftBinding;
import com.vdsl.cybermart.databinding.ChatItemRightBinding;
import com.vdsl.cybermart.databinding.ItemUserMessageBinding;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    Context context;

    ArrayList<Chat> list;

    FirebaseUser firebaseUser;

    public MessageAdapter(Context context, ArrayList<Chat> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("AccountAdapter", "onCreateViewHolder called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == MSG_TYPE_RIGHT) {
            ChatItemRightBinding binding = ChatItemRightBinding.inflate(inflater, parent, false);
            return new MessageAdapter.ViewHolder(binding);
        } else {
            ChatItemLeftBinding binding = ChatItemLeftBinding.inflate(inflater, parent, false);
            return new MessageAdapter.ViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = list.get(position);
        holder.blind(chat);
        Log.e("checkcheck", "onBindViewHolder: " + chat.isStatusSeen() );
        Log.e("checkcheck", "onBindViewHolder: " + list.size() );

        if (holder.chatItemRightBinding != null) {
            if (position == list.size() - 1) {
                if (chat.isStatusSeen()) {
                    holder.chatItemRightBinding.txtSeen.setText("Seen");
                } else {
                    holder.chatItemRightBinding.txtSeen.setText("Delivered");
                }
            } else {
                holder.chatItemRightBinding.txtSeen.setVisibility(View.GONE);
            }
        } else if (holder.chatItemLeftBinding != null){
            if (position == list.size() - 1) {
                if (chat.isStatusSeen()) {
                    holder.chatItemLeftBinding.txtSeen.setText("Seen");
                } else {
                    holder.chatItemLeftBinding.txtSeen.setText("Delivered");
                }
            } else {
                holder.chatItemLeftBinding.txtSeen.setVisibility(View.GONE);
            }
            SharedPreferences preferences = context.getSharedPreferences("user",MODE_PRIVATE);
            String avatar =preferences.getString("avatar","");
            Log.e("check50", "onBindViewHolder: " + avatar );
            if (avatar.isEmpty() || avatar != null){
                Picasso.get().load(avatar).into(holder.chatItemLeftBinding.imgProfile, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("check47", "onSuccess: " + avatar);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.chatItemLeftBinding.imgProfile.setImageResource(R.drawable.img_default_profile_image);
                    }
                });
            }else {
                holder.chatItemLeftBinding.imgProfile.setImageResource(R.drawable.img_default_profile_image);
            }


        }
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        ItemUserMessageBinding binding;
        ChatItemRightBinding chatItemRightBinding;
        ChatItemLeftBinding chatItemLeftBinding;

        public ViewHolder(ItemUserMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(ChatItemRightBinding binding) {
            super(binding.getRoot());
            this.chatItemRightBinding = binding;
        }

        public ViewHolder(ChatItemLeftBinding binding) {
            super(binding.getRoot());
            this.chatItemLeftBinding = binding;
        }

        public void blind(Chat chat) {
            if (binding != null) {
                binding.txtMessage.setText(chat.getMessage());
            } else if (chatItemRightBinding != null) {
                chatItemRightBinding.txtMessage.setText(chat.getMessage());
            } else if (chatItemLeftBinding != null) {
                chatItemLeftBinding.txtMessage.setText(chat.getMessage());
            }
        }
    }

        @Override
        public int getItemViewType(int position) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (list.get(position).getSender().equals(firebaseUser.getEmail())) {
                return MSG_TYPE_RIGHT;
            } else {
                return MSG_TYPE_LEFT;
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }
