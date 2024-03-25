package com.vdsl.cybermart.Order.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Home.Model.ProductModel;
import com.vdsl.cybermart.Order.Adapter.ProductsListAdapterInOrder;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.databinding.FragmentOrderDetailBinding;

public class OrderDetailFragment extends Fragment {
    FragmentOrderDetailBinding binding;
    Query query;
    ProductsListAdapterInOrder adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getOrderAndSetData();

    }

    private void getOrderAndSetData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Order order = (Order) bundle.getSerializable("Order");
            if (order != null) {
                binding.txtSeriNumber.setText(order.getSeri());
                setTextUser(order);
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
                String id = sharedPreferences.getString("ID", "");
                String role = sharedPreferences.getString("Role", "");
                if (!role.equals("Customer")) {
                    //Chưa test
                    binding.txtStatus.setOnClickListener(v -> {
                        String[] status = new String[]{"Delevered", "Processing", "Cancelde"};
                        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                        builder.setTitle("Cập nhật trạng thái đơn hàng");
                        builder.setSingleChoiceItems(status, 0, (dialog, which) -> {
                            order.setStatus(status[which]);
                            DatabaseReference referenceOrder = FirebaseDatabase.getInstance().
                                    getReference("Orders").child(order.getSeri());
                            referenceOrder.setValue(order);
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    });
                }
            }
        }
    }

    private void setTextUser(Order order) {
        DatabaseReference referenceNameUser = FirebaseDatabase.getInstance().
                getReference("Account").child(order.getIdUser());
        referenceNameUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameUser = snapshot.child("FullName").getValue(String.class);
                    binding.txtCustomerName.setText(nameUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DATA", "onDataChange: Error");
            }
        });
    }

    private void getProductInOrder(Order order) {
        //tạo cartmodel để lưu danh sách các sản phẩm và số lượng sẽ lấy từ đó
        DatabaseReference refProducts = FirebaseDatabase.getInstance().getReference("Orders/Id01/product");
        refProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        query = FirebaseDatabase.getInstance().getReference("Orders")
                .orderByChild("status").equalTo("delivered");
        FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class).build();
        adapter = new ProductsListAdapterInOrder(options);
        binding.rvProductList.setAdapter(adapter);
    }

}