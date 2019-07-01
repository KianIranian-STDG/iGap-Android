package net.iGap.popular.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.popular.model.Channel;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.GridViewHolder> {
    private List<Channel> channelList = new ArrayList<>();
    private Context context;

    public GridAdapter(Context context) {
        this.context = context;
        Channel channel = new Channel();
        channel.setChannelImage(ResourcesCompat.getDrawable(context.getResources(), R.drawable.image_sample, null));
        channel.setChannelTitle("کانال اخرین خبر");
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
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_grid_bottom, viewGroup, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder gridViewHolder, int i) {
        gridViewHolder.bindChannel(channelList.get(i));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }


    public class GridViewHolder extends RecyclerView.ViewHolder {
        private ImageView channelImage;
        private TextView channelTitle;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);

            channelImage = itemView.findViewById(R.id.iv_item_popular_grid);
            channelTitle = itemView.findViewById(R.id.tv_item_popular_grid);
        }


        public void bindChannel(Channel channel) {
            channelImage.setImageDrawable(channel.getChannelImage());
            channelTitle.setText(channel.getChannelTitle());
        }
    }
}
