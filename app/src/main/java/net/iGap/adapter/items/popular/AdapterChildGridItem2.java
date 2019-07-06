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

public class AdapterChildGridItem2 extends RecyclerView.Adapter<AdapterChildGridItem2.ChildGridItemViewHolder2> {
    private List<Channel> channelList = new ArrayList<>();
    private Context context;

    public AdapterChildGridItem2(Context context) {
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
    public ChildGridItemViewHolder2 onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_rv_grid_child2, viewGroup, false);
        return new ChildGridItemViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildGridItemViewHolder2 childGridItemViewHolder2, int i) {
        childGridItemViewHolder2.bindChannel(channelList.get(i));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }


    public class ChildGridItemViewHolder2 extends RecyclerView.ViewHolder {
        private CircleImageView channelImageGridChild2;
        private TextView channelTitleGridChild2;

        public ChildGridItemViewHolder2(@NonNull View itemView) {
            super(itemView);
            channelImageGridChild2 = itemView.findViewById(R.id.circle_item_popular_rv_grid_child2);
            channelTitleGridChild2 = itemView.findViewById(R.id.tv_item_popular_rv_grid_child2);
        }

        public void bindChannel(Channel channel) {
            channelImageGridChild2.setImageDrawable(channel.getChannelImage());
            channelTitleGridChild2.setText(channel.getChannelTitle());
        }
    }
}
