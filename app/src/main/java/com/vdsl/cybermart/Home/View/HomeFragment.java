package com.vdsl.cybermart.Home.View;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vdsl.cybermart.Cart.View.CartActivity;
import com.vdsl.cybermart.Home.Adapter.BannerAdapter;
import com.vdsl.cybermart.Category.Adapter.CategoryAdapter;
import com.vdsl.cybermart.Product.Adapter.ProductAdapter;
import com.vdsl.cybermart.Home.Model.Banner;
import com.vdsl.cybermart.Category.Model.CategoryModel;
import com.vdsl.cybermart.Product.Model.ProductModel;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<Banner> list;
    private Timer timer;


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

        FirebaseRecyclerOptions<CategoryModel> options = new FirebaseRecyclerOptions.Builder<CategoryModel>().setQuery(cateReference, CategoryModel.class).build();

        categoryAdapter = new CategoryAdapter(options);
        rcvCategory.setAdapter(categoryAdapter);

//Product
        DatabaseReference prodReference = FirebaseDatabase.getInstance().getReference().child("products");
        RecyclerView rcvProduct = binding.rcvProduct;
        rcvProduct.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        FirebaseRecyclerOptions<ProductModel> options1 = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(prodReference, ProductModel.class).build();

        productAdapter = new ProductAdapter(options1);
        rcvProduct.setAdapter(productAdapter);

//Banner
        list = new ArrayList<>();
        list.add(new Banner(R.drawable.banner1));
        list.add(new Banner(R.drawable.banner2));
        list.add(new Banner(R.drawable.banner3));
        list.add(new Banner(R.drawable.banner4));

        ViewPager viewPager = binding.viewPage;
        BannerAdapter bannerAdapter = new BannerAdapter(list);
        viewPager.setAdapter(bannerAdapter);

        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (viewPager.getCurrentItem() < list.size() - 1) {
                            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                        } else {
                            viewPager.setCurrentItem(0);
                        }
                    });
                }
            }, 1234, 2345);
        }

//filter product
        categoryAdapter.setCategoryClickListener(categoryModel -> {
            String categoryId = categoryModel.getTitle();
            Query query = prodReference.orderByChild("categoryId").equalTo(categoryId);
            FirebaseRecyclerOptions<ProductModel> options3 = new FirebaseRecyclerOptions.Builder<ProductModel>().setQuery(query, ProductModel.class).build();

            productAdapter.updateOptions(options3);
        });
//Click cart
        binding.btnCart.setOnClickListener(v -> startActivity(new Intent(v.getContext(), CartActivity.class)));


    }

    //        badge
    private void viewBadge() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Users", MODE_PRIVATE);
        String accountId = sharedPreferences.getString("ID", "");

        DatabaseReference cartsRef = FirebaseDatabase.getInstance().getReference().child("carts");
        Query query = cartsRef.orderByChild("accountId").equalTo(accountId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot cartSnapshot : dataSnapshot.getChildren()) {
                    String cartId = cartSnapshot.getKey();
                    DatabaseReference cartDetailRef = FirebaseDatabase.getInstance().getReference().child("carts").child(cartId).child("cartDetail");
                    cartDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int count = (int) dataSnapshot.getChildrenCount();
                            if (count > 0) {
                                binding.badge.setText(String.valueOf(count));
                            }
                            if (count == 0) {
                                cartsRef.child(accountId).removeValue();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewBadge();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
