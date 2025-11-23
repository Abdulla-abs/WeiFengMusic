package com.wei.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.wei.music.R;
import com.wei.music.bean.SearchHistoryVO;

public class HistorySearchAdapter extends ListAdapter<SearchHistoryVO, HistorySearchAdapter.ViewHolder> {

    private OnItemClickListener itemClickListener;

    public HistorySearchAdapter() {
        super(new DiffUtil.ItemCallback<SearchHistoryVO>() {
            @Override
            public boolean areItemsTheSame(@NonNull SearchHistoryVO oldItem, @NonNull SearchHistoryVO newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull SearchHistoryVO oldItem, @NonNull SearchHistoryVO newItem) {
                return true;
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchHistoryVO searchHistory = getCurrentList().get(position);
        holder.tv.setText(searchHistory.getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onClickKeywords(searchHistory.getContent());
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (itemClickListener != null) {
                    itemClickListener.onLongClickKeyWords(searchHistory.getContent());
                }
                return false;
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = (TextView) itemView;
        }
    }

    public interface OnItemClickListener {
        void onClickKeywords(String keyword);

        void onLongClickKeyWords(String keyword);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
