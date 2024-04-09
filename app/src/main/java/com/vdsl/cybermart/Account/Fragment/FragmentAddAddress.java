package com.vdsl.cybermart.Account.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.databinding.FragmentAddAddressBinding;

import java.util.HashMap;
import java.util.Map;

public class FragmentAddAddress extends Fragment {
    FragmentAddAddressBinding binding;
    DatabaseReference databaseReference, addressRef;
    SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnDeleteAddress.setVisibility(View.GONE);
        sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.btnSaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = binding.edtFullName.getText().toString().trim();
                String country = binding.edtCountry.getText().toString().trim();
                String city = binding.edtCity.getText().toString().trim();
                String district = binding.edtDistrict.getText().toString().trim();
                String descriptiom = binding.edtDescription.getText().toString().trim();
                boolean error = false;
                if (fullName.isEmpty()) {
                    binding.edtFullName.setError("Please enter your name!");
                    error = true;
                }
                if (country.isEmpty()) {
                    binding.edtCountry.setError("Please enter your country!");
                    error = true;
                }
                if (city.isEmpty()) {
                    binding.edtCity.setError("Please enter your city!");
                    error = true;
                }
                if (district.isEmpty()) {
                    binding.edtDistrict.setError("Please enter your district!");
                    error = true;
                }
                if (descriptiom.isEmpty()) {
                    binding.edtDescription.setError("Please enter description!");
                    error = true;
                }

                if ((!error)) {
//                    if (currentUser != null) {
                    String email = sharedPreferences.getString("email", null);
                    databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    String userId = sharedPreferences.getString("ID", null);
                                    if (userId != null) {
                                        String addressId = databaseReference.push().getKey();
                                        addressRef = FirebaseDatabase.getInstance().getReference().child("Account").child(userId).child("address");
                                        Map<String, Object> updateAddress = new HashMap<>();
                                        updateAddress.put("fullName", fullName);
                                        updateAddress.put("address", country + " - " + city + " - " + district + " - " + descriptiom);
                                        addressRef.child(addressId).setValue(updateAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                General.showSuccessPopup(requireActivity(), "Add new address",
                                                        "You have successfully added an address", new OnDialogButtonClickListener() {
                                                            @Override
                                                            public void onDismissClicked(Dialog dialog) {
                                                                super.onDismissClicked(dialog);
                                                                dialog.dismiss();
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                General.showFailurePopup(requireActivity(), "Add new address",
                                                        "Failed added an address",
                                                        new OnDialogButtonClickListener() {
                                                            @Override
                                                            public void onDismissClicked(Dialog dialog) {
                                                                super.onDismissClicked(dialog);
                                                                dialog.dismiss();
                                                            }
                                                        });
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

//                    }
                }
            }
        });
    }


}