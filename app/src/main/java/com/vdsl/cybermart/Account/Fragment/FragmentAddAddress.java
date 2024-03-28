package com.vdsl.cybermart.Account.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.vdsl.cybermart.Account.Model.AddressModel;
import com.vdsl.cybermart.databinding.FragmentAddAddressBinding;

public class FragmentAddAddress extends Fragment {
    FragmentAddAddressBinding binding;
    DatabaseReference databaseReference, addressRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account").child("Id9").child("address");
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
                    if (currentUser != null) {
                        databaseReference.orderByChild("email").equalTo(currentUser.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                        String userId = dataSnapshot.getKey();
                                        if (userId != null) {
                                            String addressId = databaseReference.push().getKey();
                                            addressRef = FirebaseDatabase.getInstance().getReference().child("Account").child(userId).child("address");
                                            AddressModel addressModel = new AddressModel(fullName, country + " - " + city + " - " + district + " - " + descriptiom);
                                            addressRef.child(addressId).setValue(addressModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(getActivity(), "Đã thêm địa chỉ mới thành công", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), "Không thể thêm địa chỉ mới: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

                    }
//                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                                String userId = dataSnapshot.getKey();
//                                if (userId != null) {
//                                    String addressId = databaseReference.push().getKey();
//                                    addressRef = FirebaseDatabase.getInstance().getReference().child("Account").child(userId).child("address");
//                                    AddressModel addressModel = new AddressModel(fullName, country + " - " + city + " - " + district + " - " + descriptiom);
////                                    addressRef.child(addressId).setValue(addressModel).addOnCompleteListener(new OnCompleteListener<Void>() {
////                                        @Override
////                                        public void onComplete(@NonNull Task<Void> task) {
////                                            Toast.makeText(getActivity(), "Đã thêm địa chỉ mới thành công", Toast.LENGTH_SHORT).show();
////                                        }
////                                    });
//                                    addressRef.child(addressId).setValue(addressModel).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void unused) {
//                                            Toast.makeText(getActivity(), "Đã thêm địa chỉ mới thành công", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }).addOnFailureListener(new OnFailureListener() {
//                                        @Override
//                                        public void onFailure(@NonNull Exception e) {
//                                            Toast.makeText(getActivity(), "Không thể thêm địa chỉ mới: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        }
//                                    });
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });


                }
            }
        });
    }


}