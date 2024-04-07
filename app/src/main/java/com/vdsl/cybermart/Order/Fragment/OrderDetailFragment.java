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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderDetailFragment extends Fragment {
    FragmentOrderDetailBinding binding;
    Query query;
    ProductsListAdapterInOrder adapter;

    String userFCM;


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
                String role = sharedPreferences.getString("Role", "");
                String idAccount = order.getCartModel().getAccountId();
                getTokenFromId(idAccount, new OnIdReceivedListener() {
                    @Override
                    public void onIdReceived(String id) {
                        userFCM = id;
                        Log.e("check48", "onIdReceived: " + id + userFCM );
                    }
                });
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
                                        setStatus(dialog, order, status[which]);
                                        sendNotification(order, status[which]);
                                        // trừ số hàng đã nhận vào số hàng trong kho
                                    });
                                    builder1.setNegativeButton("Cancel",(dialog1, which1) -> {});
                                    AlertDialog alertDialog = builder1.create();
                                    alertDialog.show();
                                }else {
                                    setStatus(dialog, order, status[which]);
                                    sendNotification(order, status[which]);
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

    private void setStatus(DialogInterface dialog, Order order, String status) {
        order.setStatus(status);
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

    private void sendNotification(Order order,String status) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Orders").child(order.getSeri());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Order order1 = snapshot.getValue(Order.class);
                    try {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject notificationObj = new JSONObject();
                        notificationObj.put("title",order1.getSeri());
                        notificationObj.put("body","status: " +  status);
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("userId",order1.getCartModel().getAccountId());

                        jsonObject.put("notification",notificationObj);
                        jsonObject.put("data",dataObj);
                        jsonObject.put("to",userFCM);
                        Log.e("check39", "onDataChange: " + userFCM );
                        callApi(jsonObject);

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTokenFromId(String id, final OnIdReceivedListener listener) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Account/" + id);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fcmToken = null;
                if (snapshot.exists()) {
                    fcmToken = snapshot.child("fcmToken").getValue(String.class);
                }
                listener.onIdReceived(fcmToken);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onIdReceived(null);
            }
        });
    }

    void callApi(JSONObject jsonObject){
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Log.e("check44", "callApi OrderDetail: " + jsonObject.toString() );
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .header("Authorization","Bearer AAAA5pa11nw:APA91bH9w1IfcYjdTiuQsj-o3Ttrh689JQxiL0ydOQf6qyEeSxlkbznOz7IYG6yC3rVEo6mCAM7CenfstwWe6nXPsirmoI43hcNVqpcxuNZ5uSWhNHImi0fMI-VXbirIX2GX1zWdzf80")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // Xử lý khi gửi yêu cầu không thành công
                Log.e("callApi", "Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Phản hồi thành công, có thể làm gì đó ở đây
                    Log.e("callApi", "Request successful: " + response.body().string());
                } else {
                    // Phản hồi không thành công, có thể làm gì đó ở đây
                    Log.e("callApi", "Request failed with code: " + response.code());
                }
            }
        });
    }


    public interface OnIdReceivedListener {
        void onIdReceived(String id);
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