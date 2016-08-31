package com.iGap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iGap.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public class FileUploadTestAdapter extends RecyclerView.Adapter<FileUploadTestAdapter.ViewHolder> {
    private List<String> messages = new ArrayList<>();

    public FileUploadTestAdapter(List<String> messages) {
        this.messages = messages;
    }

    public void insert(String msg) {
        messages.add(msg);
        notifyDataSetChanged();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.test_log_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.message.setText(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView message;

        public ViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.logMessage);
        }
    }
}
