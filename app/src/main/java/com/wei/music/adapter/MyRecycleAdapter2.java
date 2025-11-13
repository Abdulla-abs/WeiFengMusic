package com.wei.music.adapter;

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
import com.wei.music.bean.SongListBean;
import com.wei.music.utils.GlideLoadUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyRecycleAdapter2 extends RecyclerView.Adapter<MyRecycleAdapter2.ViewHolder> {
    private List<SongListBean> songListBeans;
    private OnItemClick mListener;

    public void setSongListBeans(List<SongListBean> songListBeans) {
        this.songListBeans = songListBeans;
    }

    public interface OnItemClick {
        void OnClick(SongListBean data, View image, View title, View msg);
    }

    public void OnClickListener(OnItemClick listener) {
        this.mListener = listener;
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        SongListBean songListBean = songListBeans.get(position);
        holder.mTitle.setText(songListBean.getTitle());
        holder.mNumber.setText(songListBean.getNumber() + " 首");
        GlideLoadUtils.setRound(holder.itemView.getContext(), songListBean.getImage(), 8, holder.mImage);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.OnClick(songListBean, holder.mImage, holder.mTitle, holder.mNumber);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (songListBeans == null) return 0;
        return songListBeans.size();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_song_list, viewGroup, false);
        return new ViewHolder(view);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitle;
        private final TextView mNumber;
        private final ImageView mImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.song_title);
            mNumber = itemView.findViewById(R.id.song_msg);
            mImage = itemView.findViewById(R.id.song_imag);
        }

    }


}
