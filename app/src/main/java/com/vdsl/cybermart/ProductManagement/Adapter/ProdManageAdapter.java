package com.vdsl.cybermart.ProductManagement.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.Product.View.ProductDetailActivity;
import com.vdsl.cybermart.databinding.ItemProductBinding;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ProdManageAdapter extends FirebaseRecyclerAdapter<ProductModel, ProdManageAdapter.ProdManageViewHolder> {


    public ProdManageAdapter(@NonNull FirebaseRecyclerOptions<ProductModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ProdManageViewHolder prodManageViewHolder, int i, @NonNull ProductModel productModel) {
        prodManageViewHolder.bind(productModel.getName(), productModel.getImage(), productModel.getPrice(), productModel.getStatus());

        viewProductDetail(prodManageViewHolder, i);

        updateProduct(prodManageViewHolder, i, productModel);
    }

    private void viewProductDetail(@NonNull ProdManageViewHolder prodManageViewHolder, int i) {
        prodManageViewHolder.itemView.setOnClickListener(v -> {
            ProductModel clickedItem = getItem(i);
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("productName", clickedItem.getName());
            intent.putExtra("productImage", clickedItem.getImage());
            intent.putExtra("productPrice", clickedItem.getPrice());
            intent.putExtra("productDescription", clickedItem.getDescription());
            v.getContext().startActivity(intent);
        });
    }


    @SuppressLint("SetTextI18n")
    private void updateProduct(@NonNull ProdManageViewHolder prodManageViewHolder, int i, @NonNull ProductModel productModel) {
        prodManageViewHolder.binding.cvProd.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Update Product");

            LinearLayout layout = new LinearLayout(v.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            TextInputLayout txtNameProd = new TextInputLayout(layout.getContext());
            txtNameProd.setHint("Name:");
            TextInputEditText edtNamePro = new TextInputEditText(layout.getContext());
            edtNamePro.setText(productModel.getName());
            txtNameProd.addView(edtNamePro);
            layout.addView(txtNameProd);

            ImageView imageView = new ImageView(layout.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setMaxWidth(550);
            imageView.setMaxHeight(550);
            Picasso.get().load(productModel.getImage()).resize(550, 550).centerInside().into(imageView);
            layout.addView(imageView);

            TextInputLayout txtPriceProd = new TextInputLayout(layout.getContext());
            txtPriceProd.setHint("Price:");
            TextInputEditText edtPriceProd = new TextInputEditText(layout.getContext());
            edtPriceProd.setText(String.valueOf(productModel.getPrice()));
            txtPriceProd.addView(edtPriceProd);
            layout.addView(txtPriceProd);

            TextInputLayout txtQuantityProd = new TextInputLayout(layout.getContext());
            txtQuantityProd.setHint("Quantity:");
            TextInputEditText edtQuantityProd = new TextInputEditText(layout.getContext());
            edtQuantityProd.setText(String.valueOf(productModel.getQuantity()));
            txtQuantityProd.addView(edtQuantityProd);
            layout.addView(txtQuantityProd);

            TextInputLayout txtDescProd = new TextInputLayout(layout.getContext());
            txtDescProd.setHint("Description:");
            TextInputEditText edtDescProd = new TextInputEditText(layout.getContext());
            edtDescProd.setText(String.valueOf(productModel.getDescription()));
            txtDescProd.addView(edtDescProd);
            layout.addView(txtDescProd);

            TextView txtSpCate = new TextView(layout.getContext());
            txtSpCate.setText(" Category:");
            txtSpCate.setTextSize(13);
            layout.addView(txtSpCate);
            Spinner spinnerCate = new Spinner(imageView.getContext());

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
                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_spinner_item, categoryList);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCate.setAdapter(categoryAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            layout.addView(spinnerCate);

            TextInputLayout textInputLayoutImageUrl = new TextInputLayout(layout.getContext());
            textInputLayoutImageUrl.setHint("Image URL:");
            TextInputEditText edtImageUrl = new TextInputEditText(layout.getContext());
            edtImageUrl.setText(productModel.getImage());
            textInputLayoutImageUrl.addView(edtImageUrl);
            layout.addView(textInputLayoutImageUrl);


            TextView txtSpinner = new TextView(layout.getContext());
            txtSpinner.setText(" Status:");
            txtSpinner.setTextSize(13);
            layout.addView(txtSpinner);
            Spinner spinnerProd = new Spinner(imageView.getContext());

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_spinner_item, new String[]{"Active", "Inactive"});
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProd.setAdapter(spinnerAdapter);
            layout.addView(spinnerProd);

            builder.setView(layout);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference rootReference = firebaseDatabase.getReference();
            DatabaseReference reference = rootReference.child("products");
            builder.setPositiveButton("Update", (dialog, which) -> {
                boolean newStatus;
                String updatedDesc = Objects.requireNonNull(edtDescProd.getText()).toString();
                String updatedTitle = Objects.requireNonNull(edtNamePro.getText()).toString();
                String updatedImage = Objects.requireNonNull(edtImageUrl.getText()).toString();
                double updatedPrice = Double.parseDouble(Objects.requireNonNull(edtPriceProd.getText()).toString());
                int updatedQuantity = Integer.parseInt(Objects.requireNonNull(edtQuantityProd.getText()).toString());
                String selectedStatus = spinnerProd.getSelectedItem().toString();
                String selectedCaregory = spinnerCate.getSelectedItem().toString();
                newStatus = selectedStatus.equals("Active");

                DatabaseReference prodRef = reference.child(Objects.requireNonNull(getRef(i).getKey()));
                prodRef.child("description").setValue(updatedDesc);
                prodRef.child("name").setValue(updatedTitle);
                prodRef.child("image").setValue(updatedImage);
                prodRef.child("price").setValue(updatedPrice);
                prodRef.child("quantity").setValue(updatedQuantity);
                prodRef.child("status").setValue(newStatus);
                prodRef.child("categoryId").setValue(selectedCaregory);

                dialog.dismiss();

            }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });
    }

    @NonNull
    @Override
    public ProdManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemProductBinding binding = ItemProductBinding.inflate(layoutInflater, parent, false);
        View view = binding.getRoot();
        return new ProdManageAdapter.ProdManageViewHolder(view, binding);
    }

    public static class ProdManageViewHolder extends RecyclerView.ViewHolder {
        private final ItemProductBinding binding;

        public ProdManageViewHolder(@NonNull View itemView, ItemProductBinding binding) {
            super(itemView);
            this.binding = binding;
        }

        public void bind(String prodName, String prodImage, double prodPrice, boolean status) {
            binding.nameProduct.setText(prodName);
            Picasso.get().load(prodImage).into(binding.imgProduct);
            String formattedPrice = String.format(Locale.getDefault(), "%.2f", prodPrice);
            binding.priceProduct.setText(String.format("%s $", formattedPrice));

            if (!status) {
                binding.imgBaned.setVisibility(View.VISIBLE);
            }
        }

    }
}
