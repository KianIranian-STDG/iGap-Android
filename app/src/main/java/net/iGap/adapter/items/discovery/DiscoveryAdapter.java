package net.iGap.adapter.items.discovery;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.adapter.items.discovery.holder.BaseViewHolder;
import net.iGap.adapter.items.discovery.holder.Type0ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type1ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type2ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type3ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type4ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type5ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type6ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type7ViewHolder;
import net.iGap.adapter.items.discovery.holder.Type8ViewHolder;
import net.iGap.adapter.items.discovery.holder.TypeUnknownViewHolder;
import net.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

public class DiscoveryAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private FragmentActivity activity;
    private ArrayList<DiscoveryItem> discoveryList;
    private int width;
    private int pos;

    public DiscoveryAdapter(FragmentActivity activity, int width, ArrayList<DiscoveryItem> discoveryList) {
        this.activity = activity;
        this.discoveryList = discoveryList;
        this.width = width;
    }

    public void setDiscoveryList(ArrayList<DiscoveryItem> discoveryList, int width) {
        this.discoveryList = discoveryList;
        this.width = width;
    }

    public void setWidth(int width) {
        this.width = width;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        switch (i) {
            case 0:
                return new Type0ViewHolder(layoutInflater.inflate(R.layout.item_discovery_0, viewGroup, false), activity);
            case 1:
                return new Type1ViewHolder(layoutInflater.inflate(R.layout.item_discovery_1, viewGroup, false), activity);
            case 2:
                return new Type2ViewHolder(layoutInflater.inflate(R.layout.item_discovery_2, viewGroup, false), activity);
            case 3:
                return new Type3ViewHolder(layoutInflater.inflate(R.layout.item_discovery_3, viewGroup, false), activity);
            case 4:
                return new Type4ViewHolder(layoutInflater.inflate(R.layout.item_discovery_4, viewGroup, false), activity);
            case 5:
                return new Type5ViewHolder(layoutInflater.inflate(R.layout.item_discovery_5, viewGroup, false), activity);
            case 6:
                return new Type6ViewHolder(layoutInflater.inflate(R.layout.item_discovery_6, viewGroup, false), activity);
            case 7:
                return new Type7ViewHolder(layoutInflater.inflate(R.layout.item_discovery_7, viewGroup, false), activity);
            case 8:
                return new Type8ViewHolder(layoutInflater.inflate(R.layout.item_discovery_8, viewGroup, false), activity);
            case 9:
            case 10:
                return new Type8ViewHolder(layoutInflater.inflate(R.layout.item_discovery_9, viewGroup, false), activity);


        }
        return new TypeUnknownViewHolder(layoutInflater.inflate(R.layout.item_discovery_unknown, viewGroup, false), activity);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder viewHolder, int i) {

        if (viewHolder.getItemViewType() != 8 && viewHolder.getItemViewType() != 9 && viewHolder.getItemViewType() != 10) {
            String[] scales = discoveryList.get(i).scale.split(":");
            float height = width * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
            viewHolder.itemView.getLayoutParams().height = Math.round(height);
        }
        viewHolder.bindView(discoveryList.get(i));
    }

    @Override
    public int getItemCount() {
        return discoveryList != null ? discoveryList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        try {
            if (discoveryList.get(position).model.getNumber() == 0 && discoveryList.get(position).discoveryFields.get(0).actionType != null && discoveryList.get(position).discoveryFields.get(0).actionType.equals(ProtoGlobal.DiscoveryField.ButtonActionType.IVAND)) {
                return 0;
            }

            if (discoveryList.get(position).model.getNumber() == 8 || discoveryList.get(position).model.getNumber() == 9 || discoveryList.get(position).model.getNumber() == 10) {
                return discoveryList.get(position).model.getNumber();
            }

            pos = position;
            return discoveryList.get(position).model.getNumber() + 1;
        } catch (Exception e) {
            return -2;
        }
    }
}