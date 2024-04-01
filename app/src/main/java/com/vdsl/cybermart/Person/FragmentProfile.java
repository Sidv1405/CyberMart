package com.vdsl.cybermart.Person;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Account.Activity.LoginActivity;
import com.vdsl.cybermart.Account.Fragment.FragmentAddStaff;
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
    private ActivityResultLauncher activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                Intent intent = o.getData();
                if (intent == null) {
                    return;
                }

                Uri uri = intent.getData();
                try {
                    //noinspection deprecation
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    saveImageToFirebase(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    FragmentProfileBinding binding;
    private FirebaseAuth auth;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    FirebaseUser currentUser;

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
        sharedPreferences = getActivity().getSharedPreferences("Users", Context.MODE_PRIVATE);
        currentUser = auth.getCurrentUser();

        //show infor
        showInitInfor();
        //end

        //back
        binding.imgBack.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });//end

        //sign out
        binding.imgLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout your accont");
            builder.setMessage("Are you sure to log out?");

            builder.setNegativeButton("NO", (dialog, which) -> {
                builder.create().cancel();
            });

            builder.setPositiveButton("YES", (dialog, which) -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            });

            builder.create().show();

        });
        //end

        binding.imgAvatar.setOnClickListener(v -> {
            openAvatarDialog();
        });
        binding.imgEditAvatar.setOnClickListener(v -> {
            openAvatarDialog();
        });


        binding.CvCreateStaff.setOnClickListener(v -> {
            FragmentAddStaff fragmentAddStaff = new FragmentAddStaff();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentAddStaff);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.btnMyOrder.setOnClickListener(v -> {
            General.loadFragment(getParentFragmentManager(), new FragmentContainer(), null);
        });
        binding.cvCateManage.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), CategoryManagementActivity.class));
        });
        binding.cvProdManage.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ProductManagementActivity.class));
        });
        binding.btnMyVoucher.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), VoucherActivity.class);
            startActivity(intent);
        });
        binding.CvSettings.setOnClickListener(v -> {
            FragmentSetting fragmentSetting = new FragmentSetting();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frag_container_main, fragmentSetting);
            transaction.addToBackStack(null);
            transaction.commit();
        });
        binding.btnStatistic.setOnClickListener(v -> {
            General.loadFragment(getParentFragmentManager(), new StatisticFragment(), null);
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
                                String FullName = dataSnapshot.child("fullName").getValue(String.class);
                                String Email = dataSnapshot.child("email").getValue(String.class);
                                String Avatar = dataSnapshot.child("avatar").getValue(String.class);
                                String Role = dataSnapshot.child("role").getValue(String.class);
                                binding.txtYourName.setText(FullName);
                                binding.txtYourEmail.setText(Email);
                                Log.d("loginnow", "onDataChange: " + FullName);
                                Log.d("loginnow", "onDataChange: " + Email);
                                Log.d("loginnow", "onDataChange: " + Avatar);
                                if (Avatar != null && !Avatar.isEmpty()) {
                                    Picasso.get().load(Avatar).into(binding.imgAvatar, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.d("Avatar", "Avatar: " + Avatar);
                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            binding.imgAvatar.setImageResource(R.drawable.img_default_profile_image);
                                        }
                                    });
                                } else {
                                    binding.imgAvatar.setImageResource(R.drawable.img_default_profile_image);
                                }
                                if (Role != null && Role.equals("Admin")) {
                                    binding.CvCreateStaff.setVisibility(View.VISIBLE);
                                    Log.d("loginnow", "Role: " + Role);
                                } else {
                                    binding.CvCreateStaff.setVisibility(View.GONE);
                                    Log.d("loginnow", "Role: " + Role);
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

        if (auth.getCurrentUser() != null) {
            Log.d("loginnow", "logged in");
            databaseReference.orderByChild("email").equalTo(currentUser.getEmail()).addValueEventListener(new ValueEventListener() {
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
            upDateAvatar();
        });
        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    //new
    private void upDateAvatar() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }
        if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
            getActivity().requestPermissions(permission, MY_REQUEST_CODE);
        }
    }

    //new
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    //new
    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select picture!"));
    }

    private void saveImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            String uid = sharedPreferences.getString("ID", null);
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Account").child(uid);
            userRef.child("avatar").setValue(imageUri.toString());
        }
    }
}