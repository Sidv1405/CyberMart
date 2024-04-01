package com.vdsl.cybermart.Message.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.vdsl.cybermart.Message.ChatsFragment;
import com.vdsl.cybermart.Message.ListCustomFragment;
import com.vdsl.cybermart.Order.Fragment.CanceledOrderFragment;
import com.vdsl.cybermart.Order.Fragment.DeliveredFragment;
import com.vdsl.cybermart.Order.Fragment.ProcessingOrderFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: {
                return new ChatsFragment();
            }
            case 1: {
                return new ListCustomFragment();
            }
            default:
                return new ChatsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
