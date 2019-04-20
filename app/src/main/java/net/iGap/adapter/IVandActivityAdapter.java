package net.iGap.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.IVandActivityViewHolder;
import net.iGap.adapter.items.ProgressBarViewHolder;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class IVandActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ProtoGlobal.IVandActivity> activitiesList;

    private static final int TYPE_ACTIVITY_ITEM = 0;
    private static final int TYPE_LOADING_MORE_ITEM = 1;

    public IVandActivityAdapter(ArrayList<ProtoGlobal.IVandActivity> activitiesList) {
        this.activitiesList = activitiesList;
    }

    public void setData(ArrayList<ProtoGlobal.IVandActivity> activitiesList) {
        this.activitiesList = activitiesList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(G.context);
        if (i == TYPE_LOADING_MORE_ITEM) {
            ProgressBar progressBar = new ProgressBar(viewGroup.getContext());
            progressBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            progressBar.setIndeterminate(true);
            return new ProgressBarViewHolder(progressBar);
        } else {
            return new IVandActivityViewHolder(layoutInflater.inflate(R.layout.item_ivand_activity, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ProgressBarViewHolder) {
            ((ProgressBarViewHolder) viewHolder).bindView();
        } else {
            ((IVandActivityViewHolder) viewHolder).bindView(activitiesList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return activitiesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (activitiesList.get(position) == null) {
            return TYPE_LOADING_MORE_ITEM;
        } else {
            return TYPE_ACTIVITY_ITEM;
        }
    }

    public int getItemCountWithoutLoadingItem() {
        if (activitiesList.get(getItemCount() - 1) == null) {
            return getItemCount() - 1;
        } else {
            return getItemCount();
        }
    }

    public void addLoadingItem() {
        activitiesList.add(null);
    }

    public void removeLoadingItem() {
        activitiesList.remove(activitiesList.size() - 1);
    }

    public void addMoreItemList(ArrayList<ProtoGlobal.IVandActivity> moreItems) {
        activitiesList.addAll(moreItems);
    }
}