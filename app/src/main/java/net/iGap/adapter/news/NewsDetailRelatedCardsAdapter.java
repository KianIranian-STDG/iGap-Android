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
import net.iGap.model.news.NewsList;

public class NewsDetailRelatedCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private NewsList mData;
    private onClickListener callBack;

    public NewsDetailRelatedCardsAdapter(NewsList mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View doubleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_related_item, parent, false);
        return new DoubleViewHolder(doubleVH);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((DoubleViewHolder) holder).initDoubleVH(position);
    }

    @Override
    public int getItemCount() {
        if (mData.getNews().size() == 1)
            return 1;
        return mData.getNews().size() / 2;
    }

    public NewsList getmData() {
        return mData;
    }

    public void setmData(NewsList mData) {
        this.mData = mData;
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

    public class DoubleViewHolder extends RecyclerView.ViewHolder {

        private TextView source, rootTitle, title;
        private ImageView image;
        private CardView container;

        private TextView source1, rootTitle1, title1;
        private ImageView image1;
        private CardView container1;

        DoubleViewHolder(@NonNull View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.source);
            rootTitle = itemView.findViewById(R.id.rootTitle);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);

            source1 = itemView.findViewById(R.id.source1);
            rootTitle1 = itemView.findViewById(R.id.rootTitle1);
            title1 = itemView.findViewById(R.id.title1);
            image1 = itemView.findViewById(R.id.image1);
            container1 = itemView.findViewById(R.id.container1);
        }

        void initDoubleVH(int position) {

            if (G.themeColor == Theme.DARK)
                changeToNormalDark();

            if (position * 2 >= mData.getNews().size())
                return;
            source.setText(mData.getNews().get(position * 2).getSource());
            rootTitle.setText(mData.getNews().get(position * 2).getRootTitle());
            title.setText(mData.getNews().get(position * 2).getTitle());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getNews().get(position * 2).getImage())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getNews().get(position * 2)));

            if ((position * 2 + 1) >= mData.getNews().size()) {
                container1.setVisibility(View.INVISIBLE);
                return;
            }
            source1.setText(mData.getNews().get(position * 2 + 1).getSource());
            rootTitle1.setText(mData.getNews().get(position * 2 + 1).getRootTitle());
            title1.setText(mData.getNews().get(position * 2 + 1).getTitle());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getNews().get(position * 2 + 1).getImage())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image1);
            container1.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getNews().get(position * 2 + 1)));

        }

        private void changeToNormalDark() {
            source.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_send_dark));

            source1.setTextColor(Color.WHITE);
            title1.setTextColor(Color.WHITE);
            container1.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_send_dark));
        }
    }
}
