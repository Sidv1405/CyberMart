package com.vdsl.cybermart.Voucher.View;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Voucher.Adapter.VoucherListAdapter;
import com.vdsl.cybermart.Voucher.Voucher;
import com.vdsl.cybermart.databinding.ActivityVoucherAddBinding;
import com.vdsl.cybermart.databinding.ActivityVoucherBinding;

public class VoucherAddActivity extends AppCompatActivity {

    ActivityVoucherAddBinding binding;

    String name, discount, code, date;

    DatabaseReference voucherRef;

    VoucherListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voucher_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        voucherRef = FirebaseDatabase.getInstance().getReference().child("Voucher");

        binding = ActivityVoucherAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.edtNameVoucher.setOnFocusChangeListener((v, hasFocus) -> {
            validName();
        });

        binding.edtDiscount.setOnFocusChangeListener((v, hasFocus) -> {
            validDiscount();
        });
        binding.edtCodeVoucher.setOnFocusChangeListener((v, hasFocus) -> {
            validCode();
        });
        binding.edtDate.setOnFocusChangeListener((v, hasFocus) -> {
            validDate();
        });

        binding.btnAddVoucher.setOnClickListener(v -> {
            AddVoucher();
        });
    }

    private void AddVoucher() {
        validName();
        validCode();
        validDate();
        validDiscount();
        if (binding.titleCodeVoucher.getError() == null &&
                binding.titleNameVoucher.getError() == null &&
                binding.titleDate.getError() == null &&
                binding.titleDiscount.getError() == null &&
                binding.titleNameVoucher.getError() == null) {

            String pathObj = String.valueOf(code);
            voucherRef.orderByChild("code").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        General.showFailurePopup(VoucherAddActivity.this, "Thất Bại", "Thêm Voucher thất bại", new OnDialogButtonClickListener() {
                            @Override
                            public void onDismissClicked(Dialog dialog) {
                                super.onDismissClicked(dialog);
                            }
                        });
                    } else {
                        DatabaseReference newVoucherRef = voucherRef.push();
                        Voucher voucher = new Voucher(code, name, date, Integer.parseInt(discount));
                        newVoucherRef.setValue(voucher);
                        General.showSuccessPopup(VoucherAddActivity.this, "Thành công", "Bạn đã thêm voucher thành công", new OnDialogButtonClickListener() {
                            @Override
                            public void onDismissClicked(Dialog dialog) {
                                super.onDismissClicked(dialog);
                                Intent intent = new Intent(VoucherAddActivity.this,VoucherActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("VoucherActivity", "Firebase query cancelled: " + error.getMessage());
                }
            });
        }else{
            General.showFailurePopup(VoucherAddActivity.this, "Thất Bại", "Thêm Voucher thất bại", new OnDialogButtonClickListener() {
                @Override
                public void onDismissClicked(Dialog dialog) {
                    super.onDismissClicked(dialog);

                }
            });
        }
    }

    public void validName() {
        name = binding.edtNameVoucher.getText().toString();
        if (name.isEmpty()) {
            binding.titleNameVoucher.setError("Không được để trống");
        } else {
            binding.titleNameVoucher.setError(null);
        }
    }

    public void validCode() {
        code = binding.edtCodeVoucher.getText().toString();
        if (code.isEmpty()) {
            binding.titleCodeVoucher.setError("Không được để trống");
        } else {
            binding.titleCodeVoucher.setError(null);
        }
    }

    private void validDiscount() {
        discount = binding.edtDiscount.getText().toString();
        if (discount.isEmpty()) {
            binding.titleDiscount.setError("Không được để trống");
        } else {
            try {

                int salaryInt = Integer.parseInt(discount);
                if (salaryInt < 0) {
                    binding.titleDiscount.setError("Discount phải lớn hơn 0");
                } else {
                    binding.titleDiscount.setError(null);
                }
            } catch (Exception e) {
                binding.titleDiscount.setError("Vui Lòng nhập đúng định dạng số");
            }
        }
    }

    public void validDate() {
        String regex = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/(19|20)\\d{2}$";
        date = binding.edtDate.getText().toString();
        if (date.isEmpty()) {
            binding.titleDate.setError("Không được để trống");
        } else {
            if (!date.matches(regex)) {
                binding.titleDate.setError("Vui Lòng nhập đúng định dạng dd/mm/yyyy");
            } else {
                binding.titleDate.setError(null);
            }
        }
    }


}
