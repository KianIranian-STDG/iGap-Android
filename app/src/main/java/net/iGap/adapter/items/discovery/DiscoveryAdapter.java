package net.iGap.adapter.items.discovery;

import android.content.Context;
import android.content.res.Resources;
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
import net.iGap.adapter.items.discovery.holder.Type7ViewHolder;
import net.iGap.adapter.items.discovery.holder.TypeUnknownViewHolder;

import java.util.ArrayList;

public class DiscoveryAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private Context context;
    private ArrayList<DiscoveryItem> discoveryList;

    public DiscoveryAdapter(Context context, ArrayList<DiscoveryItem> discoveryList) {
        this.context = context;
        this.discoveryList = discoveryList;
    }

    public void setDiscoveryList(ArrayList<DiscoveryItem> discoveryList) {
        this.discoveryList = discoveryList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(context.getApplicationContext());
        switch (i) {
            case 1:
                return new Type1ViewHolder(layoutInflater.inflate(R.layout.item_discovery_1, viewGroup, false));
            case 2:
                return new Type2ViewHolder(layoutInflater.inflate(R.layout.item_discovery_2, viewGroup, false));
            case 3:
                return new Type3ViewHolder(layoutInflater.inflate(R.layout.item_discovery_3, viewGroup, false));
            case 4:
                return new Type4ViewHolder(layoutInflater.inflate(R.layout.item_discovery_4, viewGroup, false));
            case 5:
                return new Type5ViewHolder(layoutInflater.inflate(R.layout.item_discovery_5, viewGroup, false));
            case 6:
                return new Type6ViewHolder(layoutInflater.inflate(R.layout.item_discovery_6, viewGroup, false));
            case 7:
                return new Type7ViewHolder(layoutInflater.inflate(R.layout.item_discovery_7, viewGroup, false));
        }
        return new TypeUnknownViewHolder(layoutInflater.inflate(R.layout.item_discovery_unknown, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int i) {
        String[] scales = discoveryList.get(i).scale.split(":");
        float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
        viewHolder.itemView.getLayoutParams().height = Math.round(height);
        viewHolder.bindView(discoveryList.get(i));
    }

    @Override
    public int getItemCount() {
        return discoveryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return discoveryList.get(position).model.getNumber() + 1;
    }

}