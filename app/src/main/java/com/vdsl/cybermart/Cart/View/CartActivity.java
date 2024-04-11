package com.vdsl.cybermart.Cart.View;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.Cart.Adapter.CartAdapter;
import com.vdsl.cybermart.Cart.Model.CartModel;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Order.Fragment.PrepareFragment;
import com.vdsl.cybermart.Order.Model.Order;
import com.vdsl.cybermart.Order.OrderActivity;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.Adapter.VoucherListAdapter;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ActivityCartBinding;
import com.vdsl.cybermart.databinding.FragmentVoucherBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartAdapter.TotalPriceListener {
    CartModel cart;
    ImageView btnBack;
    Button btnCheckOut;
    RadioGroup radioGroup;
    RadioButton rdoCash, rdoCredit;
    ActivityCartBinding binding;
    private CartAdapter adapter;
    private TextView txtTotalPrice, txtVoucher;



    FragmentVoucherBinding voucherBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        findViews();
        readData();

        btnBack.setOnClickListener(v -> finish());
        //chuyển sang activity đặt hàng
        btnCheckOut.setOnClickListener(v -> {
            if (cart.getCartDetail() != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("cart",cart);
                String voucher = binding.textPromoCode.getText().toString().isEmpty() ? "0" : binding.textPromoCode.getText().toString();
                Intent intent= new Intent(this, OrderActivity.class);
                intent.putExtras(bundle);
                intent.putExtra("voucher",voucher);
                startActivity(intent);

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Thông báo");
                builder.setMessage("Giỏ hàng đang trống");
                builder.setNegativeButton("OK", ((dialog, which) -> {
                }));
                AlertDialog dialog = builder.create();
                dialog.show();
            }

        });
        binding.btnVoucher.setOnClickListener(v1 -> {
            String promoCode = binding.textPromoCode.getText().toString().trim();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Voucher");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        /*String voucherCode = snapshot1.child("code").getValue(String.class);*/
                        Voucher voucher = snapshot1.getValue(Voucher.class);
                        if (voucher.getCode() != null && voucher.getCode().equals(promoCode)) {
                            applyVoucher(voucher);
                            return;
                        }
                    }
                    Log.d("CartActivity", "Invalid voucher code or voucher does not exist");
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

                }
            });
        });


        binding.btnListVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, VoucherActivity.class);
            startActivity(intent);
            //finish();
        });
        String reCode = getIntent().getStringExtra("voucherCode");
        Log.e("check34", "onCreate: " + reCode );
        binding.textPromoCode.setText(reCode);
    }


    private void readData() {
        String reCode = getIntent().getStringExtra("voucherCode");
        RecyclerView rcvCart = findViewById(R.id.rcv_cart);
        rcvCart.setLayoutManager(new LinearLayoutManager(this));

        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        String cartDetailName = "cartDetail_" + accountId;
        SharedPreferences cartSharedPreferences = getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
        String cartId = cartSharedPreferences.getString("id", "");

        adapter = new CartAdapter(new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("carts").child(cartId).child("cartDetail"), ProductModel.class)
                .build(), this, this,reCode);
        rcvCart.setAdapter(adapter);

        txtTotalPrice = findViewById(R.id.text_total_price);
        DatabaseReference cartsRef = FirebaseDatabase.getInstance().getReference().child("carts");
        cartsRef.orderByChild("accountId").equalTo(accountId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalCartPrice = 0.0;
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    cart = cartSnapshot.getValue(CartModel.class);
                    if (cart != null) {
                        totalCartPrice += cart.getTotalPrice();
                    }
                }
                String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalCartPrice);
                SharedPreferences pref = getSharedPreferences("price",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("oldPrice", String.valueOf(totalCartPrice));
                editor.commit();

                txtTotalPrice.setText(String.format("%s $", formattedPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onTotalPriceUpdated(double totalPriceSum) {
        String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalPriceSum);
        txtTotalPrice.setText(String.format("%s $", formattedPrice));
    }

    private void applyVoucher(Voucher voucher) {
        SharedPreferences sharedPreferences = getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        String cartDetailName = "cartDetail_" + accountId;
        SharedPreferences cartSharedPreferences = getSharedPreferences(cartDetailName, Context.MODE_PRIVATE);
        String cartId = cartSharedPreferences.getString("id", "");
        String voucherCode = voucher.getCode();
        double discount = ((double) voucher.getDiscount() / 100);
        Date currentDate = new Date();
        DatabaseReference userVouchersRef = FirebaseDatabase.getInstance().getReference("UserVouchers").child(accountId);
        DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");




        String dateString = voucher.getExpiryDate();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date expiryDate = dateFormat.parse(dateString);

            if (expiryDate != null && expiryDate.before(currentDate)) {
                Log.d("check30", "applyVoucher: " + currentDate + expiryDate);
                Log.d("CartActivity", "Voucher has expired");
                General.showFailurePopup(CartActivity.this, "Sử Dụng Voucher", "Voucher đã hết hạn!", new OnDialogButtonClickListener() {
                    @Override
                    public void onDismissClicked(Dialog dialog) {
                        super.onDismissClicked(dialog);
                    }
                });
                return;
            }
        } catch (ParseException e) {
            Log.e("CartActivity", "Error parsing expiry date", e);
        }
        userVouchersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child(voucherCode).exists()) {
                        // Nếu voucher đã được sử dụng, hiển thị thông báo
                        Log.d("CartActivity", "Voucher đã được sử dụng trước đó");
                        General.showFailurePopup(CartActivity.this, "Sử Dụng Voucher", "Bạn đã dùng voucher này!", new OnDialogButtonClickListener() {
                            @Override
                            public void onDismissClicked(Dialog dialog) {
                                super.onDismissClicked(dialog);
                            }
                        });
                    } else {
                        updateTotalPriceAndMarkVoucherUsed(totalPriceRef, userVouchersRef, voucherCode, discount);

                    }
                } else {
                    userVouchersRef.child(voucherCode).setValue(true).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            updateTotalPriceAndMarkVoucherUsed(totalPriceRef, userVouchersRef, voucherCode, discount);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



    private void updateTotalPriceAndMarkVoucherUsed(DatabaseReference totalPriceRef, DatabaseReference userVouchersRef, String voucherCode, double discount) {
        totalPriceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    double totalPrice = snapshot.getValue(Double.class);
                    double totalCartPrice = totalPrice * (1 - discount);
                    totalPriceRef.setValue(totalCartPrice);
                    String formattedPrice = String.format(Locale.getDefault(), "%.2f", totalCartPrice);
                    txtTotalPrice.setText(String.format("%s $", formattedPrice));
                    General.showSuccessPopup(CartActivity.this, "Sử Dụng Voucher", "Sử Dụng Voucher thành công", new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);
                        }
                    });

                    // Đánh dấu voucher đã sử dụng
                    SharedPreferences pref = getSharedPreferences("price",MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("voucherCode", voucherCode);
                    editor.putString("discount", String.valueOf(discount));
                    editor.commit();
                    userVouchersRef.child(voucherCode).setValue(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }





    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readData();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void findViews() {
        btnBack = findViewById(R.id.c_ic_back);
        btnCheckOut = findViewById(R.id.btn_check_out_cart);
        txtVoucher = findViewById(R.id.text_promo_code);
    }

    /*@Override
    public void onClickVoucher(Voucher voucher) {
        DatabaseReference userVouchersRef = FirebaseDatabase.getInstance().getReference("UserVouchers").child(accountId);
        DatabaseReference totalPriceRef = FirebaseDatabase.getInstance().getReference("carts/" + cartId + "/totalPrice");
        double discount = ((double) voucher.getDiscount() / 100);
        adapter.setCartItemClickListener(new CartAdapter.CartItemClickListener() {
            @Override
            public void onPlusClicked(ProductModel productModel, double oldPrice) {
                updateTotalPriceAndMarkVoucherUsed(totalPriceRef,userVouchersRef,voucher.getCode(),discount);
            }

            @Override
            public void onMinusClicked(ProductModel productModel, double oldPrice) {

            }
        });
    }*/
}
