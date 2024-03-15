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

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.CategoryManagement.Adapter.CateManageAdapter;
import com.vdsl.cybermart.Home.Adapter.CategoryAdapter;
import com.vdsl.cybermart.Home.Model.CategoryModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.DialogAddCategoryBinding;

public class CategoryManagementActivity extends AppCompatActivity {
    private DatabaseReference cateReference;
    private CateManageAdapter adapter;

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

//        get all
        cateReference = FirebaseDatabase.getInstance().getReference().child("categories");
        RecyclerView rcvCategory = findViewById(R.id.rcv_category_management);
        rcvCategory.setLayoutManager(new GridLayoutManager(this, 3));

        FirebaseRecyclerOptions<CategoryModel> options =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(cateReference, CategoryModel.class)
                        .build();

        adapter = new CateManageAdapter(options);
        rcvCategory.setAdapter(adapter);

//        Add
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

                Picasso.get().load(image).into(addCategoryBinding.imgAddCate);

                DatabaseReference newCategoryRef = cateReference.push();
                String categoryId = newCategoryRef.getKey();

                CategoryModel categoryModel = new CategoryModel(categoryId, title, image, true);

                newCategoryRef.setValue(categoryModel);
                alertDialog.dismiss();
            });

            addCategoryBinding.btnCancelAddCate.setOnClickListener(v1 -> alertDialog.dismiss());

        });

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