package com.vdsl.cybermart.Account.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vdsl.cybermart.Account.Fragment.FragmentUpdateAddress;
import com.vdsl.cybermart.Account.Model.AddressModel;
import com.vdsl.cybermart.R;

public class AddressAdapter extends FirebaseRecyclerAdapter<AddressModel, AddressAdapter.AddressViewHolder> {

    private int selectedPosition = -1;
    private SharedPreferences sharedPreferences, addressPref;
    private FragmentActivity mActivity;
    AddressModel addressDTO;

    public AddressAdapter(@NonNull FirebaseRecyclerOptions<AddressModel> options, Context context, FragmentActivity activity) {
        super(options);
        sharedPreferences = context.getSharedPreferences("AddressAdapterPrefs", Context.MODE_PRIVATE);
        addressPref = context.getSharedPreferences("addressPref", Context.MODE_PRIVATE);
        selectedPosition = sharedPreferences.getInt("selectedPosition", -1);
        mActivity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull AddressViewHolder addressViewHolder, int i, @NonNull AddressModel addressModel) {
        addressViewHolder.bind(addressModel, i);

        addressViewHolder.itemView.setOnClickListener(v -> {
            String addressId = getRef(i).getKey();
            addressDTO = new AddressModel(addressViewHolder.txtFullName.getText().toString(), addressViewHolder.txtAddress.getText().toString());
            FragmentUpdateAddress fragmentUpdateAddress = new FragmentUpdateAddress();
            Bundle bundle = new Bundle();
            bundle.putString("fullName", addressModel.getFullName());
            bundle.putString("address", addressModel.getAddress());
            bundle.putString("addressId", addressId);
            Log.d("addressId", "onBindViewHolder: " + addressId);
            fragmentUpdateAddress.setArguments(bundle);
            FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentUpdateAddress);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        addressViewHolder.chkUseAddress.setChecked(selectedPosition==i);
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    class AddressViewHolder extends RecyclerView.ViewHolder {

        CheckBox chkUseAddress;
        TextView txtFullName, txtAddress;
        CardView CvItemAddress;

        @SuppressLint("NotifyDataSetChanged")
        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            chkUseAddress = itemView.findViewById(R.id.chkUseAddress);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            CvItemAddress = itemView.findViewById(R.id.CvItemAddress);
//            chkUseAddress.setOnClickListener(v -> {
//                boolean isChecked = chkUseAddress.isChecked();
//                SharedPreferences.Editor editor = addressPref.edit();
//                if (isChecked) {
//                    int position = getAdapterPosition();
//                    if (position != RecyclerView.NO_POSITION) {
//                        selectedPosition = position;
//                        editor.putString("address", txtAddress.getText().toString());
//                        saveSelectedPosition(selectedPosition);
//                        notifyDataSetChanged();
//                    }
//                } else if(!isChecked){
//                    selectedPosition = -1;
//                    editor.putString("address", null);
//                    saveSelectedPosition(selectedPosition);
//                    notifyDataSetChanged();
//                }
//                editor.apply();
//            });

            chkUseAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = addressPref.edit();
                if (isChecked) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        selectedPosition = position;
                        editor.putString("address", txtAddress.getText().toString());
                        saveSelectedPosition(selectedPosition);
                    }
                } else if(!isChecked){
                    selectedPosition = -1;
                    editor.putString("address", null);
                    saveSelectedPosition(selectedPosition);
                }
                editor.apply();
            });
        }

        public void bind(AddressModel item, int position) {
            txtFullName.setText(item.getFullName());
            txtAddress.setText(item.getAddress());
            chkUseAddress.setChecked(position == selectedPosition);
        }
    }

    private void saveSelectedPosition(int position) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectedPosition", position);
        editor.putInt("address", position);
        editor.apply();
    }
}
