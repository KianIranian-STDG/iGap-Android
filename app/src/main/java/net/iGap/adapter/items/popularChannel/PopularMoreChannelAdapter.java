package net.iGap.adapter.items.popularChannel;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.popularChannel.Channel;

import java.util.ArrayList;
import java.util.List;

public class PopularMoreChannelAdapter extends RecyclerView.Adapter<PopularMoreChannelAdapter.MoreChannelViewHolder> {
    private List<Channel> channels = new ArrayList<>();
    private OnMoreChannelCallBack callBack;

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
        notifyDataSetChanged();
    }

    public void addChannel(List<Channel> channels) {
        this.channels.addAll(channels);
        notifyDataSetChanged();
    }

    public void setCallBack(OnMoreChannelCallBack callBack) {
        this.callBack = callBack;
    }

    @NonNull
    @Override
    public MoreChannelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View channelViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popular_channel_normal_item, viewGroup, false);
        return new MoreChannelViewHolder(channelViewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull MoreChannelViewHolder categoryViewHolder, int i) {
        categoryViewHolder.bindChannel(channels.get(i));
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    @FunctionalInterface
    public interface OnMoreChannelCallBack {
        void onChannelClick(Channel channel);
    }

    public class MoreChannelViewHolder extends RecyclerView.ViewHolder {
        private ImageView channelAvatarIv;
        private TextView channelNameTv;

        public MoreChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            channelAvatarIv = itemView.findViewById(R.id.circle_item_popular_rv_grid_linear);
            channelNameTv = itemView.findViewById(R.id.tv_item_popular_rv_grid_linear);
        }

        public void bindChannel(Channel channel) {
            ImageLoadingService.load(channel.getIcon(), channelAvatarIv);
            if (G.isAppRtl) {
                channelNameTv.setText(channel.getTitle());
            } else {
                channelNameTv.setText(channel.getTitleEn());
            }

            itemView.setOnClickListener(v -> {
                if (callBack != null)
                    callBack.onChannelClick(channel);
            });
        }

    }

}
