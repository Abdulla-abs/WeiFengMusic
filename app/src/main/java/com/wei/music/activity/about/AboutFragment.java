package com.wei.music.activity.about;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.wei.music.BuildDependencies;
import com.wei.music.databinding.AboutFragmentBinding;

import java.util.ArrayList;

public class AboutFragment extends Fragment {

    private AboutFragmentBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = AboutFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ThankAdapter adapter = new ThankAdapter(new DiffUtil.ItemCallback<String>() {
            @Override
            public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
                return true;
            }
        });
        binding.aboutThankRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.aboutThankRecycler.setAdapter(adapter);
        ArrayList<String> list = new ArrayList<>(BuildDependencies.LIBRARIES);
        list.add(0, "致谢：");
        adapter.submitList(list);

    }
}
