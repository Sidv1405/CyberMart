package com.vdsl.cybermart.Account.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.vdsl.cybermart.Account.Model.AddressModel;
import com.vdsl.cybermart.R;

public class AddressAdapter extends FirebaseRecyclerAdapter<AddressModel, AddressAdapter.AddressViewHolder> {

    private int selectedPosition = -1;
    private SharedPreferences sharedPreferences,addressPref;


    public AddressAdapter(@NonNull FirebaseRecyclerOptions<AddressModel> options, Context context) {
        super(options);
        sharedPreferences = context.getSharedPreferences("AddressAdapterPrefs", Context.MODE_PRIVATE);
        addressPref = context.getSharedPreferences("addressPref", Context.MODE_PRIVATE);
        selectedPosition = sharedPreferences.getInt("selectedPosition", -1);
    }

    @Override
    protected void onBindViewHolder(@NonNull AddressViewHolder addressViewHolder, int i, @NonNull AddressModel addressModel) {
        addressViewHolder.bind(addressModel, i);

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

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            chkUseAddress = itemView.findViewById(R.id.chkUseAddress);
            txtFullName = itemView.findViewById(R.id.txtFullName);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            chkUseAddress.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    selectedPosition = position;
                    notifyDataSetChanged();
                    SharedPreferences.Editor editor= addressPref.edit();
                    editor.putString("address", txtAddress.getText().toString());
                    editor.apply();
                    saveSelectedPosition(selectedPosition);
                }
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
