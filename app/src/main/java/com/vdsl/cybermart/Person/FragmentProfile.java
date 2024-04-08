package com.vdsl.cybermart.Person;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Account.Activity.LoginActivity;
import com.vdsl.cybermart.Account.Fragment.FragmentManagementStaff;
import com.vdsl.cybermart.Account.Fragment.FragmentSetting;
import com.vdsl.cybermart.CategoryManagement.View.CategoryManagementActivity;
import com.vdsl.cybermart.General;
import com.vdsl.cybermart.Order.Fragment.FragmentContainer;
import com.vdsl.cybermart.ProductManagement.View.ProductManagementActivity;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.Statistic.Fragment.StatisticFragment;
import com.vdsl.cybermart.Voucher.View.VoucherActivity;
import com.vdsl.cybermart.databinding.FragmentProfileBinding;

import java.io.IOException;

public class FragmentProfile extends Fragment {


    //new
    private static final int MY_REQUEST_CODE = 99;
//    private ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
//        @Override
//        public void onActivityResult(ActivityResult o) {
//            if (o.getResultCode() == getActivity().RESULT_OK) {
//                Intent intent = o.getData();
//                if (intent == null) {
//                    return;
//                }
//
//                Uri uri = intent.getData();
//                try {
//                    //noinspection deprecation
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
//                    saveImageToFirebase(uri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    });

    FragmentProfileBinding binding;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    FirebaseUser currentUser;

    ProgressDialog progressDialog;

    private FirebaseAuth auth;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Account");
        sharedPreferences = requireActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        currentUser = auth.getCurrentUser();
        progressDialog = new ProgressDialog(getActivity());

        //role and menu
        showItemMenu();

        //show infor
        showInitInfor();
        //back
        binding.imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        //sign out
        binding.imgLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout your accont");
            builder.setMessage("Are you sure to log out?");

            builder.setNegativeButton("NO", (dialog, which) -> builder.create().cancel());

            builder.setPositiveButton("YES", (dialog, which) -> {
                progressDialog.setMessage("Loging out...");
                progressDialog.show();

                FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            FirebaseAuth.getInstance().signOut();
                            if (requireActivity() != null) {
                                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        }
                    }
                });
            });

            builder.create().show();

        });
        //end

        binding.imgAvatar.setOnClickListener(v -> openAvatarDialog());
        binding.imgEditAvatar.setOnClickListener(v -> openAvatarDialog());

        binding.CvCreateStaff.setOnClickListener(v -> {
            FragmentManagementStaff fragmentManagementStaff = new FragmentManagementStaff();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentManagementStaff);
            transaction.addToBackStack(null);
            transaction.commit();
        });


        binding.btnMyOrder.setOnClickListener(v -> General.loadFragment(getParentFragmentManager(), new FragmentContainer(), null));
        binding.cvCateManage.setOnClickListener(v -> startActivity(new Intent(getContext(), CategoryManagementActivity.class)));
        binding.cvProManage.setOnClickListener(v -> startActivity(new Intent(getContext(), ProductManagementActivity.class)));
        binding.btnMyVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VoucherActivity.class);
            startActivity(intent);
        });
        binding.CvSettings.setOnClickListener(v5 -> {
            FragmentSetting fragmentSetting = new FragmentSetting();
            FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentSetting);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.btnStatistic.setOnClickListener(v -> General.loadFragment(getParentFragmentManager(), new StatisticFragment(), null));
    }

    private void showItemMenu() {
        String role = sharedPreferences.getString("role", "");
        Log.d("TAG", "showItemMenu: " + role);
        if (role.equals("Admin")) {
            binding.cvCateManage.setVisibility(View.VISIBLE);
            binding.cvProManage.setVisibility(View.VISIBLE);
            binding.btnMyVoucher.setVisibility(View.VISIBLE);
            binding.btnStatistic.setVisibility(View.VISIBLE);
            binding.CvCreateStaff.setVisibility(View.VISIBLE);
        } else if (role.equals("Staff")) {
            binding.cvCateManage.setVisibility(View.VISIBLE);
            binding.cvProManage.setVisibility(View.VISIBLE);
        }
    }


    private void showInitInfor() {
        Log.d("loginnow", "logged in");
        String email = sharedPreferences.getString("email", "nothing to show");
        databaseReference.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
//                    String userId = sharedPreferences.getString("ID", null);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String userId = dataSnapshot.getKey();
                        if (userId != null) {
                            String fullName = sharedPreferences.getString("fullName", "nothing to show");
                            String avatar = dataSnapshot.child("avatar").getValue(String.class);
                            String role = sharedPreferences.getString("role", "nothing to show");
                            binding.txtYourName.setText(fullName);
                            binding.txtYourEmail.setText(email);
                            Log.d("loginnow", "fname: " + fullName);
                            Log.d("loginnow", "email: " + email);
                            Log.d("loginnow", "avatar: " + avatar);
                            if (avatar != null && !avatar.isEmpty()) {
                                Picasso.get().load(avatar).into(binding.imgAvatar, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("Avatar", "Avatar: " + avatar);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        binding.imgAvatar.setImageResource(R.drawable.img_default_profile_image);
                                    }
                                });
                            } else {
                                binding.imgAvatar.setImageResource(R.drawable.img_default_profile_image);
                            }
                            if (!role.isEmpty() && !role.equals("nothing to show") && role.equals("Admin")) {
                                binding.CvCreateStaff.setVisibility(View.VISIBLE);
                                Log.d("loginnow", "Role: " + role);
                            } else {
                                binding.CvCreateStaff.setVisibility(View.GONE);
                                Log.d("loginnow", "Role: " + role);
                            }
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //new
    public void openAvatarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_update_avatar, null);
        builder.setView(view);
        Dialog dialog = builder.create();
        dialog.show();

        ImageView dialogAvatar = view.findViewById(R.id.dialogAvatar);
        Button btnEdit = view.findViewById(R.id.btnEdit);
        Button btnDone = view.findViewById(R.id.btnDone);
        String ID = sharedPreferences.getString("ID", null);
        if (ID != null) {
            Log.d("loginnow", "logged in");
            String email = sharedPreferences.getString("email", null);
            databaseReference.orderByChild("email").equalTo(email).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            String userId = dataSnapshot.getKey();
                            if (userId != null) {
                                String Avatar = dataSnapshot.child("avatar").getValue(String.class);
                                if (Avatar != null && !Avatar.isEmpty()) {
                                    Picasso.get().load(Avatar).into(dialogAvatar, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("Avatar", "Avatar: " + Avatar);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            dialogAvatar.setImageResource(R.drawable.img_default_profile_image);
                                        }
                                    });
                                } else {
                                    dialogAvatar.setImageResource(R.drawable.img_default_profile_image);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            Log.d("loginnow", "not logged in");
        }

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, MY_REQUEST_CODE);
        });
        btnDone.setOnClickListener(v -> dialog.dismiss());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri avatarUri = data.getData();
            updateAvatar(avatarUri);
        }
    }

    private void updateAvatar(Uri avatarUri) {
        String ID = sharedPreferences.getString("ID", null);
        if (ID != null) {
            String imagePath = avatarUri.toString();
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imageRef = storageRef.child("avatars/" + ID + ".jpg");

            imageRef.putFile(avatarUri).addOnSuccessListener(taskSnapshot -> {
                // Lưu ảnh thành công, lấy URL của ảnh
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    // Lưu URL của ảnh cùng với ID vào Firebase Realtime Database hoặc Firestore
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Account").child(ID);
                    userRef.child("avatar").setValue(imageUrl).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Avatar updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update avatar", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Failed to upload avatar", Toast.LENGTH_SHORT).show();
            });
        }

    }


    //new
//    private void upDateAvatar() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            openGallery();
//            return;
//        }
//        if (getActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            openGallery();
//        } else {
//            String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
//            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
//        }
//    }
//
////    new
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == MY_REQUEST_CODE) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                openGallery();
//            }
//        }
//    }
//
////    new
//    private void openGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        activityResultLauncher.launch(Intent.createChooser(intent, "Select picture!"));
//    }
//
//    private void saveImageToFirebase(Uri imageUri) {
//        if (imageUri != null) {
//            String uid = sharedPreferences.getString("ID", null);
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Account").child(uid);
//            userRef.child("avatar").setValue(imageUri.toString());
//        }
//    }
}