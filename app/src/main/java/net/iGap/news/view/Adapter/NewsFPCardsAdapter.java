package net.iGap.news.view.Adapter;

import android.graphics.Color;
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
import net.iGap.news.repository.model.NewsFPList;
import net.iGap.news.repository.model.NewsFirstPage;

public class NewsFPCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int newsSingle = 3;
    private static final int newsDouble = 4;
    private static final int newsTriple = 5;
    private NewsFirstPage mData;
    private onClickListener callBack;

    NewsFPCardsAdapter(NewsFirstPage mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        switch (viewType) {
            case newsSingle:
                View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_single_news_item, parent, false);
                viewHolder = new SingleViewHolder(singleVH);
                break;
            case newsDouble:
                View doubleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_double_news_item, parent, false);
                viewHolder = new DoubleViewHolder(doubleVH);

                display.getSize(size);
                viewHolder.itemView.getLayoutParams().width = (int) (size.x * 0.48);
                viewHolder.itemView.getLayoutParams().height = (int) ((size.x * 0.48) *1);
                break;
            case newsTriple:
                View tripleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_triple_news_item, parent, false);
                viewHolder = new TripleViewHolder(tripleVH);

                display.getSize(size);
                viewHolder.itemView.getLayoutParams().height = (int) (size.x *0.32);
                viewHolder.itemView.getLayoutParams().width = (int) (size.x *0.32);

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
            case newsSingle:
                ((SingleViewHolder)holder).initSingleVH(position);
                break;
            case newsDouble:
                ((DoubleViewHolder)holder).initDoubleVH(position);
                break;
            case newsTriple:
                ((TripleViewHolder)holder).initTripleVH(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.getmNews().size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.getmType();
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        private TextView category, source, rootTitle, title;
        private ImageView image;
        private CardView container;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            source = itemView.findViewById(R.id.source);
            rootTitle = itemView.findViewById(R.id.rootTitle);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initSingleVH(int position) {
            category.setText("" + mData.getmNews().get(position).getCategory());
            source.setText("" + mData.getmNews().get(position).getNews().get(0).getSource());
            rootTitle.setText("" + mData.getmNews().get(position).getNews().get(0).getContents().get(0).getRootTitle());
            if (rootTitle.getText().equals(""))
                rootTitle.setVisibility(View.GONE);
            title.setText("" + mData.getmNews().get(position).getNews().get(0).getContents().get(0).getTitle());
            Picasso.get()
                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
//                    .load(mData.getmNews().get(position).getNews().get(0).getContents().get(0).getImage().get(0).getOriginal())
                    .placeholder(R.mipmap.logo)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position)));

            if (mData.getmNews().get(position).getColor() == 0) {
                // Normal
                changeToNormal();
            }
            else {
                // Red
                changeToRed();
            }
        }

        private void changeToRed() {
            category.setTextColor(Color.WHITE);
            source.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.news_red));
        }

        private void changeToNormal() {
            if (G.isDarkTheme) {
                changeToNormalDark();
                return;
            }
            category.setTextColor(G.context.getResources().getColor(R.color.news_red));
            source.setTextColor(G.context.getResources().getColor(R.color.black_register));
            title.setTextColor(Color.BLACK);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
        }

        private void changeToNormalDark() {
            category.setTextColor(Color.WHITE);
            source.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
        }
    }

    public class DoubleViewHolder extends RecyclerView.ViewHolder {

        private TextView category, source, rootTitle, title;
        private ImageView image;
        private CardView container;

        public DoubleViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            source = itemView.findViewById(R.id.source);
            rootTitle = itemView.findViewById(R.id.rootTitle);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initDoubleVH(int position) {
            category.setText("" + mData.getmNews().get(position).getCategory());
            source.setText("" + mData.getmNews().get(position).getNews().get(0).getSource());
            rootTitle.setText("" + mData.getmNews().get(position).getNews().get(0).getContents().get(0).getRootTitle());
            title.setText("" + mData.getmNews().get(position).getNews().get(0).getContents().get(0).getTitle());
            Picasso.get()
                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
//                    .load(mData.getmNews().get(position).getNews().get(0).getContents().get(0).getImage().get(0).getOriginal())
                    .placeholder(R.mipmap.logo)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position)));

            if (mData.getmNews().get(position).getColor() == 0) {
                // Normal
                changeToNormal();
            }
            else {
                // Red
                changeToRed();
            }
        }

        private void changeToRed() {
            category.setTextColor(Color.WHITE);
            source.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.news_red));
        }

        private void changeToNormal() {
            if (G.isDarkTheme) {
                changeToNormalDark();
                return;
            }
            category.setTextColor(G.context.getResources().getColor(R.color.news_red));
            source.setTextColor(G.context.getResources().getColor(R.color.black_register));
            title.setTextColor(Color.BLACK);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
        }

        private void changeToNormalDark() {
            category.setTextColor(Color.WHITE);
            source.setTextColor(Color.WHITE);
            title.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
        }
    }

    public class TripleViewHolder extends RecyclerView.ViewHolder {

        private TextView category;
        private ImageView image;
        private CardView container;

        public TripleViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initTripleVH(int position) {
            category.setText("" + mData.getmNews().get(position).getCategory());
            Picasso.get()
                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    //.load(mData.getmNews().get(position).getNews().get(0).getContents().get(0).getImage().get(0).getOriginal())
                    .placeholder(R.mipmap.logo)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position)));

            if (mData.getmNews().get(position).getColor() == 0) {
                // Normal
                changeToNormal();
            }
            else {
                // Red
                changeToRed();
            }
        }
        private void changeToRed() {
            category.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.news_red));
        }

        private void changeToNormal() {
            if (G.isDarkTheme) {
                changeToNormalDark();
                return;
            }
            category.setTextColor(G.context.getResources().getColor(R.color.news_red));
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
        }

        private void changeToNormalDark() {
            category.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
        }
    }

    public interface onClickListener {
        void onNewsGroupClick(NewsFPList slide);
    }

    public NewsFirstPage getmData() {
        return mData;
    }

    public void setmData(NewsFirstPage mData) {
        this.mData = mData;
    }

    public onClickListener getCallback() {
        return callBack;
    }

    public void setCallback(onClickListener callback) {
        this.callBack = callback;
    }
}
