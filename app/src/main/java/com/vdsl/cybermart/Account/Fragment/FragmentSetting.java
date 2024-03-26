package com.vdsl.cybermart.Account.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.DialogInformationBinding;
import com.vdsl.cybermart.databinding.DialogPasswordBinding;
import com.vdsl.cybermart.databinding.FragmentSettingBinding;

public class FragmentSetting extends Fragment {

    FragmentSettingBinding binding;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    SharedPreferences preferencesGetInfor, preferencesGetPass, addressPref;
    FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account");
        preferencesGetInfor = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        preferencesGetPass = getActivity().getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);
        addressPref = getActivity().getSharedPreferences("addressPref", Context.MODE_PRIVATE);
        currentUser = auth.getCurrentUser();

        showInitInfor();

        binding.imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        binding.txtPersonInfor.setOnClickListener(v -> {
            showInformationDialog();
        });
        binding.CvName.setOnClickListener(v -> {
            showInformationDialog();
        });
        binding.CvEmail.setOnClickListener(v -> {
            showInformationDialog();
        });
        binding.CvAddress.setOnClickListener(v -> {
            FragmentAddress fragment = new FragmentAddress();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.txtYourAddress.setOnClickListener(v -> {
            FragmentAddress fragment = new FragmentAddress();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.CvPhoneNumber.setOnClickListener(v -> {
            showInformationDialog();
        });

        binding.txtPasswordtitle.setOnClickListener(v -> {
            showPassDialog();
        });
        binding.CvPassword.setOnClickListener(v -> {
            showPassDialog();
        });
    }

    private void showInitInfor() {
        if (auth.getCurrentUser() != null) {
            Log.d("loginnow", "logged in");
            databaseReference.orderByChild("email").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String userId = dataSnapshot.getKey();
                            if (userId != null) {
                                String Password = preferencesGetPass.getString("password", "nothing to show");
                                binding.txtPassword.setText(Password);
                                String FullName = dataSnapshot.child("fullName").getValue(String.class);
                                String Email = dataSnapshot.child("email").getValue(String.class);
                                String Address = addressPref.getString("address", "No address yet");
                                String PhoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                                binding.txtName.setText(FullName);
                                binding.txtEmail.setText(Email);
                                binding.txtAddress.setText(Address);
                                binding.txtPhoneNumber.setText(PhoneNumber);
                                if (FullName.equals(null)) {
                                    binding.txtName.setTextColor(Color.RED);
                                } else {
                                    binding.txtName.setTextColor(Color.BLACK);
                                }
                                if (Address.equals("No address yet")) {
                                    binding.txtAddress.setTextColor(Color.RED);
                                } else {
                                    binding.txtAddress.setTextColor(Color.BLACK);
                                }
                                if (PhoneNumber.equals(null)) {
                                    binding.txtPhoneNumber.setTextColor(Color.RED);
                                } else {
                                    binding.txtPhoneNumber.setTextColor(Color.BLACK);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            String FullName = preferencesGetInfor.getString("fullName", "nothing to show");
//            String Email = preferencesGetInfor.getString("email", "nothing to show");
//            String Password = preferencesGetPass.getString("password", "nothing to show");
//            String Address = preferencesGetInfor.getString("address", "No address yet");
//            String PhoneNumber = preferencesGetInfor.getString("phoneNumber", "No phone number yet");
//
//            binding.txtName.setText(FullName);
//            binding.txtEmail.setText(Email);
//            binding.txtPassword.setText(Password);
////            binding.txtAddress.setText(Address);
//            binding.txtPhoneNumber.setText(PhoneNumber);
//            if (FullName.equals("nothing to show")) {
//                binding.txtName.setTextColor(Color.RED);
//            } else {
//                binding.txtName.setTextColor(Color.BLACK);
//            }
////            if (Address.equals("No address yet")) {
////                binding.txtAddress.setTextColor(Color.RED);
////            } else {
////                binding.txtAddress.setTextColor(Color.BLACK);
////            }
//            if (PhoneNumber.equals("No phone number yet")) {
//                binding.txtPhoneNumber.setTextColor(Color.RED);
//            } else {
//                binding.txtPhoneNumber.setTextColor(Color.BLACK);
//            }

        } else {
            Log.d("loginnow", "not logged in");
        }
    }//endShowInit

    /**
     * @noinspection deprecation
     */
    private void showInformationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        DialogInformationBinding dialogInformation = DialogInformationBinding.inflate(inflater);
        builder.setView(dialogInformation.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        dialogInformation.edtName.setText(binding.txtName.getText().toString());
        if (binding.txtAddress.getText().toString().equals("No address yet")) {
            dialogInformation.edtAddress.setText(null);
        } else {
            dialogInformation.edtAddress.setText(binding.txtAddress.getText().toString());
        }
        if (binding.txtPhoneNumber.getText().toString().equals("No phone number yet")) {
            dialogInformation.edtPhoneNumber.setText(null);
        } else {
            dialogInformation.edtPhoneNumber.setText(binding.txtPhoneNumber.getText().toString());
        }
        dialogInformation.edtEmail.setText(binding.txtEmail.getText().toString());

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
                                Toast.makeText(getActivity(), "Email đã được cập nhật thành công", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d("error", "showInformationDialog: " + task.getException().getMessage());
                                Toast.makeText(getActivity(), "Không thể cập nhật email trên Firebase Authentication", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        user.sendEmailVerification().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Một email xác thực đã được gửi đến địa chỉ email của bạn. Vui lòng kiểm tra và xác nhận trước khi cập nhật email.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Không thể gửi email xác thực. Vui lòng thử lại sau.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }

        });

    }

    private void showPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            Toast.makeText(getActivity(), "Change password successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to change password!", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }
}