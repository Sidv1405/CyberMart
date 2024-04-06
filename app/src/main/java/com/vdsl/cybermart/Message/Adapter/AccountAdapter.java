package com.vdsl.cybermart.Message.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.vdsl.cybermart.databinding.ItemUserMessageBinding;

import java.util.ArrayList;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolderAccount> {

    Context context;

    ArrayList<UserModel> list;
    private boolean isChat;
    String last_MSG;

    public AccountAdapter(Context context, ArrayList<UserModel> list, boolean isChat) {
        this.context = context;
        this.list = list;
        this.isChat = isChat;
    }

    /*@Override
    protected void onBindViewHolder(@NonNull ViewHolderAccount viewHolderAccount, int i, @NonNull UserModel userModel) {
        Log.e("AccountAdapter", "Binding User at position " + i + ": " + userModel.toString());
        viewHolderAccount.bind(userModel);

        viewHolderAccount.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userEmail",userModel.getEmail());
            context.startActivity(intent);
        });
    }*/

    @NonNull
    @Override
    public ViewHolderAccount onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("AccountAdapter", "onCreateViewHolder called");
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemUserMessageBinding binding = ItemUserMessageBinding.inflate(inflater, parent, false);
        return new ViewHolderAccount(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderAccount holder, int position) {

        UserModel user = list.get(position);
        holder.bind(user);
        Log.e("AccountAdapter", "Binding User at position " + position + ": " + user.toString());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("userEmail",user.getEmail());
            intent.putExtra("fcmToken",user.getFcmToken());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("check36", "onBindViewHolder: " + user.getEmail());
            context.startActivity(intent);
        });

        if (isChat){
            lastMessage(user.getEmail(),holder.binding.txtMessage);
        }else{
            holder.binding.txtMessage.setVisibility(View.GONE);
        }

        if (isChat){
            if (user.getStatus().equals("online")){
                holder.binding.imgOnline.setVisibility(View.VISIBLE);
                holder.binding.imgOffline.setVisibility(View.GONE);
            }else{
                holder.binding.imgOnline.setVisibility(View.GONE);
                holder.binding.imgOffline.setVisibility(View.VISIBLE);
            }
        }else{
            holder.binding.imgOnline.setVisibility(View.GONE);
            holder.binding.imgOffline.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolderAccount extends RecyclerView.ViewHolder {

        ItemUserMessageBinding binding;

        public ViewHolderAccount(ItemUserMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(UserModel userModel) {
            binding.txtNameUser.setText(userModel.getFullName());
            if (!userModel.getAvatar().isEmpty() || userModel.getAvatar() != null){
                Picasso.get().load(userModel.getAvatar()).into(binding.imgStaff, new Callback() {
                    @Override
                    public void onSuccess() {
                        Log.d("check47", "onSuccess: " + userModel.getAvatar());
                    }

                    @Override
                    public void onError(Exception e) {
                        binding.imgStaff.setImageResource(R.drawable.img_default_profile_image);
                    }
                });
            }else {
                binding.imgStaff.setImageResource(R.drawable.img_default_profile_image);
            }
        }
    }

    public void lastMessage(String userId, TextView view){
        last_MSG = "default";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Chat chat = snapshot1.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getEmail()) && chat.getSender().equals(userId) ||
                            chat.getSender().equals(userId) && chat.getSender().equals(firebaseUser.getEmail())){
                            last_MSG = chat.getMessage();
                    }
                }
                switch (last_MSG){
                    case "default":
                        view.setText("No Message");
                        break;
                    default:
                        view.setText(last_MSG);
                        break;
                }
                last_MSG = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

