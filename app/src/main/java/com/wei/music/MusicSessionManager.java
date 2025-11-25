package com.wei.music;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.wei.music.bean.PlaylistDTO;
import com.wei.music.repository.MusicListRepository;
import com.wei.music.service.MusicService;
import com.wei.music.service.musicaction.MusicActionContract;
import com.wei.music.service.musicaction.MusicIntentContract;
import com.wei.music.utils.AudioFileFetcher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.functions.Function;

@Singleton
public class MusicSessionManager {

    //用户的歌单
    private final MutableLiveData<List<PlaylistDTO>> _allPlaylistData = new MutableLiveData<>();
    public final LiveData<List<PlaylistDTO>> allPlaylistData = _allPlaylistData;

    public final MutableLiveData<MusicActionContract> action = new MutableLiveData<>();
    private final MutableLiveData<MusicIntentContract> intent = new MutableLiveData<>();
    private final MusicListRepository musicListRepository;

    private List<MediaSessionCompat.QueueItem> currentList = Collections.emptyList();

    @Inject
    public MusicSessionManager(MusicListRepository musicListRepository) {
        this.musicListRepository = musicListRepository;
    }

    public void onMusicIntent(MusicIntentContract intent) {
        if (intent instanceof MusicIntentContract.ChangePlayListOrSkipToPosition) {
            MusicIntentContract.ChangePlayListOrSkipToPosition changePlayListOrSkipToPosition =
                    (MusicIntentContract.ChangePlayListOrSkipToPosition) intent;
            List<MediaSessionCompat.QueueItem> replace = changePlayListOrSkipToPosition.getReplace();
            PlaybackStateCompat playbackState = changePlayListOrSkipToPosition.getPlaybackState();
            if (replace == currentList) {
                int startIndex = changePlayListOrSkipToPosition.getStartIndex();
                Optional<Integer> actPlayIndex = Optional.ofNullable(playbackState.getExtras())
                        .map(bundle -> bundle.getInt(MusicService.MUSIC_STATE_POSITION, -1))
                        .filter(integer -> integer >= 0);
                if (actPlayIndex.isPresent() && actPlayIndex.get() != startIndex) {
                    action.postValue(new MusicActionContract.OnSkipToPosition(startIndex));
                }
            } else {
                currentList = changePlayListOrSkipToPosition.getReplace();
                action.postValue(new MusicActionContract.ChangePlayQueue(
                        changePlayListOrSkipToPosition.getReplace(),
                        changePlayListOrSkipToPosition.getStartIndex()
                ));
            }
        } else if (intent instanceof MusicIntentContract.InsertMusicAndPlay) {
            action.postValue(new MusicActionContract.Insert(((MusicIntentContract.InsertMusicAndPlay) intent).getInsert()));
        }
    }

    public Observable<List<PlaylistDTO>> loadDatabaseSongList() {
        return musicListRepository.loadDatabaseSongList()
                .map(new Function<List<PlaylistDTO>, List<PlaylistDTO>>() {
                    @Override
                    public List<PlaylistDTO> apply(List<PlaylistDTO> playlistDTOS) throws Throwable {
                        Optional<PlaylistDTO> localSongListOpt = playlistDTOS.stream().filter(new Predicate<PlaylistDTO>() {
                            @Override
                            public boolean test(PlaylistDTO playlistDTO) {
                                return playlistDTO.getId() == AudioFileFetcher.LOCAL_SONG_LIST_ID;
                            }
                        }).findFirst();
                        localSongListOpt.ifPresent(new Consumer<PlaylistDTO>() {
                            @Override
                            public void accept(PlaylistDTO playlistDTO) {
                                playlistDTOS.remove(playlistDTO);
                                playlistDTOS.add(0, playlistDTO);
                            }
                        });
                        _allPlaylistData.postValue(playlistDTOS);
                        return playlistDTOS;
                    }
                })
                .onErrorReturn(new Function<Throwable, List<PlaylistDTO>>() {
                    @Override
                    public List<PlaylistDTO> apply(Throwable throwable) throws Throwable {
                        _allPlaylistData.postValue(Collections.emptyList());
                        return Collections.emptyList();
                    }
                });
    }

    public Single<List<PlaylistDTO>> loadLocalSongList() {
        return musicListRepository.fetchLocalSongList();
    }

    public void refreshSongListWithUser(int userId) {
        musicListRepository.fetchAllSongList(userId)
                .subscribe();
    }
}
