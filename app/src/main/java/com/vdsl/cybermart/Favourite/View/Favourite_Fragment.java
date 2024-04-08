package com.vdsl.cybermart.Favourite.View;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vdsl.cybermart.Category.Adapter.CategoryAdapter;
import com.vdsl.cybermart.Category.Model.CategoryModel;
import com.vdsl.cybermart.Favourite.Adapter.FavoriteAdapter;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.databinding.FragmentFavouriteBinding;

public class Favourite_Fragment extends Fragment {
    private FragmentFavouriteBinding binding;
    private FavoriteAdapter favoriteAdapter;

    public Favourite_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Users", Context.MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");
        String favoritesDetailName = "favoritesDetail_" + accountId;
        SharedPreferences favSharedPreferences = getContext().getSharedPreferences(favoritesDetailName, Context.MODE_PRIVATE);
        String favId = favSharedPreferences.getString("favoritesId", "");

        DatabaseReference favReference = FirebaseDatabase.getInstance().getReference().child("favorites");

        RecyclerView rcvFav = binding.rcvFav;
        rcvFav.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseRecyclerOptions<ProductModel> options = new FirebaseRecyclerOptions.Builder<ProductModel>()
                .setQuery(favReference.child(favId).child("listFavorites"), ProductModel.class)
                .build();

        favoriteAdapter = new FavoriteAdapter(options);
        rcvFav.setAdapter(favoriteAdapter);
    }

    @Override
    public void onStart() {
        setupRecyclerView();
        super.onStart();
        favoriteAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        favoriteAdapter.stopListening();
    }

}
