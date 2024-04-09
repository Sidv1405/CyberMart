package com.vdsl.cybermart.Account.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.MainActivity;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentLogInBinding;

public class FragmentLogIn extends Fragment {
    FragmentLogInBinding binding;

    FirebaseAuth userAuth;
    DatabaseReference userDatabase;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Account");
        progressDialog = new ProgressDialog(getActivity());
        sharedPreferences = requireActivity().getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);
        binding.edtEmailSignIn.setText(sharedPreferences.getString("email", ""));
        binding.edtPassSignIn.setText(sharedPreferences.getString("password", ""));
        binding.chkRemember.setChecked(sharedPreferences.getBoolean("remember", false));


        binding.txtSignUp.setOnClickListener(v -> {
            FragmentSignUp fragmentSignUp = new FragmentSignUp();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fr_framemain, fragmentSignUp);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.txtForgotPass.setOnClickListener(v -> {
            FragmentForgotPassword fragmentForgotPassword = new FragmentForgotPassword();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fr_framemain, fragmentForgotPassword);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmailSignIn.getText().toString().trim();
            String password = binding.edtPassSignIn.getText().toString().trim();

            boolean error = false;
            String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

            if (TextUtils.isEmpty(email)) {
                binding.edtEmailSignIn.setError("Please enter your Email!");
                error = true;
            } else if (!email.matches(emailPattern)) {
                binding.edtEmailSignIn.setError("Wrong email format!");
                error = true;
            }
            if (TextUtils.isEmpty(password)) {
                binding.edtPassSignIn.setError("Please enter your Password!");
                error = true;
            } else if (password.length() < 6) {
                binding.edtPassSignIn.setError("Please enter more than 6 characters!");
                error = true;
            }

            if (!error) {
                progressDialog.setMessage("Loging now...");
                progressDialog.show();
                userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            String ID = userSnapshot.getKey();
                                            String fullName = userSnapshot.child("fullName").getValue(String.class);
                                            String email = userSnapshot.child("email").getValue(String.class);
                                            String role = userSnapshot.child("role").getValue(String.class);
                                            String avatar = userSnapshot.child("avatar").getValue(String.class);
                                            /*String Address = userSnapshot.child("address").getValue(String.class);*/
                                            String phoneNumber = userSnapshot.child("phoneNumber").getValue(String.class);

                                            editor.putString("ID", ID);
                                            editor.putString("fullName", fullName);
                                            editor.putString("email", email);
                                            editor.putString("role", role);

                                            if (!TextUtils.isEmpty(avatar)) {
                                                editor.putString("avatar", avatar);
                                                Log.d("Avatar", "onViewCreated: " + avatar);
                                            } else {
                                                editor.putString("avatar", null);
                                                Log.d("Avatar", "Avatar is empty");
                                            }
                                            /*editor.putString("address", Address);*/

                                            progressDialog.dismiss();
                                            Toast.makeText(requireActivity(), "Log in Successful", Toast.LENGTH_SHORT).show();
                                            rememberUser(email, password, binding.chkRemember.isChecked());
                                            editor.putString("phoneNumber", phoneNumber);
                                            editor.apply();
                                            Intent intent = new Intent(requireActivity(), MainActivity.class);
                                            startActivity(intent);
                                            getActivity().finish();
                                            Log.d("ID", "ID " + ID);
                                            Log.d("Role", "Role: " + role);
                                            return;
                                        }
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Email not found!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error1) {
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
//                            Intent intent = new Intent(getActivity(), MainActivity.class);
//                            startActivity(intent);
//                            getActivity().finish();
//                            loginWithRealtimeDatabase(email, password);
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Failed, Please check your Email or Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });

    }

    private void loginWithRealtimeDatabase(String email, String password) {
        userDatabase.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Tìm thấy email trong Realtime Database, kiểm tra mật khẩu
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String storedPassword = userSnapshot.child("password").getValue(String.class);
                        if (storedPassword != null && storedPassword.equals(password)) {
                            // Đăng nhập thành công bằng Realtime Database

                            // Lưu thông tin người dùng vào SharedPreferences
                            sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            String ID = userSnapshot.getKey();
                            String fullName = userSnapshot.child("fullName").getValue(String.class);
                            String role = userSnapshot.child("role").getValue(String.class);
                            String avatar = userSnapshot.child("avatar").getValue(String.class);
                            String phoneNumber = userSnapshot.child("phoneNumber").getValue(String.class);
                            String active = userSnapshot.child("active").getValue(String.class);

                            editor.putString("ID", ID);
                            editor.putString("fullName", fullName);
                            editor.putString("email", email); // Lưu email từ người dùng nhập, không phải từ database
                            editor.putString("role", role);
                            editor.putString("avatar", avatar != null ? avatar : ""); // Kiểm tra Avatar null
                            editor.putString("phoneNumber", phoneNumber != null ? phoneNumber : ""); // Kiểm tra PhoneNumber null
                            editor.apply();

                            if (active != null && active.equals("Not working")) {
                                progressDialog.dismiss();
                                Toast.makeText(requireActivity(), "Your account has been locked. Please contact support.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // Chuyển đến giao diện trang chủ
                            progressDialog.dismiss();
                            Toast.makeText(requireActivity(), "Log in Successful", Toast.LENGTH_SHORT).show();
                            rememberUser(email, password, binding.chkRemember.isChecked());
                            Intent intent = new Intent(requireActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                            return;
                        }
                    }
                    // Mật khẩu không khớp
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), "Incorrect password", Toast.LENGTH_SHORT).show();
                } else {
                    // Không tìm thấy email trong Realtime Database
                    progressDialog.dismiss();
                    Toast.makeText(requireActivity(), "Email not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error1) {
                progressDialog.dismiss();
                Toast.makeText(requireActivity(), "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void rememberUser(String email, String password, boolean checked) {
        SharedPreferences pref = requireActivity().getSharedPreferences("LOGIN_PREFS", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!checked) {
            editor.clear();
        } else {
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putBoolean("remember", checked);
        }
        editor.apply();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}
