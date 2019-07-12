package com.example.customrecyclerview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Seaman on 2019-07-12.
 * Banggood Ltd
 */
public class RecomPagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<ItemFragment> itemFragments = new ArrayList<>();

    public RecomPagerAdapter(FragmentManager fm) {
        super(fm);
        for (int i = 0; i < 8; i++) {
            itemFragments.add(ItemFragment.newInstance(2));
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
        return itemFragments.get(position);
    }

    @Override
    public int getCount() {
        return itemFragments.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "Tab " + (position + 1);
    }
}
