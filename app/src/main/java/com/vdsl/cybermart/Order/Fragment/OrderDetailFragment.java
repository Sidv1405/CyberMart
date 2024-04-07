package com.vdsl.cybermart.Order.Fragment;

import android.annotation.SuppressLint;
import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.vdsl.cybermart.Order.Adapter.ProductsListAdapterInOrder;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.FragmentOrderDetailBinding;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

    @SuppressLint("SetTextI18n")
    private void getOrderAndSetData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            Order order = (Order) bundle.getSerializable("Order");
            if (order != null) {
                binding.txtSeriNumber.setText(order.getSeri());
                binding.totalValue.setText(order.getCartModel().getTotalPrice()+"");
                binding.txtAddressName.setText(order.getAddress());
                binding.txtStatus.setText(order.getStatus());
                binding.txtPaymentName.setText(order.getPaymentMethod());
                binding.txtDate.setText(order.getCartModel().getDate());
                setTextUser(order);
                getProductInOrder(order);
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
                String id = sharedPreferences.getString("ID", "");
                String role = sharedPreferences.getString("role", "");
                if (!role.equals("Customer")) {
                    if(!order.getStatus().equals("Delivered")){
                        binding.txtStatus.setOnClickListener(v -> {
                            String[] status = new String[]{"Processing", "Canceled","Delivered"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                            builder.setTitle("Cập nhật trạng thái đơn hàng");
                            builder.setSingleChoiceItems(status, 0, (dialog, which) -> {
                                if(which==2){
                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(requireContext());
                                    builder1.setTitle("Cảnh báo");
                                    builder1.setMessage("Khi chuyển qua trạng thái \"Delivered\" thì bạn không thể thay đổi!");
                                    builder1.setPositiveButton("OK",(dialog1, which1) -> {
                                        setStatus(dialog, order, status[which],id);
                                        // trừ số hàng đã nhận vào số hàng trong kho
                                        updateQuantity(order);
                                    });
                                    builder1.setNegativeButton("Cancel",(dialog1, which1) -> {});
                                    AlertDialog alertDialog = builder1.create();
                                    alertDialog.show();
                                }else {
                                    setStatus(dialog, order, status[which],id);
                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        });
                    }
                }
            }
        }
    }

    private void setStatus(DialogInterface dialog, Order order, String status,String id) {
        order.setStatus(status);
        order.setIdStaff(id);
        order.setStatusId(status+order.getCartModel().getAccountId());
        DatabaseReference referenceOrder = FirebaseDatabase.getInstance().
                getReference("Orders").child(order.getSeri());
        referenceOrder.setValue(order);
        binding.txtStatus.setText(status);
        dialog.cancel();
    }

    private void setTextUser(Order order) {
        DatabaseReference referenceNameUser = FirebaseDatabase.getInstance().
                getReference("Account").child(order.getCartModel().getAccountId());
        referenceNameUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String nameUser = snapshot.child("fullName").getValue(String.class);
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
        query = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(order.getSeri()).child("cartModel").child("cartDetail");

        FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class).build();
        adapter = new ProductsListAdapterInOrder(options);
        binding.rvProductList.setAdapter(adapter);
    }
    private static void updateQuantity(Order order) {
        Query proQuery = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(order.getSeri()).child("cartModel").child("cartDetail");
        proQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ProductModel cartPro = dataSnapshot.getValue(ProductModel.class);
                        if (cartPro != null) {
                            int quantitySell = cartPro.getQuantity();
                            String productId = cartPro.getProdId();
                            // Cập nhật số lượng sản phẩm trong kho
                            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference()
                                    .child("products").child(productId).child("quantity");
                            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        int currentQuantity = Objects.requireNonNull(snapshot.getValue(Integer.class));
                                        int updatedQuantity = currentQuantity - quantitySell;
                                        productRef.setValue(updatedQuantity);

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.startListening();
    }
}