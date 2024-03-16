package com.vdsl.cybermart.CategoryManagement.View;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class CategoryManagementActivity extends AppCompatActivity {
    private DatabaseReference cateReference;
    private CateManageAdapter adapter;
    private RecyclerView rcvCategory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_management), (v, insets) -> {
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
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            super.onBackPressed();
        });


        SearchView searchView = findViewById(R.id.search_category_management);
        TextView textView = findViewById(R.id.txt_cate_manage);
        searchView.setOnSearchClickListener(v -> textView.setVisibility(View.GONE));
        searchView.setOnCloseListener(() -> {
            textView.setVisibility(View.VISIBLE);
            return false;
        });
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
        ImageButton btnAdd = findViewById(R.id.btn_add_category);
        btnAdd.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            DialogAddCategoryBinding addCategoryBinding = DialogAddCategoryBinding.inflate(inflater);

            builder.setView(addCategoryBinding.getRoot());
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            addCategoryBinding.btnAcceptAddCate.setOnClickListener(v1 -> {
                String title = addCategoryBinding.edtAddNameCate.getText().toString();
                String image = addCategoryBinding.edtAddUrlCate.getText().toString();

                // Kiểm tra tính duy nhất của tên danh mục
                cateReference.orderByChild("title").equalTo(title).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Tên danh mục đã tồn tại, hiển thị thông báo lỗi
                            Toast.makeText(getApplicationContext(), "Had same title", Toast.LENGTH_SHORT).show();
                        } else {
                            // Tên danh mục chưa tồn tại, thêm danh mục mới
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
                        // Xử lý khi có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu
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