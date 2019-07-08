package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.items.popular.model.Channel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterRowInfoItem extends RecyclerView.Adapter<AdapterRowInfoItem.ChildGridItemViewHolder> {
    private List<Channel> channelList = new ArrayList<>();
    private Context context;

    public AdapterRowInfoItem(Context context) {
        this.context = context;
        Channel channel = new Channel();
        channel.setChannelImage(ResourcesCompat.getDrawable(context.getResources(), R.drawable.image_sample, null));
        channel.setChannelTitle("باشگاه خبرنگاران جوان");
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
        channelList.add(channel);
    }

    @NonNull
    @Override
    public ChildGridItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_rv_row_info, viewGroup, false);
        return new ChildGridItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildGridItemViewHolder gridItemPrentViewHolder, int i) {
        gridItemPrentViewHolder.bindChannel(channelList.get(i));

    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class ChildGridItemViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView channelImageGridChild;
        private TextView channelTitleGridChild;

        public ChildGridItemViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImageGridChild = itemView.findViewById(R.id.circle_item_popular_child_rv_grid);
            channelTitleGridChild = itemView.findViewById(R.id.tv_item_popular_child_rv_grid);
        }

        public void bindChannel(Channel channel) {
            channelImageGridChild.setImageDrawable(channel.getChannelImage());
            channelTitleGridChild.setText(channel.getChannelTitle());
        }
    }
}
