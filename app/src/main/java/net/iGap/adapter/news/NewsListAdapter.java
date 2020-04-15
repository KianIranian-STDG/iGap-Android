package net.iGap.adapter.news;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.module.Theme;
import net.iGap.helper.HelperCalander;
import net.iGap.model.news.NewsList;

import java.util.ArrayList;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int newsType = 0;
    private static final int advType = 1;
    private static final int loadType = 2;
    private NewsList mData;
    private onClickListener callBack;
    private boolean isLoaderVisible = false;

    public NewsListAdapter(NewsList mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType) {
            case newsType:
                View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
                viewHolder = new NewsViewHolder(singleVH);
                break;
            case advType:
                View doubleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_adv_item, parent, false);
                viewHolder = new AdvViewHolder(doubleVH);
                break;
            case loadType:
                View loadVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_load_item, parent, false);
                viewHolder = new LoadViewHolder(loadVH);
                break;
            default:
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case newsType:
                ((NewsViewHolder) holder).initVH(position);
                break;
            case advType:
                ((AdvViewHolder) holder).initVH(position);
                break;
            case loadType:
                ((LoadViewHolder) holder).initVH();
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mData.getNews() == null)
            return 0;
        return mData.getNews().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.getNews().get(position) == null)
            return loadType;
        return mData.getNews().get(position).getType();
    }

    public void addItems(NewsList postItems) {
        this.mData.getNews().addAll(postItems.getNews());
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        mData.getNews().add(null);
        notifyItemInserted(mData.getNews().size() - 1);
    }

    public void removeLoading() {
        // TODO: 10/28/2019 line 104 & 105 must be removed
        if (mData.getNews().size() == 0)
            return;

        isLoaderVisible = false;
        int position = mData.getNews().size() - 1;
        NewsList.News item = mData.getNews().get(position);
        if (item == null) {
            mData.getNews().remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        mData.setNews(null);
        mData.setNews(new ArrayList<>());
        notifyDataSetChanged();
    }

    public onClickListener getCallback() {
        return callBack;
    }

    public void setCallback(onClickListener callback) {
        this.callBack = callback;
    }

    public interface onClickListener {
        void onNewsGroupClick(NewsList.News slide);
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        private TextView time, source, rootTitle, title, view, view_icon;
        private ImageView image;
        private CardView container;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.date);
            source = itemView.findViewById(R.id.source);
            rootTitle = itemView.findViewById(R.id.rootTitle);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            view = itemView.findViewById(R.id.view);
            view_icon = itemView.findViewById(R.id.view_icon);
            container = itemView.findViewById(R.id.container);
        }

        void initVH(int position) {
            time.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(mData.getNews().get(position).getDate()) : mData.getNews().get(position).getDate());
            source.setText(mData.getNews().get(position).getSource());
            title.setText(mData.getNews().get(position).getTitle());

            int viewTemp = Integer.valueOf(mData.getNews().get(position).getViewNum()) + Integer.valueOf(mData.getNews().get(position).getId());
            if (viewTemp == 0) {
                view.setVisibility(View.GONE);
                view_icon.setVisibility(View.GONE);
            } else {
                if (viewTemp > 1000000) {
                    viewTemp /= 1000000;
                    view.setText((HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(viewTemp)) : String.valueOf(viewTemp)) + "M");
                } else if (viewTemp > 1000) {
                    viewTemp /= 1000;
                    view.setText((HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(viewTemp)) : String.valueOf(viewTemp)) + "K");
                } else {
                    view.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(viewTemp)) : String.valueOf(viewTemp));
                }
            }

            String tempRoot = mData.getNews().get(position).getRootTitle();
            if (tempRoot == null || tempRoot.equals(""))
                rootTitle.setVisibility(View.GONE);
            else
                rootTitle.setText(tempRoot);
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getNews().get(position).getImage())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getNews().get(position)));
            setColor();
        }

        private void setColor() {
            if (G.themeColor == Theme.DARK) {
                changeToDark();
                return;
            }
            source.setTextColor(G.context.getResources().getColor(R.color.news_red));
            title.setTextColor(Color.BLACK);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
        }

        private void changeToDark() {
            source.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
        }

    }

    public class AdvViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private CardView container;

        AdvViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initVH(int position) {
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getNews().get(position).getImage())
                    .placeholder(R.mipmap.news_temp_banner)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getNews().get(position)));
        }

    }

    public class LoadViewHolder extends RecyclerView.ViewHolder {

        LoadViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void initVH() {

        }
    }
}
