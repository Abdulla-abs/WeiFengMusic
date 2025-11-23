package com.wei.music.activity.about;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SpanUtils;
import com.wei.music.BuildDependencies;
import com.wei.music.databinding.ThankItemBinding;

public class ThankAdapter extends ListAdapter<String, ThankAdapter.ViewHolder> {


    protected ThankAdapter(@NonNull DiffUtil.ItemCallback<String> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                ThankItemBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String str = getCurrentList().get(holder.getAdapterPosition());
        holder.binding.text1.getPaint().setFlags(holder.binding.text1.getPaint().getFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.binding.text1.setText(str);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ThankItemBinding binding;

        public ViewHolder(@NonNull ThankItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ThankItemBinding getBinding() {
            return binding;
        }
    }
}
