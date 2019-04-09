package net.iGap.adapter.items.discovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.adapter.items.discovery.holder.BaseViewHolder;
import net.iGap.adapter.items.discovery.holder.Type1ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type2ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type3ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type4ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type5ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type6ViewHolder;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class DiscoveryAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private ArrayList<ProtoGlobal.Discovery> discoveryList;

    public DiscoveryAdapter(Context context, ArrayList<ProtoGlobal.Discovery> discoveryList) {
        this.context = context;
        this.discoveryList = discoveryList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context.getApplicationContext());
        switch (i) {
            case 1:
                return new Type1ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_1, viewGroup, false));
            case 2:
                return new Type2ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_2, viewGroup, false));
            case 3:
                return new Type3ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_3, viewGroup, false));
            case 4:
                return new Type4ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_4, viewGroup, false));
            case 5:
                return new Type5ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_5, viewGroup, false));
            case 6:
                return new Type6ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_6, viewGroup, false));
        }
        return new Type1ViewHolder(layoutInflater.inflate(R.layout.item_dashboard_1, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int i) {
        viewHolder.itemView.getLayoutParams().height = Math.round(AndroidUtils.dpToPx(context, discoveryList.get(i).getHeight()));
        viewHolder.bindView(discoveryList.get(i));
    }

    @Override
    public int getItemCount() {
        return discoveryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return discoveryList.get(position).getModelValue() + 1;
    }

}