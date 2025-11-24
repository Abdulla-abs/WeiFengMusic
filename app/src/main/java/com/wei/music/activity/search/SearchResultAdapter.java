package com.wei.music.activity.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wei.music.R;
import com.wei.music.bean.SearchResultDTO;
import com.wei.music.databinding.ItemMusicListBinding;
import com.wei.music.utils.GlideLoadUtils;

import java.util.function.Consumer;

public class SearchResultAdapter extends ListAdapter<SearchResultDTO.ResultDTO.SongsDTO,
        SearchResultAdapter.ViewHolder> {


    protected SearchResultAdapter(@NonNull DiffUtil.ItemCallback<SearchResultDTO.ResultDTO.SongsDTO> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ItemMusicListBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int adapterPosition = holder.getAdapterPosition();
        SearchResultDTO.ResultDTO.SongsDTO songsDTO = getCurrentList().get(adapterPosition);

        GlideLoadUtils.setRound(R.drawable.ic_music_load, 8, holder.binding.musicImag);
        holder.binding.musicTitle.setText(songsDTO.getName());
        SearchSongArtistsFlatmap.flatmap(songsDTO.getArtists())
                        .ifPresent(new Consumer<String>() {
                            @Override
                            public void accept(String s) {
                                holder.binding.musicMsg.setText(s);
                            }
                        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ItemMusicListBinding binding;

        public ViewHolder(@NonNull ItemMusicListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
