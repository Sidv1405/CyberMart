package com.vdsl.cybermart.Home.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;
import com.vdsl.cybermart.Home.Model.Banner;
import com.vdsl.cybermart.R;

import java.util.List;

public class BannerAdapter extends PagerAdapter {
    private final List<Banner> listBanner;

    public BannerAdapter(List<Banner> listBanner) {
        this.listBanner = listBanner;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_banner, container, false);
        ImageView img = view.findViewById(R.id.img_banner);

        Banner banner = listBanner.get(position);
        if (banner != null) {
            Picasso.get().load(banner.getResourceId()).into(img);
        }
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return listBanner.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
