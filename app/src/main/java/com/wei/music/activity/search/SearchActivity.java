package com.wei.music.activity.search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.wei.music.adapter.SearchPagerAdapter;
import com.wei.music.databinding.ActivitySearchBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {

    private com.wei.music.databinding.ActivitySearchBinding binding;
    private SearchViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initData();
        observerData();
    }


    private void initView() {
        binding.viewpager.setAdapter(new SearchPagerAdapter(this));


    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);


    }

    private void observerData() {

    }
}
