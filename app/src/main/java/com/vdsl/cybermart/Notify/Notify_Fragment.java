package com.vdsl.cybermart.Notify;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Message.MessageActivity;
import com.vdsl.cybermart.Message.Model.Chat;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentNotifyBinding;

import java.util.ArrayList;

public class Notify_Fragment extends Fragment {

    FragmentNotifyBinding binding;

    NotificationAdapter adapter;

    FirebaseUser firebaseUser;

    FirebaseDatabase firebaseDatabase;

    DatabaseReference reference;

    ArrayList<NotifyModel> notifyList = new ArrayList<>();

    public Notify_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotifyBinding
                .inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.rcvNotify.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        binding.rcvNotify.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setStackFromEnd(true);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        receiveNotify();

        binding.btnClear.setOnClickListener(v -> {
            showDeleteConfirmDialog();
        });

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchNotify(newText);
                return false;
            }
        });

    }

    private void searchNotify(String query) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Notification");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifyList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    NotifyModel notifyModel = snapshot1.getValue(NotifyModel.class);
                    if (notifyModel != null && notifyModel.getUserId().equals(firebaseUser.getUid()) &&
                            notifyModel.getBody().contains(query)) {
                        notifyList.add(notifyModel);
                    }
                }
                adapter = new NotificationAdapter(notifyList, getContext(), 1);
                binding.rcvNotify.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void showDeleteConfirmDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_confirm_delete_notification);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextView btnCancel = dialog.findViewById(R.id.btn_cancel);
        TextView btnDelete = dialog.findViewById(R.id.btn_delete);

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            dialog.dismiss();
            clearNotify(firebaseUser.getUid());
        });

        dialog.show();
    }


    private void clearNotify(String userId) {
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("Notification");

        notificationsRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        });
    }


    private void receiveNotify() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Notification");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notifyList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    NotifyModel notifyModel = snapshot1.getValue(NotifyModel.class);
                    if (notifyModel != null && notifyModel.getUserId().equals(firebaseUser.getUid())) {
                        notifyList.add(notifyModel);
                    }
                }
                adapter = new NotificationAdapter(notifyList, getContext());
                binding.rcvNotify.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}

