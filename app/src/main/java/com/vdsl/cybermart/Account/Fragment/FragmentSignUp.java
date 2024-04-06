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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentSignUpBinding;

public class FragmentSignUp extends Fragment {
    FragmentSignUpBinding binding;
    FirebaseAuth userAuth;
    DatabaseReference userDatabase;
    FirebaseUser currentUser;
    int latestUserID = 0;
    /**
     * @noinspection deprecation
     */
    ProgressDialog progressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    /**
     * @noinspection deprecation
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Account");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        progressDialog = new ProgressDialog(getActivity());
        binding.txtSignIn.setOnClickListener(v -> {
            FragmentLogIn fragmentLogIn = new FragmentLogIn();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fr_framemain, fragmentLogIn);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        binding.btnSignUp.setOnClickListener(v -> {
            String fullName = binding.edtFullNameSignUp.getText().toString().trim();
            String email = binding.edtEmailSignUp.getText().toString().trim();
            String password = binding.edtPassSignUp.getText().toString().trim();
            String confirmPassword = binding.edtConfirmPassSignUp.getText().toString().trim();

            boolean error = false;
            String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";

            if (TextUtils.isEmpty(fullName)) {
                binding.edtFullNameSignUp.setError("Please enter your Full name!");
                error = true;
            }
            if (TextUtils.isEmpty(email)) {
                binding.edtEmailSignUp.setError("Please enter your Email!");
                error = true;
            } else if (!email.matches(emailPattern)) {
                binding.edtEmailSignUp.setError("Wrong email format!");
                error = true;
            }

            userDatabase.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        binding.edtEmailSignUp.setError("Email already exists. Please use another email!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (TextUtils.isEmpty(password)) {
                binding.edtPassSignUp.setError("Please enter your Password!");
                error = true;

            } else if (password.length() < 6) {
                binding.edtPassSignUp.setError("Please enter more than 6 characters!");
                error = true;
            }
            if (TextUtils.isEmpty(confirmPassword)) {
                binding.edtConfirmPassSignUp.setError("Please confirm your Password!");
                error = true;

            } else if (!confirmPassword.equals(password)) {
                binding.edtConfirmPassSignUp.setError("Your password is not match!");
                error = true;
            }


            if (!error) {
                progressDialog.setMessage("registering...");
                progressDialog.show();
                userAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String userID = dataSnapshot.getKey();
                                        if (userID != null && userID.startsWith("Id")) {
                                            int id = Integer.parseInt(userID.substring(2));
                                            if (id > latestUserID) {
                                                latestUserID = id;
                                            }
                                        }
                                    }

                                    latestUserID++;
                                    String ID = "Id" + latestUserID;
                                    DatabaseReference currentUserDB = userDatabase.child(ID);
                                    currentUserDB.child("fullName").setValue(fullName);
                                    currentUserDB.child("email").setValue(email);
                                    currentUserDB.child("role").setValue("Customer");

                                    progressDialog.dismiss();
                                    Toast.makeText(getActivity(), "Sign Up Successful.", Toast.LENGTH_SHORT).show();
                                    FragmentLogIn fragmentLogIn = new FragmentLogIn();
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fr_framemain, fragmentLogIn);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    progressDialog.dismiss();
                                }
                            });
                        }else {
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}
