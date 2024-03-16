package com.vdsl.cybermart.CategoryManagement.Adapter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Home.Model.CategoryModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.ItemCategoryBinding;
import com.vdsl.cybermart.databinding.ItemCategoryManagementBinding;

import java.util.Objects;

public class CateManageAdapter extends FirebaseRecyclerAdapter<CategoryModel, CateManageAdapter.CateManageViewHolder> {

    public CateManageAdapter(@NonNull FirebaseRecyclerOptions<CategoryModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CateManageViewHolder cateManageViewHolder, int position, @NonNull CategoryModel categoryModel) {
        cateManageViewHolder.bind(categoryModel.getTitle(), categoryModel.getImage(), categoryModel.isStatus());

        cateManageViewHolder.cateManaBinding.cvItemCategory.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle("Update Category");

            LinearLayout layout = new LinearLayout(v.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);

            TextInputLayout txtNameCate = new TextInputLayout(layout.getContext());
            txtNameCate.setHint("Name:");
            TextInputEditText edtNameCate = new TextInputEditText(layout.getContext());
            edtNameCate.setText(categoryModel.getTitle());
            txtNameCate.addView(edtNameCate);
            layout.addView(txtNameCate);

            ImageView imageView = new ImageView(layout.getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageView.setMaxWidth(550);
            imageView.setMaxHeight(550);
            Picasso.get().load(categoryModel.getImage()).resize(550, 550).centerInside().into(imageView);
            layout.addView(imageView);

            TextInputLayout textInputLayoutImageUrl = new TextInputLayout(layout.getContext());
            textInputLayoutImageUrl.setHint("Image URL:");
            TextInputEditText edtImageUrl = new TextInputEditText(layout.getContext());
            edtImageUrl.setText(categoryModel.getImage());
            textInputLayoutImageUrl.addView(edtImageUrl);
            layout.addView(textInputLayoutImageUrl);

            TextView txtSpinner = new TextView(layout.getContext());
            txtSpinner.setText(" Status:");
            txtSpinner.setTextSize(13);
            layout.addView(txtSpinner);
            Spinner spinnerCate = new Spinner(imageView.getContext());

            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_spinner_item, new String[]{"Active", "Inactive"});
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCate.setAdapter(spinnerAdapter);
            layout.addView(spinnerCate);

            builder.setView(layout);

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference rootReference = firebaseDatabase.getReference();
            DatabaseReference cateReference = rootReference.child("categories");
            builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean newStatus;
                    String updatedTitle = edtNameCate.getText().toString();
                    String updatedImage = edtImageUrl.getText().toString();
                    String selectedStatus = spinnerCate.getSelectedItem().toString();
                    if (selectedStatus.equals("Active")) {
                        newStatus = true;
                    } else {
                        newStatus = false;
                    }

                    DatabaseReference categoryRef = cateReference.child(getRef(position).getKey());
                    categoryRef.child("title").setValue(updatedTitle);
                    categoryRef.child("image").setValue(updatedImage);
                    categoryRef.child("status").setValue(newStatus);

                    dialog.dismiss();

                }
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });
    }

    @NonNull
    @Override
    public CateManageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCategoryManagementBinding binding = ItemCategoryManagementBinding.inflate(layoutInflater, parent, false);
        View view = binding.getRoot();
        return new CateManageAdapter.CateManageViewHolder(view, binding);
    }

    public static class CateManageViewHolder extends RecyclerView.ViewHolder {
        private final ItemCategoryManagementBinding cateManaBinding;

        public CateManageViewHolder(@NonNull View itemView, ItemCategoryManagementBinding cateManaBinding) {
            super(itemView);
            this.cateManaBinding = cateManaBinding;
        }

        public void bind(String categoryName, String categoryImage, boolean status) {
            cateManaBinding.titleCategory.setText(categoryName);
            Picasso.get().load(categoryImage).into(cateManaBinding.imgCategory);

            if (!status) {
                itemView.setBackgroundColor(Color.GRAY);
            }
        }
    }
}