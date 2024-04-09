package com.vdsl.cybermart.Account.Adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Account.Fragment.FragmentUpdateStaff;
import com.vdsl.cybermart.Account.Model.UserModel;
import com.vdsl.cybermart.R;

public class StaffMangeAdapter extends FirebaseRecyclerAdapter<UserModel, StaffMangeAdapter.StaffManageViewHolder> {

    FragmentActivity mActivity;
    public StaffMangeAdapter(@NonNull FirebaseRecyclerOptions<UserModel> options, FragmentActivity activity) {
        super(options);
        mActivity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull StaffManageViewHolder staffManageViewHolder, int i, @NonNull UserModel userModel) {
        staffManageViewHolder.bind(userModel);

        staffManageViewHolder.itemView.setOnClickListener(v -> {
            String staffId = getRef(i).getKey();
            FragmentUpdateStaff fragmentUpdateStaff = new FragmentUpdateStaff();
            Bundle bundle = new Bundle();
            bundle.putString("fullName", userModel.getFullName());
            bundle.putString("phoneNumber", userModel.getPhoneNumber());
            bundle.putString("email", userModel.getEmail());
            bundle.putString("password", userModel.getPassword());
            bundle.putString("staffId", staffId);
            bundle.putString("active", userModel.getActive());
            fragmentUpdateStaff.setArguments(bundle);
            FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentUpdateStaff);
            transaction.addToBackStack(null);
            transaction.commit();

        });
    }

    @NonNull
    @Override
    public StaffManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_staff, parent, false);
        return new StaffManageViewHolder(view);
    }

    class StaffManageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgStaffAvatar;
        TextView txtStaffName, txtStaffEmail;

        public StaffManageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStaffAvatar = itemView.findViewById(R.id.imgStaffAvatar);
            txtStaffName = itemView.findViewById(R.id.txtStaffName);
            txtStaffEmail = itemView.findViewById(R.id.txtStaffEmail);
        }

        public void bind(UserModel item) {
            txtStaffName.setText(item.getFullName());
            txtStaffEmail.setText(item.getEmail());
            if (item.getAvatar() != null) {
                Picasso.get()
                        .load(item.getAvatar())
                        .error(R.drawable.img_default_profile_image) // Đặt ảnh mặc định nếu không thể tải được ảnh
                        .into(imgStaffAvatar, new Callback() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(R.drawable.img_default_profile_image).into(imgStaffAvatar);
                            }
                        });
            } else {
                Picasso.get().load(R.drawable.img_default_profile_image).into(imgStaffAvatar);
            }

        }
    }
}
