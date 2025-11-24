package com.wei.music.activity.search;

import android.os.Bundle;
import android.view.View;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.wei.music.adapter.SearchPagerAdapter;
import com.wei.music.databinding.ActivitySearchBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
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
        binding.viewpager.setUserInputEnabled(false);
        binding.viewpager.setAdapter(new SearchPagerAdapter(this));
        binding.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = binding.searchEdittext.getText().toString();
                viewModel.searchIntentMutableLiveData.postValue(
                        new SearchIntent.Search(content)
                );
            }
        });
    }

    private void initData() {
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.viewpager.getCurrentItem() != 0) {
                    binding.viewpager.setCurrentItem(0, false);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void observerData() {
        viewModel.searchIntentMutableLiveData.observe(this, new Observer<SearchIntent>() {
            @Override
            public void onChanged(SearchIntent searchIntent) {
                if (searchIntent instanceof SearchIntent.Search) {
                    binding.viewpager.setCurrentItem(1, false);
                    viewModel.storeSearchKeywords((SearchIntent.Search) searchIntent);
                }
            }
        });
    }

}
