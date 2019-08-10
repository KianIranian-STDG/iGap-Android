package net.iGap.adapter.items.favoritechannel;


import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.FavoriteChannel.Channel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelViewHolder> {
    private List<Channel> channelList;
    private OnClickedChannelEventCallBack onClickedChannelEventCallBack;

    public ChannelAdapter(List<Channel> channelList) {
        this.channelList = channelList;
    }


    @NonNull
    @Override
    public ChannelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(G.fragmentActivity).inflate(R.layout.item_favorite_channel_rv_row,viewGroup,false);
        return new ChannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChannelViewHolder channelViewHolder, int i) {
        channelViewHolder.bindChannel(channelList.get(i));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public class ChannelViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView channelImage;
        private TextView channelTitle;
        private CardView root;

        public ChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            channelImage = itemView.findViewById(R.id.circle_item_popular_rv_linear);
            channelTitle = itemView.findViewById(R.id.tv_item_popular_rv_linear);
            root = itemView.findViewById(R.id.card_item_pop_row);
            Utils.setCardsBackground(root, R.color.white, R.color.navigation_dark_mode_bg);

        }


        public void bindChannel(Channel channel) {
            ImageLoadingService.load(channel.getIcon(), channelImage);
            if (G.selectedLanguage.equals("fa"))
                channelTitle.setText(channel.getTitle());
            if (G.selectedLanguage.equals("en"))
                channelTitle.setText(channel.getmTitleEn());

            itemView.setOnClickListener(v -> onClickedChannelEventCallBack.onClickedChannel(channel));

        }
    }

    public void setOnClickedChannelEventCallBack(OnClickedChannelEventCallBack onClickedChannelEventCallBack) {
        this.onClickedChannelEventCallBack = onClickedChannelEventCallBack;
    }

    public interface OnClickedChannelEventCallBack {
        void onClickedChannel(Channel channel);
    }
}
