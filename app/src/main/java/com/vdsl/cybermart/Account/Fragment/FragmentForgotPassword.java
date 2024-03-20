package com.vdsl.cybermart.Account.Fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentForgotPassBinding;

public class FragmentForgotPassword extends Fragment {
    FragmentForgotPassBinding binding;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    /**
     * @noinspection deprecation
     */
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentForgotPassBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Account");
        progressDialog = new ProgressDialog(getActivity());
        binding.btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                String email = binding.edtEmailForgotPass.getText().toString().trim();

                boolean error = false;
                String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

                if (TextUtils.isEmpty(email)) {
                    binding.edtEmailForgotPass.setError("Please enter your Email!");
                    error = true;
                } else if (!email.matches(emailPattern)) {
                    binding.edtEmailForgotPass.setError("Wrong email format!");
                    error = true;
                }

                if (!error) {


                    mDatabase.orderByChild("Email").equalTo(email).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    mAuth.sendPasswordResetEmail(email)
                                            .addOnCompleteListener(task1 -> {
                                                if (task1.isSuccessful()) {
                                                    progressDialog.dismiss();
                                                    binding.edtEmailForgotPass.setVisibility(View.GONE);
                                                    binding.textInputLayout.setVisibility(View.GONE);
                                                    binding.txtText.setVisibility(View.VISIBLE);
                                                    binding.txtTitle.setText("Verify Successfully");
                                                    binding.txtText.setText("Please check your email to change password");
                                                    binding.btnVerify.setText("Back to sign in");
                                                    binding.btnVerify.setOnClickListener(v1 -> {
                                                        FragmentLogIn fragmentLogIn = new FragmentLogIn();
                                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                                        transaction.replace(R.id.fr_framemain, fragmentLogIn);
                                                        transaction.addToBackStack(null);
                                                        transaction.commit();
                                                    });

                                                    Toast.makeText(getActivity(), "Password reset email sent", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getActivity(), "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    return;
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "Email not found!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error1) {
                        }
                    });

                } else {
                    progressDialog.dismiss();
                }
            }
        });
    }
}
