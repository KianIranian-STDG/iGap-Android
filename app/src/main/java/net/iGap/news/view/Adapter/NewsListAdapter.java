package net.iGap.news.view.Adapter;

import android.graphics.Point;
import android.view.Display;
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
import net.iGap.news.repository.model.NewsList;

public class NewsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int newsType = 0;
    private static final int advType = 1;
    private NewsList mData;
    private onClickListener callBack;

    public NewsListAdapter(NewsList mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        switch (viewType) {
            case newsType:
                View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
                viewHolder = new NewsViewHolder(singleVH);
                break;
            case advType:
                View doubleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_adv_item, parent, false);
                viewHolder = new AdvViewHolder(doubleVH);

                display.getSize(size);
                viewHolder.itemView.getLayoutParams().width = (int) (size.x * 0.48);
                viewHolder.itemView.getLayoutParams().height = (int) (size.y *0.35);
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
                ((NewsViewHolder)holder).initVH(position);
                break;
            case advType:
                ((AdvViewHolder)holder).initVH(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.getNews().size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.getNews().get(position).getType();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {

        private TextView time, source, rootTitle, title;
        private ImageView image;
        private CardView container;

        NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.date);
            source = itemView.findViewById(R.id.source);
            rootTitle = itemView.findViewById(R.id.rootTitle);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initVH(int position) {
            time.setText("27 اردیبهشت 1397");
            source.setText("همشهری");
            rootTitle.setText("اگه این روتیتر نبود چی میشد؟");
            if (rootTitle.getText().equals(""))
                rootTitle.setVisibility(View.GONE);
            title.setText("" + mData.getNews().get(position).getTitle());
            Picasso.get()
                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
//                    .load(mData.getNews().get(position).getImage())
                    .placeholder(R.mipmap.logo)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getNews().get(position)));
        }

    }

    public class AdvViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private CardView container;

        public AdvViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initVH(int position) {
            Picasso.get()
                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
//                    .load(mData.getNews().get(position).getImage())
                    .placeholder(R.mipmap.logo)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getNews().get(position)));
        }

    }

    public interface onClickListener {
        void onNewsGroupClick(NewsList.News slide);
    }

    public onClickListener getCallback() {
        return callBack;
    }

    public void setCallback(onClickListener callback) {
        this.callBack = callback;
    }
}
