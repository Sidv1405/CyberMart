package com.vdsl.cybermart.Account.Fragment;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.saadahmedsoft.popupdialog.listener.OnDialogButtonClickListener;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.databinding.DialogPasswordBinding;
import com.vdsl.cybermart.databinding.FragmentUpdateStaffsBinding;

import java.util.HashMap;
import java.util.Map;

public class FragmentUpdateStaff extends Fragment {

    FragmentUpdateStaffsBinding binding;
    DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentUpdateStaffsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showInitData();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account");
        binding.btnUpdateStaff.setOnClickListener(v -> {
            updateStaff();
        });
        binding.SwLockAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean isChecked= binding.SwLockAccount.isChecked();
                if (isChecked) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("LOCK ACCOUNT");
                    builder.setMessage("Are you sure to lock this staff?");

                    builder.setNegativeButton("NO", (dialog, which) -> {
                    binding.SwLockAccount.setChecked(false);
                        builder.create().dismiss();
                    });
                    builder.setPositiveButton("YES", (dialog, which) -> {
                        Bundle bundle = getArguments();
                        String staffId = bundle.getString("staffId");
                        DatabaseReference staffRef = databaseReference.child(staffId);
                        staffRef.child("active").setValue("Not working").addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Account Locked Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Failed to Lock Account", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                    builder.create().show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle("UNLOCK ACCOUNT");
                    builder.setMessage("Are you sure to unlock this account?");

                    builder.setNegativeButton("NO", (dialog, which) -> {
                    binding.SwLockAccount.setChecked(true);
                        builder.create().dismiss();
                    });
                    builder.setPositiveButton("YES", (dialog, which) -> {
                        Bundle bundle = getArguments();
                        String staffId = bundle.getString("staffId");
                        DatabaseReference staffRef = databaseReference.child(staffId);
                        staffRef.child("active").setValue("Working").addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Account Unlocked Successfully",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Failed to Unlock Account",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                    builder.create().show();
                }
            }
        });
//        binding.SwLockAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//                builder.setTitle("LOCK ACCOUNT");
//                builder.setMessage("Are you sure to lock this staff?");
//
//                builder.setNegativeButton("NO", (dialog, which) -> {
////                    binding.SwLockAccount.setChecked(false);
//                    builder.create().dismiss();
//                });
//                builder.setPositiveButton("YES", (dialog, which) -> {
//                    Bundle bundle = getArguments();
//                    String staffId = bundle.getString("staffId");
//                    DatabaseReference staffRef = databaseReference.child(staffId);
//                    staffRef.child("active").setValue("Not working").addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getActivity(), "Account Locked Successfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity(), "Failed to Lock Account", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                });
//                builder.create().show();
//            } else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
//                builder.setTitle("UNLOCK ACCOUNT");
//                builder.setMessage("Are you sure to unlock this account?");
//
//                builder.setNegativeButton("NO", (dialog, which) -> {
////                    binding.SwLockAccount.setChecked(true);
//                    builder.create().dismiss();
//                });
//                builder.setPositiveButton("YES", (dialog, which) -> {
//                    Bundle bundle = getArguments();
//                    String staffId = bundle.getString("staffId");
//                    DatabaseReference staffRef = databaseReference.child(staffId);
//                    staffRef.child("active").setValue("Working").addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(getActivity(), "Account Unlocked Successfully",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(getActivity(), "Failed to Unlock Account",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                });
//                builder.create().show();
//            }
//
//        });
        binding.layoutStaffPass.setOnClickListener(v -> {
            changeStaffPass();
        });
    }

    private void showInitData() {
        Bundle bundle = getArguments();
        String fullName = bundle.getString("fullName");
        String phoneNumber = bundle.getString("phoneNumber");
        String email = bundle.getString("email");
        String password = bundle.getString("password");
        String active = bundle.getString("active");
        binding.edtStaffName.setText(fullName);
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            binding.edtStaffPhone.setText("No number yet");
            binding.edtStaffPhone.setTextColor(Color.RED);
            binding.edtStaffPhone.setOnClickListener(v -> {
                binding.edtStaffPhone.setTextColor(Color.BLACK);
                binding.edtStaffPhone.setText(null);
            });
        } else {
            binding.edtStaffPhone.setText(phoneNumber);
        }
        if (active != null && active.equals("Not working")) {
            binding.SwLockAccount.setChecked(true);
        } else if (active != null && active.equals("Working")) {
            binding.SwLockAccount.setChecked(false);
        } else {
            binding.SwLockAccount.setChecked(false);
        }
        binding.edtStaffEmail.setText(email);
        binding.txtStaffPassword.setText("password");
    }

    private void updateStaff() {
        String fullName = binding.edtStaffName.getText().toString().trim();
        String phoneNumber = binding.edtStaffPhone.getText().toString().trim();
        boolean error = false;
        if (fullName.isEmpty()) {
            binding.edtStaffName.setError("Please enter staff name!");
            error = true;
        }
        if (phoneNumber.isEmpty()) {
            binding.edtStaffPhone.setError("Please enter staff phone number!");
            error = true;
        } else {
            if (phoneNumber.length() != 10) {
                binding.edtStaffPhone.setError("you just can enter 10 characters!");
                error = true;
            }
            if (!phoneNumber.startsWith("0")) {
                binding.edtStaffPhone.setError("Please start with '0'!");
                error = true;
            }
        }

        if (!error) {
            Bundle bundle = getArguments();
            String staffId = bundle.getString("staffId");
            DatabaseReference staffRef = databaseReference.child(staffId);
            Map<String, Object> updateStaff = new HashMap<>();
            updateStaff.put("fullName", fullName);
            updateStaff.put("phoneNumber", phoneNumber);
            staffRef.updateChildren(updateStaff).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    General.showSuccessPopup(requireContext(), "Successfully", "You're updated " +
                                    "staff have Id: '" + staffId + "'",
                            new OnDialogButtonClickListener() {
                                @Override
                                public void onDismissClicked(Dialog dialog) {
                                    super.onDismissClicked(dialog);
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                }
                            });
                } else {
                    General.showFailurePopup(requireContext(), "Failed", "Failed to update staff information", new OnDialogButtonClickListener() {
                        @Override
                        public void onDismissClicked(Dialog dialog) {
                            super.onDismissClicked(dialog);
                        }
                    });
                }
            });
        }

    }


    private void changeStaffPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getLayoutInflater();
        DialogPasswordBinding dialogPass = DialogPasswordBinding.inflate(inflater);
        builder.setView(dialogPass.getRoot());
        AlertDialog dialog = builder.create();
        dialog.show();

        dialogPass.btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialogPass.txtTitle.setText("Change staff's password");
        dialogPass.txtSendResetEmail.setVisibility(View.VISIBLE);
        dialogPass.tipOldPass.setVisibility(View.GONE);
        dialogPass.tipNewPass.setVisibility(View.GONE);
        dialogPass.tipReNewPass.setVisibility(View.GONE);
        dialogPass.btnDone.setText("SEND");
        dialogPass.btnDone.setOnClickListener(v -> {
            Bundle bundle = getArguments();
            String staffId = bundle.getString("staffId");
            DatabaseReference staffRef = databaseReference.child(staffId);
            staffRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String staffEmail = dataSnapshot.child("email").getValue(String.class);

                    FirebaseAuth.getInstance().sendPasswordResetEmail(staffEmail)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password reset email sent successfully", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getActivity(), "Failed to send password reset email", Toast.LENGTH_SHORT).show();
                                }
                            });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        });
    }


}
