package com.vdsl.cybermart.Home.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vdsl.cybermart.Home.Adapter.CategoryAdapter;
import com.vdsl.cybermart.Home.Adapter.ProductAdapter;
import com.vdsl.cybermart.Home.Model.CategoryModel;
import com.vdsl.cybermart.Home.Model.ProductModel;
import com.vdsl.cybermart.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//Category
        DatabaseReference cateReference = FirebaseDatabase.getInstance().getReference().child("categories");

        RecyclerView rcvCategory = binding.rcvCategory;
        rcvCategory.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        FirebaseRecyclerOptions<CategoryModel> options =
                new FirebaseRecyclerOptions.Builder<CategoryModel>()
                        .setQuery(cateReference, CategoryModel.class)
                        .build();

        categoryAdapter = new CategoryAdapter(options);
        rcvCategory.setAdapter(categoryAdapter);

//Product
        DatabaseReference prodReference = FirebaseDatabase.getInstance().getReference().child("products");
        RecyclerView rcvProduct = binding.rcvProduct;
        rcvProduct.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        FirebaseRecyclerOptions<ProductModel> options1 =
                new FirebaseRecyclerOptions.Builder<ProductModel>()
                        .setQuery(prodReference, ProductModel.class)
                        .build();

        productAdapter = new ProductAdapter(options1);
        rcvProduct.setAdapter(productAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        categoryAdapter.startListening();
        productAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        categoryAdapter.stopListening();
        productAdapter.stopListening();
    }
}
