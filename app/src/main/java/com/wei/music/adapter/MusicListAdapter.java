package com.wei.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import android.support.v4.media.session.MediaSessionCompat;

import com.wei.music.mapper.MediaDescriptionCompatMapper;
import com.wei.music.mapper.MediaMetadataInfo;
import com.wei.music.utils.GlideLoadUtils;
import com.wei.music.R;


public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.MusicViewHolder> {

    private Context mContext;
    private List<MediaSessionCompat.QueueItem> songQueueItems = Collections.emptyList();

    private OnItemClick mListener;

    public MusicListAdapter(Context cont) {
        this.mContext = cont;
    }

    public interface OnItemClick {
        void OnClick(MediaSessionCompat.QueueItem data, int postion);
    }

    public void OnClickListener(OnItemClick listener) {
        this.mListener = listener;
    }

    @Override
    public void onBindViewHolder(final MusicViewHolder holder, final int position) {
        MediaMetadataInfo info = MediaDescriptionCompatMapper.mapper(
                songQueueItems.get(holder.getAdapterPosition()).getDescription()
        );
        holder.mName.setText(info.getTitle());
        holder.mSinger.setText(info.getArtist());
        GlideLoadUtils.setRound(info.getAlbum(), 8, holder.mImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnClick(songQueueItems.get(holder.getAdapterPosition()), holder.getAdapterPosition());
            }
        });
    }


    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup viewgroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_music_list, viewgroup, false);
        return new MusicViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return songQueueItems.size();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mSinger;
        private final ImageView mImage;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.music_title);
            mSinger = itemView.findViewById(R.id.music_msg);
            mImage = itemView.findViewById(R.id.music_imag);
        }

    }

    public void setQueueItems(List<MediaSessionCompat.QueueItem> queueItems) {
        this.songQueueItems = queueItems;
        notifyDataSetChanged();
    }
}

