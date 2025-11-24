package com.wei.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wei.music.activity.search.SearchMainFragment;
import com.wei.music.activity.search.SearchResultFragment;

public class SearchPagerAdapter extends FragmentStateAdapter {

    private final Fragment[] pagers = new Fragment[]{
            SearchMainFragment.newInstance(null, null),
            SearchResultFragment.newInstance(null, null)
    };

    public SearchPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return pagers[position];
    }

    @Override
    public int getItemCount() {
        return pagers.length;
    }


}
