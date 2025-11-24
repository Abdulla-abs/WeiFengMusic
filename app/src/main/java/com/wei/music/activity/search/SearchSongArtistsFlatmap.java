package com.wei.music.activity.search;

import com.wei.music.bean.SearchResultDTO;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;

public class SearchSongArtistsFlatmap {

    public static Optional<String> flatmap(List<SearchResultDTO.ResultDTO.SongsDTO.ArtistsDTO> artists) {
        return artists.stream()
                .map(new Function<SearchResultDTO.ResultDTO.SongsDTO.ArtistsDTO, String>() {
                    @Override
                    public String apply(SearchResultDTO.ResultDTO.SongsDTO.ArtistsDTO artistsDTO) {
                        return artistsDTO.getName();
                    }
                })
                .reduce(new BinaryOperator<String>() {
                    @Override
                    public String apply(String s, String s2) {
                        return s + s2;
                    }
                });
    }
}
