package com.wei.music.activity.search;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.wei.music.MusicSessionManager;
import com.wei.music.R;
import com.wei.music.bean.SearchResultDTO;
import com.wei.music.databinding.FragmentSearchResultBinding;
import com.wei.music.mapper.SongsDTOMapper;
import com.wei.music.service.musicaction.MusicIntentContract;
import com.wei.music.utils.Resource;

import java.util.Collections;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class SearchResultFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentSearchResultBinding binding;
    private SearchResultAdapter searchResultAdapter;

    public SearchResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchResultFragment newInstance(String param1, String param2) {
        SearchResultFragment fragment = new SearchResultFragment();
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
    private SearchResultViewModel searchResultViewModel;
    @Inject
    MusicSessionManager musicSessionManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSearchResultBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        searchViewModel = new ViewModelProvider(requireActivity()).get(SearchViewModel.class);
        searchResultViewModel = new ViewModelProvider(this).get(SearchResultViewModel.class);

        initView();
        observerData();
    }

    private void initView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        searchResultAdapter = new SearchResultAdapter(new DiffUtil.ItemCallback<SearchResultDTO.ResultDTO.SongsDTO>() {
            @Override
            public boolean areItemsTheSame(@NonNull SearchResultDTO.ResultDTO.SongsDTO oldItem, @NonNull SearchResultDTO.ResultDTO.SongsDTO newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull SearchResultDTO.ResultDTO.SongsDTO oldItem, @NonNull SearchResultDTO.ResultDTO.SongsDTO newItem) {
                return Objects.equals(oldItem.getStatus(), newItem.getStatus());
            }
        });
        searchResultAdapter.setOnClickListener(new SearchResultAdapter.OnClickListener() {
            @Override
            public void onItemClick(SearchResultDTO.ResultDTO.SongsDTO songsDTO) {
                musicSessionManager.onMusicIntent(
                        new MusicIntentContract.InsertMusicAndPlay(
                                SongsDTOMapper.mapper(songsDTO)
                        )
                );
            }
        });
        binding.recyclerView.setAdapter(searchResultAdapter);
    }

    private void observerData() {
        searchViewModel.searchIntentMutableLiveData.observe(getViewLifecycleOwner(), new Observer<SearchIntent>() {
            @Override
            public void onChanged(SearchIntent searchIntent) {
                if (searchIntent instanceof SearchIntent.Search) {
                    searchResultViewModel.onSearchSong(((SearchIntent.Search) searchIntent).searchKeyWorlds);
                }
            }
        });

        searchResultViewModel.searchResultLiveData.observe(getViewLifecycleOwner(), new Observer<Resource<SearchResultDTO.ResultDTO>>() {
            @Override
            public void onChanged(Resource<SearchResultDTO.ResultDTO> resultDTOResource) {
                if (resultDTOResource instanceof Resource.Success) {
                    searchResultAdapter.submitList(resultDTOResource.getData().getSongs());
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.progressbar.setVisibility(View.GONE);
                } else if (resultDTOResource instanceof Resource.Loading) {
                    binding.recyclerView.setVisibility(View.GONE);
                    binding.progressbar.setVisibility(View.VISIBLE);
                } else if (resultDTOResource instanceof Resource.Error) {
                    searchResultAdapter.submitList(Collections.emptyList());
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.progressbar.setVisibility(View.GONE);
                    Toast.makeText(requireActivity(), resultDTOResource.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}