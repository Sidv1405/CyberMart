package com.vdsl.cybermart.Message;

import android.accounts.Account;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.Message.Adapter.AccountAdapter;
import com.vdsl.cybermart.Message.Model.Chat;
import com.vdsl.cybermart.Message.Model.ChatList;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentChatsBinding;
import com.vdsl.cybermart.databinding.FragmentMessageBinding;

import java.util.ArrayList;
import java.util.List;


public class ChatsFragment extends Fragment {

    AccountAdapter adapter;

    ArrayList<UserModel> mUser;

    FragmentChatsBinding binding;

    FirebaseUser firebaseUser;

    DatabaseReference reference;

    ArrayList<ChatList> userList;

    String userEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        /*reference = FirebaseDatabase.getInstance().getReference("Chats");*/
        userEmail = firebaseUser.getEmail();
        userList = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvHistory.setLayoutManager(linearLayoutManager);

        getIdFromEmail(userEmail, new FragmentMessage.OnIdReceivedListener() {
            @Override
            public void onIdReceived(String id) {
                reference = FirebaseDatabase.getInstance().getReference("ChatList").child(id);
                Log.e("check4", "onIdReceived: " + id );

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userList.clear();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            String email = snapshot1.child("email").getValue(String.class);
                            Log.e("Mytom", "onIdReceived: " + email );
                            // Tạo một đối tượng ChatList từ email và thêm vào userList
                            ChatList chatList = new ChatList(email);
                            userList.add(chatList);
                        }
                        Log.d("check3", "onDataChange: " + userList.size());
                        chatList();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void chatList() {
        mUser = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Account");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUser.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    UserModel user = snapshot1.getValue(UserModel.class);
                    for (ChatList chatList : userList){
                        Log.d("CHeck", "onDataChange: " + chatList.getId());
                        if (user.getEmail().equals(chatList.getId())){
                            mUser.add(user);
                        }
                    }
                }
                Log.d("yeh", "onDataChange: " + mUser.size() + mUser.toString());
                adapter = new AccountAdapter(getContext(),mUser,true);
                binding.rcvHistory.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void getIdFromEmail(String email, final FragmentMessage.OnIdReceivedListener listener) {
        DatabaseReference peopleReference = FirebaseDatabase.getInstance().getReference().child("Account");
        peopleReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String accountId = null;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        accountId = dataSnapshot1.getKey();
                        Log.d("TAG", "onDataChange: sdone" + accountId);
                        break;
                    }
                }
                listener.onIdReceived(accountId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onIdReceived(null);
            }
        });
    }

    public interface OnIdReceivedListener {
        void onIdReceived(String id);
    }



}