package net.iGap.adapter.items.popularChannel;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.popularChannel.Channel;

import java.util.List;

public class PopularMoreChannelAdapter extends RecyclerView.Adapter<PopularMoreChannelAdapter.MoreChannelViewHolder> {
    private List<Channel> channels;
    private OnMoreChannelCallBack callBack;

    public PopularMoreChannelAdapter(OnMoreChannelCallBack onMoreChannelCallBack) {
        this.callBack = onMoreChannelCallBack;
    }

    public void setChannels(List<Channel> channels) {
        Log.wtf(this.getClass().getName(), "setChannels");
        this.channels = channels;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MoreChannelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MoreChannelViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_popular_channel_normal_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MoreChannelViewHolder categoryViewHolder, int i) {
        if (i + 4 > getItemCount() - 1) {
            callBack.onLoadMore();
        }
        categoryViewHolder.bindChannel(channels.get(i));
    }

    @Override
    public int getItemCount() {
        return channels != null ? channels.size() : 0;
    }

    public interface OnMoreChannelCallBack {
        void onChannelClick(Channel channel);

        void onLoadMore();
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
            Glide.with(G.context)
                    .load(channel.getIcon())
                    .fitCenter()
                    .centerInside()
                    .error(R.drawable.ic_error)
                    .into(channelAvatarIv);
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
