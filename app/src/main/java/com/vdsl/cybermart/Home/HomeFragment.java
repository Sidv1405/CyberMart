package com.vdsl.cybermart.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vdsl.cybermart.Category.Category;
import com.vdsl.cybermart.Category.CategoryAdapter;
import com.vdsl.cybermart.Category.Category_Element;
import com.vdsl.cybermart.Category.Category_ElementAdapter;
import com.vdsl.cybermart.R;
import com.vdsl.cybermart.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;

    CategoryAdapter adapter;
    Category_ElementAdapter elementAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new CategoryAdapter(getContext());


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        binding.rcvCategory.setLayoutManager(linearLayoutManager);
        adapter.setData(getCategoryList());
        binding.rcvCategory.setAdapter(adapter);
    }

    private List<Category> getCategoryList() {
        List<Category_Element> list = new ArrayList<>();

        list.add(new Category_Element(R.drawable.clothes_nightwear_outfit_svgrepo_com, "Clothes"));
        list.add(new Category_Element(R.drawable.clothes_nightwear_outfit_svgrepo_com, "Clothes"));
        list.add(new Category_Element(R.drawable.clothes_nightwear_outfit_svgrepo_com, "Clothes"));
        list.add(new Category_Element(R.drawable.clothes_nightwear_outfit_svgrepo_com, "Clothes"));
        list.add(new Category_Element(R.drawable.clothes_nightwear_outfit_svgrepo_com, "Clothes"));

        List<Category> categoryList = new ArrayList<>();
        categoryList.add(new Category(list));

        return categoryList;
    }

}
