package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.model.PopularChannel.Channel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterChannelInfoItem extends RecyclerView.Adapter<AdapterChannelInfoItem.ChannelInfoViewHolder> {

    private List<Channel> channelList;
    private Context context;

    public AdapterChannelInfoItem(Context context) {
        this.context = context;

    }

    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
        notifyDataSetChanged();
    }

    public void addChannelList(List<Channel> channelList) {
        Log.i("nazanin", "addChannelList: ");
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
        private CircleImageView circleImageView;
        private TextView textView;
        private CardView root;
        private LinearLayout linearLayout;

        public ChannelInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.circle_item_popular_rv_grid);
            textView = itemView.findViewById(R.id.tv_item_popular_rv_grid);
            linearLayout = itemView.findViewById(R.id.ll_item_pop_card_category);
            if (G.isDarkTheme) {
                linearLayout.setBackgroundResource(R.drawable.shape_popular_channel_dark_item_them);
            }
            root = itemView.findViewById(R.id.card_item_pop_category);
            Utils.setCardsBackground(root, R.color.white, R.color.gray_6c);

        }

        public void bindChannel(Channel channel) {
            ImageLoadingService.load(channel.getIcon(), circleImageView);
            textView.setText(channel.getTitle());

        }
    }
}
