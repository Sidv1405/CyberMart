package com.vdsl.cybermart.CategoryManagement.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.CategoryManagement.Adapter.CateManageAdapter;
import com.vdsl.cybermart.Home.Model.CategoryModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.DialogAddCategoryBinding;

import java.util.Objects;

public class CategoryManagementActivity extends AppCompatActivity {
    private DatabaseReference cateReference;
    private CateManageAdapter adapter;
    private RecyclerView rcvCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rcvCategory = findViewById(R.id.rcv_category_management);

//        get all
        readDataCategory();
//        Add
        createDataCategory();
//        Back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        SearchView searchView = findViewById(R.id.search_category_management);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                txtSearch(newText);
                return false;
            }
        });

    }

    private void txtSearch(String text) {
        FirebaseRecyclerOptions<CategoryModel> options =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("categories").orderByChild("title").startAt(text).endAt(text + "~"), CategoryModel.class)
                        .build();
        adapter = new CateManageAdapter(options);
        adapter.startListening();
        rcvCategory.setAdapter(adapter);
    }

    private void createDataCategory() {
        FloatingActionButton btnAdd = findViewById(R.id.btn_add_category);
        btnAdd.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            DialogAddCategoryBinding addCategoryBinding = DialogAddCategoryBinding.inflate(inflater);

            builder.setView(addCategoryBinding.getRoot());
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            addCategoryBinding.btnAcceptAddCate.setOnClickListener(v1 -> {
                String title = Objects.requireNonNull(addCategoryBinding.edtAddNameCate.getText()).toString();
                String image = Objects.requireNonNull(addCategoryBinding.edtAddUrlCate.getText()).toString();

                cateReference.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Had same title", Toast.LENGTH_SHORT).show();
                        } else {
                            Picasso.get().load(image).into(addCategoryBinding.imgAddCate);

                            DatabaseReference newCategoryRef = cateReference.push();
                            String categoryId = newCategoryRef.getKey();

                            CategoryModel categoryModel = new CategoryModel(categoryId, title, image, true);

                            newCategoryRef.setValue(categoryModel);
                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error query db", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            addCategoryBinding.btnCancelAddCate.setOnClickListener(v1 -> alertDialog.dismiss());
        });
    }

    private void readDataCategory() {
        cateReference = FirebaseDatabase.getInstance().getReference().child("categories");
        rcvCategory.setLayoutManager(new GridLayoutManager(this, 3));

        FirebaseRecyclerOptions<CategoryModel> options =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(cateReference, CategoryModel.class)
                        .build();

        adapter = new CateManageAdapter(options);
        rcvCategory.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}