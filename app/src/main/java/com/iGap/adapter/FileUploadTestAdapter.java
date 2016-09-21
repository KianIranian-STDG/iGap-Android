package com.iGap.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iGap.R;
import com.iGap.activitys.FileUploadTestActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public class FileUploadTestAdapter extends RecyclerView.Adapter<FileUploadTestAdapter.ViewHolder> {
    private List<FileUploadTestActivity.LogStruct> messages = new ArrayList<>();
    private FileUploadTestActivity.LogType logType;

    public void insert(FileUploadTestActivity.LogStruct msg) {
        messages.add(msg);
        notifyDataSetChanged();
    }

    public void clear() {
        messages.clear();
        notifyDataSetChanged();
    }

    public enum AverageType {
        OFU, GNB, R
    }

    public int average(AverageType type) {
        int count = 0;
        int numTotal = 0;

        for (FileUploadTestActivity.LogStruct logStruct : messages) {
            if (type == AverageType.OFU && logStruct.message.toString().contains("ELAPSED IN ON_FILE_UPLOAD")) {
                numTotal += Integer.parseInt(logStruct.message.subSequence("ELAPSED IN ON_FILE_UPLOAD".length() + 2, logStruct.message.length()).toString());
                count++;
            } else if (type == AverageType.GNB && logStruct.message.toString().contains("ELAPSED FOR GETTING N BYTES")) {
                numTotal += Integer.parseInt(logStruct.message.subSequence("ELAPSED FOR GETTING N BYTES".length() + 2, logStruct.message.length()).toString());
                count++;
            } else if (type == AverageType.R && logStruct.message.toString().contains("ELAPSED FOR REQUESTING")) {
                numTotal += Integer.parseInt(logStruct.message.subSequence("ELAPSED FOR REQUESTING".length() + 2, logStruct.message.length()).toString());
                count++;
            }
        }

        return count == 0 ? 0 : numTotal / count;
    }

    public void showLogType(FileUploadTestActivity.LogType logType) {
        this.logType = logType;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.test_log_message, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (logType == FileUploadTestActivity.LogType.ALL || messages.get(position).logType == logType) {
            holder.message.setText(messages.get(position).message);
        } else {
            holder.message.setVisibility(View.GONE);
        }
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