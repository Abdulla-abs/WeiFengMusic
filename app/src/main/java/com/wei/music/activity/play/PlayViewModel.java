package com.wei.music.activity.play;

import android.support.v4.media.MediaMetadataCompat;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PlayViewModel extends ViewModel {

    @Inject
    public PlayViewModel() {
    }

    public final MutableLiveData<MediaMetadataCompat> mediaMetadataCompatMutableLiveData
            = new MutableLiveData<>();


}
