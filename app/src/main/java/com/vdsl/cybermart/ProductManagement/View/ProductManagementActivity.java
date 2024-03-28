package com.vdsl.cybermart.ProductManagement.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.ProductManagement.Adapter.ProdManageAdapter;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.DialogAddProductBinding;

import java.util.ArrayList;
import java.util.Objects;


public class ProductManagementActivity extends AppCompatActivity {
    private DatabaseReference prodReference;
    private ProdManageAdapter adapter;
    private RecyclerView rcvProduct;
    private Spinner spinnerCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        rcvProduct = findViewById(R.id.rcv_product_management);

        readDataProduct();

        createDataProduct();

        SearchView searchView = findViewById(R.id.search_product_management);
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
        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("products").orderByChild("name").startAt(text).endAt(text + "~"), ProductModel.class)
                        .build();
        adapter = new ProdManageAdapter(options);
        adapter.startListening();
        rcvProduct.setAdapter(adapter);
    }

    private void createDataProduct() {
        FloatingActionButton btnAdd = findViewById(R.id.btn_add_product);
        btnAdd.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = getLayoutInflater();
            DialogAddProductBinding addProdBinding = DialogAddProductBinding.inflate(inflater);

            builder.setView(addProdBinding.getRoot());
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Objects.requireNonNull(alertDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            spinnerCategory = addProdBinding.spCate;
            DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference().child("categories");
            ArrayList<String> categoryList = new ArrayList<>();
            categoryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    categoryList.clear();
                    for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                        String categoryName = categorySnapshot.child("title").getValue(String.class);
                        if (categoryName != null) {
                            categoryList.add(categoryName);
                        }
                    }
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(ProductManagementActivity.this, android.R.layout.simple_spinner_item, categoryList);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(categoryAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            addProdBinding.btnAcceptAddProd.setOnClickListener(v1 -> {
                String name = Objects.requireNonNull(addProdBinding.edtAddNameProd.getText()).toString();
                double price = Double.parseDouble(Objects.requireNonNull(addProdBinding.edtAddPriceProd.getText()).toString());
                int quantity = Integer.parseInt(Objects.requireNonNull(addProdBinding.edtAddCountProd.getText()).toString());
                String description = Objects.requireNonNull(addProdBinding.edtAddDescProd.getText()).toString();
                String image = Objects.requireNonNull(addProdBinding.edtAddUrlProd.getText()).toString();
                String cateName = spinnerCategory.getSelectedItem().toString();

                prodReference.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Had same title", Toast.LENGTH_SHORT).show();
                        } else {
                            DatabaseReference newProdRef = prodReference.push();

                            ProductModel productModel = new ProductModel(name, description, price, quantity, image, cateName, true);

                            newProdRef.setValue(productModel);
                            alertDialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Error query db", Toast.LENGTH_SHORT).show();
                    }
                });
            });
            addProdBinding.btnCancelAddProd.setOnClickListener(v1 -> alertDialog.dismiss());
        });
    }

    private void readDataProduct() {
        prodReference = FirebaseDatabase.getInstance().getReference().child("products");
        rcvProduct.setLayoutManager(new GridLayoutManager(this, 2));

        FirebaseRecyclerOptions<ProductModel> options =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(prodReference, ProductModel.class)
                        .build();

        adapter = new ProdManageAdapter(options);
        rcvProduct.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) // Check if adapter is not null before calling startListening()
            adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

}