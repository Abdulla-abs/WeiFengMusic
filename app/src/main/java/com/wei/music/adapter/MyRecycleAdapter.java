package com.wei.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wei.music.R;
import com.wei.music.bean.PlaylistDTO;
import com.wei.music.bean.SongListBean;
import com.wei.music.bean.UserLoginBean;
import com.wei.music.databinding.LayoutHomeMineBinding;
import com.wei.music.utils.GlideLoadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class MyRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private enum ViewType {
        TYPE_USER_CARD(0),
        TYPE_SONG_LIST(1);
        public final int type;

        ViewType(int type) {
            this.type = type;
        }
    }

    private UserLoginBean userLoginBean;
    private List<PlaylistDTO> playlistDTOS = Collections.emptyList();
    private OnItemClickListener mListener;

    private final Context context;


    public MyRecycleAdapter(Context context) {
        this.context = context;
    }

    public void onDataSetChange(List<PlaylistDTO> playlistDTOS) {
        this.playlistDTOS = playlistDTOS;
        notifyItemRangeChanged(0, playlistDTOS.size());
    }

    @Override
    public int getItemCount() {
        return playlistDTOS.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return ViewType.TYPE_USER_CARD.type;
        } else {
            return ViewType.TYPE_SONG_LIST.type;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == ViewType.TYPE_USER_CARD.type) {
            return new UserCardViewHolder(
                    LayoutHomeMineBinding.inflate(
                            LayoutInflater.from(viewGroup.getContext()),
                            viewGroup,
                            false)
            );
        } else {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_song_list, viewGroup, false);
            return new SongListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyRecycleAdapter.UserCardViewHolder) {
            MyRecycleAdapter.UserCardViewHolder userCardViewHolder = (UserCardViewHolder) holder;
            Optional.ofNullable(userLoginBean)
                    .ifPresent(new Consumer<UserLoginBean>() {
                        @Override
                        public void accept(UserLoginBean userLoginBean) {
                            GlideLoadUtils.setCircle(context,
                                    userLoginBean.getProfile().getAvatarUrl(),
                                    userCardViewHolder.binding.userIcon
                            );
                            userCardViewHolder.binding.userName.setText(userLoginBean.getProfile().getNickname());
                        }
                    });
            userCardViewHolder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onUserCardClick(userLoginBean);
                    }
                }
            });

        } else if (holder instanceof MyRecycleAdapter.SongListViewHolder) {
            MyRecycleAdapter.SongListViewHolder songListViewHolder = (SongListViewHolder) holder;
            PlaylistDTO playlistDTO = playlistDTOS.get(holder.getAdapterPosition() - 1);
            songListViewHolder.mTitle.setText(playlistDTO.getName());
            songListViewHolder.mNumber.setText(playlistDTO.getTrackCount() + " 首");
            GlideLoadUtils.setRound(
                    playlistDTO.getCoverImgUrl(),
                    8,
                    songListViewHolder.mImage
            );
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onSongListClick(playlistDTO,
                                songListViewHolder.mImage,
                                songListViewHolder.mTitle,
                                songListViewHolder.mNumber
                        );
                    }
                }
            });
        }
    }

    public static class SongListViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final TextView mNumber;
        private final ImageView mImage;

        public SongListViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.song_title);
            mNumber = itemView.findViewById(R.id.song_msg);
            mImage = itemView.findViewById(R.id.song_imag);
        }

    }

    private static class UserCardViewHolder extends RecyclerView.ViewHolder {
        public final LayoutHomeMineBinding binding;

        public UserCardViewHolder(@NonNull LayoutHomeMineBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public interface OnItemClickListener {
        void onUserCardClick(UserLoginBean user);

        void onSongListClick(final PlaylistDTO data, final View image, final View title, final View msg);
    }

    public void setListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    public void initUserData(UserLoginBean data) {
        this.userLoginBean = data;
        notifyItemChanged(0);
    }

}
