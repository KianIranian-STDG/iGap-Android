package net.iGap.popular.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.popular.model.Channel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterGridChannel extends RecyclerView.Adapter<AdapterGridChannel.FragmentGridViewHolder> {
    private List<Channel> channelList = new ArrayList<>();
    private Context context;
    private OnClickedItemEventCallBack onClickedItemEventCallBack;

    public AdapterGridChannel(Context context) {
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
    public FragmentGridViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_rv_grid, viewGroup, false);
        return new FragmentGridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FragmentGridViewHolder holder, int i) {
        holder.bindChannel(channelList.get(i));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class FragmentGridViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView channelImageGrid;
        private TextView channelTitleGrid;

        public FragmentGridViewHolder(@NonNull View itemView) {
            super(itemView);

            channelImageGrid = itemView.findViewById(R.id.circle_item_popular_fragment_grid);
            channelTitleGrid = itemView.findViewById(R.id.tv_item_popular_fragment_grid);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickedItemEventCallBack.onClickedItem();
                }
            });
        }

        public void bindChannel(Channel channel) {
            channelImageGrid.setImageDrawable(channel.getChannelImage());
            channelTitleGrid.setText(channel.getChannelTitle());
        }
    }

    public void setOnClickedItemEventCallBack(OnClickedItemEventCallBack onClickedItemEventCallBack) {
        this.onClickedItemEventCallBack = onClickedItemEventCallBack;
    }

    public interface OnClickedItemEventCallBack{
        void onClickedItem();
    }
}
