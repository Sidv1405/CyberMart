package com.vdsl.cybermart.Account.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.DialogInformationBinding;
import com.vdsl.cybermart.databinding.DialogPasswordBinding;

public class SettingsActivity extends AppCompatActivity {

    ImageView img_back;
    TextView txtPersonInfor, txtPasswordtitle, txtName, txtEmail, txtPassword, txtAddress, txtPhoneNumber;
    CardView CvName, CvEmail, CvPassword, CvFAQ, CvContact, CvPrivacy, CvAddress, CvPhoneNumber;
    Switch SwSales, SwNewArrivals, SwDelivery;

    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    SharedPreferences preferencesGetInfor, preferencesGetPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        img_back = findViewById(R.id.img_back);
        txtPersonInfor = findViewById(R.id.txtPersonInfor);
        txtPasswordtitle = findViewById(R.id.txtPasswordtitle);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtAddress = findViewById(R.id.txtAddress);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        CvName = findViewById(R.id.CvName);
        CvEmail = findViewById(R.id.CvEmail);
        CvPassword = findViewById(R.id.CvPassword);
        CvFAQ = findViewById(R.id.CvFAQ);
        CvContact = findViewById(R.id.CvContact);
        CvPrivacy = findViewById(R.id.CvPrivacy);
        CvAddress = findViewById(R.id.CvAddress);
        CvPhoneNumber = findViewById(R.id.CvPhoneNumber);
        SwSales = findViewById(R.id.SwSales);
        SwNewArrivals = findViewById(R.id.SwNewArrivals);
        SwDelivery = findViewById(R.id.SwDelivery);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account");
        preferencesGetInfor = getSharedPreferences("Users", Context.MODE_PRIVATE);
        preferencesGetPass = getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);

        showInitInfor();

        img_back.setOnClickListener(v -> finish());

        txtPersonInfor.setOnClickListener(v -> {
            showInformationDialog();
        });
        CvName.setOnClickListener(v -> {
            showInformationDialog();
        });
        CvEmail.setOnClickListener(v -> {
            showInformationDialog();
        });
        CvAddress.setOnClickListener(v -> {
            showInformationDialog();
        });
        CvPhoneNumber.setOnClickListener(v -> {
            showInformationDialog();
        });

        txtPasswordtitle.setOnClickListener(v -> {
            showPassDialog();
        });
        CvPassword.setOnClickListener(v -> {
            showPassDialog();
        });

    }

    private void showInitInfor() {
        if (auth.getCurrentUser() != null) {
            Log.d("loginnow", "logged in");

            String FullName = preferencesGetInfor.getString("fullName", "nothing to show");
            String Email = preferencesGetInfor.getString("email", "nothing to show");
            String Password = preferencesGetPass.getString("password", "nothing to show");
            String Address = preferencesGetInfor.getString("address", "No address yet");
            String PhoneNumber = preferencesGetInfor.getString("phoneNumber", "No phone number yet");

            txtName.setText(FullName);
            txtEmail.setText(Email);
            txtPassword.setText(Password);
            txtAddress.setText(Address);
            txtPhoneNumber.setText(PhoneNumber);
            if (FullName.equals("nothing to show")) {
                txtName.setTextColor(Color.RED);
            } else {
                txtName.setTextColor(Color.BLACK);
            }
            if (Address.equals("No address yet")) {
                txtAddress.setTextColor(Color.RED);
            } else {
                txtAddress.setTextColor(Color.BLACK);
            }
            if (PhoneNumber.equals("No phone number yet")) {
                txtPhoneNumber.setTextColor(Color.RED);
            } else {
                txtPhoneNumber.setTextColor(Color.BLACK);
            }

        } else {
            Log.d("loginnow", "not logged in");
        }
    }

    /**
     * @noinspection deprecation
     */
    private void showInformationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        DialogInformationBinding dialogInformation = DialogInformationBinding.inflate(inflater);
        builder.setView(dialogInformation.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        dialogInformation.edtName.setText(txtName.getText().toString());
        if (txtAddress.getText().toString().equals("No address yet")) {
            dialogInformation.edtAddress.setText(null);
        } else {
            dialogInformation.edtAddress.setText(txtAddress.getText().toString());
        }
        if (txtName.getText().toString().equals("No phone number yet")) {
            dialogInformation.edtPhoneNumber.setText(null);
        } else {
            dialogInformation.edtPhoneNumber.setText(txtName.getText().toString());
        }
        dialogInformation.edtEmail.setText(txtEmail.getText().toString());

        dialogInformation.btnCancel1.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogInformation.btnDone.setOnClickListener(v -> {
            String newName = dialogInformation.edtName.getText().toString();
            String newEmail = dialogInformation.edtEmail.getText().toString();
            boolean error = false;
            String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
            FirebaseUser user = auth.getCurrentUser();

            if (newName.isEmpty()) {
                dialogInformation.edtName.setError("Please enter your name!");
                error = true;
            }
            if (newEmail.isEmpty()) {
                dialogInformation.edtEmail.setError("Please enter your email!");
                error = true;
            } else if (!newEmail.matches(emailPattern)) {
                dialogInformation.edtEmail.setError("Wrong email format!!");
                error = true;
            }

            if (!error) {
                if (user != null) {
                    if (user.isEmailVerified()) {
                        user.updateEmail(newEmail).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Email đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("error", "showInformationDialog: " + task.getException().getMessage());
                                Toast.makeText(SettingsActivity.this, "Không thể cập nhật email trên Firebase Authentication", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        user.sendEmailVerification().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Một email xác thực đã được gửi đến địa chỉ email của bạn. Vui lòng kiểm tra và xác nhận trước khi cập nhật email.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SettingsActivity.this, "Không thể gửi email xác thực. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

        });

    }

    private void showPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        DialogPasswordBinding dialogPass = DialogPasswordBinding.inflate(inflater);
        builder.setView(dialogPass.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();
        dialogPass.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogPass.btnDone.setOnClickListener(v -> {
            FirebaseUser user = auth.getCurrentUser();
            String oldPass = dialogPass.edtOldPass.getText().toString();
            String newPass = dialogPass.edtNewPass.getText().toString();
            String reNewPass = dialogPass.edtReNewPass.getText().toString();
            String Password = preferencesGetPass.getString("password", "nothing to show");
            boolean error = false;
            if (oldPass.isEmpty()) {
                dialogPass.edtOldPass.setError("Please enter your old password!");
                error = true;
            } else if (!oldPass.equals(Password)) {
                dialogPass.edtOldPass.setError("Your old password is wrong!");
                error = true;
            }
            if (newPass.isEmpty()) {
                dialogPass.edtNewPass.setError("Please enter your new password!");
                error = true;
            } else if (newPass.length() < 6) {
                dialogPass.edtNewPass.setError("Please enter more than 6 characters!");
                error = true;
            }
            if (reNewPass.isEmpty()) {
                dialogPass.edtReNewPass.setError("Please enter your new password!");
                error = true;
            } else if (!reNewPass.equals(newPass)) {
                dialogPass.edtReNewPass.setError("Your password not match!");
                error = true;
            }

            if (!error) {
                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Change password successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SettingsActivity.this, "Failed to change password!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}