package com.wei.music.activity.search;

import static com.blankj.utilcode.util.SizeUtils.dp2px;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wei.music.R;
import com.wei.music.adapter.HistorySearchAdapter;
import com.wei.music.bean.SearchHistoryVO;
import com.wei.music.databinding.FragmentSearchMainBinding;
import com.wei.music.view.FlowLayoutManager;
import com.wei.music.view.SpaceItemDecoration;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchMainFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
@AndroidEntryPoint
public class SearchMainFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private com.wei.music.databinding.FragmentSearchMainBinding binding;

    public SearchMainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchMainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchMainFragment newInstance(String param1, String param2) {
        SearchMainFragment fragment = new SearchMainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private SearchViewModel searchViewModel;
    private HistorySearchAdapter historySearchAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchMainBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();
        observerData();
    }


    private void initView() {
        binding.rvHistory.addItemDecoration(new SpaceItemDecoration(dp2px(4)));
        binding.rvHistory.setLayoutManager(new FlowLayoutManager());
    }


    private void initData() {
        historySearchAdapter = new HistorySearchAdapter();
        historySearchAdapter.setItemClickListener(new HistorySearchAdapter.OnItemClickListener() {
            @Override
            public void onClickKeywords(String keyword) {

            }

            @Override
            public void onLongClickKeyWords(String keyword) {

            }
        });
        binding.rvHistory.setAdapter(historySearchAdapter);
    }

    private void observerData() {
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);

        searchViewModel.searchHistory.observe(getViewLifecycleOwner(), new Observer<List<SearchHistoryVO>>() {
            @Override
            public void onChanged(List<SearchHistoryVO> searchHistoryVOS) {
                historySearchAdapter.submitList(searchHistoryVOS);
            }
        });
    }
}