package com.vdsl.cybermart.Account.Fragment;

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


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LOGIN_PREFS", Context.MODE_PRIVATE);
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
                userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task ->
                        userDatabase.orderByChild("Email").equalTo(email).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        String storedPassword = userSnapshot.child("Password").getValue(String.class);
                                        if (storedPassword != null && storedPassword.equals(password)) {
                                            Toast.makeText(getActivity(), "Log in Successful", Toast.LENGTH_SHORT).show();
                                            rememberUser(email, password, binding.chkRemember.isChecked());
                                            startActivity(new Intent(getActivity(), MainActivity.class));
                                            // Trong LoginActivity hoặc nơi đăng nhập thành công

                                            Log.d("Role", "Role: " + userSnapshot.child("Role").getValue(String.class));
                                            getActivity().finish(); // Kết thúc activity hiện tại sau khi đăng nhập thành công
                                            return;
                                        }
                                    }
                                    Toast.makeText(getActivity(), "Incorrect password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Email not found!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error1) {
                            }
                        }));
            }

        });

    }

    private void rememberUser(String email, String password, boolean checked) {
        SharedPreferences pref = getActivity().getSharedPreferences("LOGIN_PREFS", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (!checked) {
            editor.clear();
        } else {
            editor.putString("email", email);
            editor.putString("password", password);
            editor.putBoolean("remember", checked);
        }
        editor.commit();

    }

}
