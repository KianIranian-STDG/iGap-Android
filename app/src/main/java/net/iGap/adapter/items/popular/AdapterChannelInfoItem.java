package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.model.PopularChannel.Channel;
import net.iGap.module.CircleImageView;

import java.util.List;

public class AdapterChannelInfoItem extends RecyclerView.Adapter<AdapterChannelInfoItem.ChannelInfoViewHolder> {

    private List<Channel> channelList;
    private Context context;

    public AdapterChannelInfoItem(Context context, List<Channel> channelList) {
        this.channelList = channelList;
        this.context = context;

    }

    @NonNull
    @Override
    public ChannelInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_category, viewGroup, false);
        return new ChannelInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelInfoViewHolder channelInfoViewHolder, int i) {
        channelInfoViewHolder.bindChannel(channelList.get(i));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class ChannelInfoViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView textView;

        public ChannelInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.circle_item_popular_rv_grid);
            textView = itemView.findViewById(R.id.tv_item_popular_rv_grid);

        }

        public void bindChannel(Channel channel) {
            ImageLoadingService.load(channel.getIcon(), imageView);
            textView.setText(channel.getTitle());
        }
    }
}
