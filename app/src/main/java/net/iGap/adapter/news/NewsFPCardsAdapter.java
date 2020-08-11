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
import net.iGap.model.news.NewsFPList;
import net.iGap.model.news.NewsFirstPage;
import net.iGap.module.Theme;

public class NewsFPCardsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int newsSingle = 0;
    private static final int newsDouble = 1;
    private static final int newsTriple = 2;
    private NewsFirstPage mData;
    private onClickListener callBack;

    NewsFPCardsAdapter(NewsFirstPage mData) {
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
//        Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
//        Point size = new Point();
        switch (viewType) {
            case newsSingle:
                View singleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_single_news_item, parent, false);
                viewHolder = new SingleViewHolder(singleVH);
                break;
            case newsDouble:
                View doubleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_double_news_item, parent, false);
                viewHolder = new DoubleViewHolder(doubleVH);
/*
                display.getSize(size);
                viewHolder.itemView.getLayoutParams().width = (int) (size.x * 0.48);
                viewHolder.itemView.getLayoutParams().height = (int) ((size.x * 0.48) *1);*/
                break;
            case newsTriple:
                View tripleVH = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_triple_news_item, parent, false);
                viewHolder = new TripleViewHolder(tripleVH);
/*
                display.getSize(size);
                viewHolder.itemView.getLayoutParams().height = (int) (size.x *0.32);
                viewHolder.itemView.getLayoutParams().width = (int) (size.x *0.32);*/

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
                ((SingleViewHolder) holder).initSingleVH(position);
                break;
            case newsDouble:
                ((DoubleViewHolder) holder).initDoubleVH(position);
                break;
            case newsTriple:
                ((TripleViewHolder) holder).initTripleVH(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        switch (mData.getmType()) {
            case newsSingle:
                return mData.getmNews().size();
            case newsDouble:
                return mData.getmNews().size() / 2;
            case newsTriple:
                return mData.getmNews().size() / 3;
            default:
                return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.getmType();
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

    public interface onClickListener {
        void onNewsGroupClick(NewsFPList slide);
    }

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        private TextView category, title, lead;
        private ImageView image;
        private CardView container;

        SingleViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.rootTitle);
            lead = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);
        }

        void initSingleVH(int position) {
            NewsFPList temp = mData.getmNews().get(position);
            category.setText(temp.getCategory());
            title.setText(temp.getNews().get(0).getContents().getTitle());
            lead.setText(temp.getNews().get(0).getContents().getLead());
            Picasso.with(G.context)
                    .load(temp.getNews().get(0).getContents().getImage().get(0).getTmb256())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(temp));

            setColor(temp);
        }

        private void setColor(NewsFPList data) {
            if (data.getNews().get(0).getColor().equals("#000000")) {
                // Normal
                changeToNormal();
            } else {
                // color
                changeToServerColor(data.getNews().get(0).getColor(), data.getNews().get(0).getColorRootTitile(), data.getNews().get(0).getColorTitle());
            }
        }

        private void changeToServerColor(String backColor, String rootTitleColor, String titleColor) {
            category.setTextColor(Color.parseColor(titleColor));
            lead.setTextColor(Color.parseColor(rootTitleColor));

            title.setTextColor(Color.parseColor(titleColor));

            container.setCardBackgroundColor(Color.parseColor(backColor));
        }

        private void changeToNormal() {
            if (G.themeColor == Theme.DARK) {
                changeToNormalDark();
                return;
            }
            category.setTextColor(G.context.getResources().getColor(R.color.news_red));
            lead.setTextColor(Color.BLACK);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
        }

        private void changeToNormalDark() {
            category.setTextColor(Color.WHITE);
            lead.setTextColor(Color.WHITE);
            container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
        }
    }

    public class DoubleViewHolder extends RecyclerView.ViewHolder {

        private TextView category, title;
        private ImageView image;
        private CardView container;

        private TextView category1, title1;
        private ImageView image1;
        private CardView container1;

        DoubleViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);

            category1 = itemView.findViewById(R.id.category1);
            title1 = itemView.findViewById(R.id.title1);
            image1 = itemView.findViewById(R.id.image1);
            container1 = itemView.findViewById(R.id.container1);
        }

        void initDoubleVH(int position) {
            if (position * 2 >= mData.getmNews().size())
                return;
            category.setText(mData.getmNews().get(position * 2).getCategory());
            title.setText(mData.getmNews().get(position * 2).getNews().get(0).getContents().getTitle());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getmNews().get(position * 2).getNews().get(0).getContents().getImage().get(0).getTmb256())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position * 2)));
            setColor(0, mData.getmNews().get(position * 2));

            if ((position * 2 + 1) >= mData.getmNews().size())
                return;
            category1.setText(mData.getmNews().get(position * 2 + 1).getCategory());
            title1.setText(mData.getmNews().get(position * 2 + 1).getNews().get(0).getContents().getTitle());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getmNews().get(position * 2 + 1).getNews().get(0).getContents().getImage().get(0).getTmb256())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image1);
            container1.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position * 2 + 1)));
            setColor(1, mData.getmNews().get(position * 2 + 1));
        }

        private void setColor(int cell, NewsFPList data) {
            if (data.getNews().get(0).getColor().equals("#000000")) {
                // Normal
                changeToNormal(cell);
            } else {
                // color
                changeToServerColor(cell, data.getNews().get(0).getColor(), data.getNews().get(0).getColorTitle());
            }
        }

        private void changeToServerColor(int cell, String backColor, String titleColor) {
            if (cell == 0) {
                category.setTextColor(Color.parseColor(titleColor));
                title.setTextColor(Color.parseColor(titleColor));

                container.setCardBackgroundColor(Color.parseColor(backColor));
            } else {
                category1.setTextColor(Color.parseColor(titleColor));
                title1.setTextColor(Color.parseColor(titleColor));

                container1.setCardBackgroundColor(Color.parseColor(backColor));
            }
        }

        private void changeToNormal(int cell) {
            if (G.themeColor == Theme.DARK) {
                changeToNormalDark(cell);
                return;
            }
            if (cell == 0) {
                category.setTextColor(G.context.getResources().getColor(R.color.news_red));
                title.setTextColor(Color.BLACK);
                container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
            } else {
                category1.setTextColor(G.context.getResources().getColor(R.color.news_red));
                title1.setTextColor(Color.BLACK);
                container1.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
            }
        }

        private void changeToNormalDark(int cell) {
            if (cell == 0) {
                category.setTextColor(Color.WHITE);
                title.setTextColor(Color.WHITE);
                container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
            } else {
                category1.setTextColor(Color.WHITE);
                title1.setTextColor(Color.WHITE);
                container1.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
            }
        }
    }

    public class TripleViewHolder extends RecyclerView.ViewHolder {

        private TextView category;
        private ImageView image;
        private CardView container;

        private TextView category1;
        private ImageView image1;
        private CardView container1;

        private TextView category2;
        private ImageView image2;
        private CardView container2;

        TripleViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.category);
            image = itemView.findViewById(R.id.image);
            container = itemView.findViewById(R.id.container);

            category1 = itemView.findViewById(R.id.category1);
            image1 = itemView.findViewById(R.id.image1);
            container1 = itemView.findViewById(R.id.container1);

            category2 = itemView.findViewById(R.id.category2);
            image2 = itemView.findViewById(R.id.image2);
            container2 = itemView.findViewById(R.id.container2);
        }

        void initTripleVH(int position) {
            if (position * 3 > mData.getmNews().size())
                return;
            category.setText(mData.getmNews().get(position * 3).getCategory());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getmNews().get(position * 3).getNews().get(0).getContents().getImage().get(0).getTmb256())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image);
            container.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position * 3)));
            setColor(0, mData.getmNews().get(position * 3));

            if (position * 3 + 1 > mData.getmNews().size())
                return;
            category1.setText(mData.getmNews().get(position * 3 + 1).getCategory());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getmNews().get(position * 3 + 1).getNews().get(0).getContents().getImage().get(0).getTmb256())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image1);
            container1.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position * 3 + 1)));
            setColor(1, mData.getmNews().get(position * 3 + 1));

            if (position * 3 + 2 > mData.getmNews().size())
                return;
            category2.setText(mData.getmNews().get(position * 3 + 2).getCategory());
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(mData.getmNews().get(position * 3 + 2).getNews().get(0).getContents().getImage().get(0).getTmb256())
                    .placeholder(R.mipmap.news_temp_icon)
                    .into(image2);
            container2.setOnClickListener(v -> callBack.onNewsGroupClick(mData.getmNews().get(position * 3 + 2)));
            setColor(2, mData.getmNews().get(position * 3 + 2));
        }

        private void setColor(int cell, NewsFPList data) {
            if (data.getNews().get(0).getColor().equals("#000000")) {
                // Normal
                changeToNormal(cell);
            } else {
                // color
                changeToServerColor(cell, data.getNews().get(0).getColor(), data.getNews().get(0).getColorTitle());
            }
        }

        private void changeToServerColor(int cell, String backColor, String titleColor) {
            if (cell == 0) {
                category.setTextColor(Color.parseColor(titleColor));
                container.setCardBackgroundColor(Color.parseColor(backColor));
            } else if (cell == 1) {
                category1.setTextColor(Color.parseColor(titleColor));
                container1.setCardBackgroundColor(Color.parseColor(backColor));
            } else {
                category2.setTextColor(Color.parseColor(titleColor));
                container2.setCardBackgroundColor(Color.parseColor(backColor));
            }
        }

        private void changeToNormal(int cell) {
            if (G.themeColor == Theme.DARK) {
                changeToNormalDark(cell);
                return;
            }
            if (cell == 0) {
                category.setTextColor(G.context.getResources().getColor(R.color.news_red));
                container.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
            } else if (cell == 1) {
                category1.setTextColor(G.context.getResources().getColor(R.color.news_red));
                container1.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
            } else {
                category2.setTextColor(G.context.getResources().getColor(R.color.news_red));
                container2.setCardBackgroundColor(G.context.getResources().getColor(R.color.kuknos_WH_itembg));
            }
        }

        private void changeToNormalDark(int cell) {
            if (cell == 0) {
                category.setTextColor(Color.WHITE);
                container.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
            } else if (cell == 1) {
                category1.setTextColor(Color.WHITE);
                container1.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
            } else {
                category2.setTextColor(Color.WHITE);
                container2.setCardBackgroundColor(G.context.getResources().getColor(R.color.chat_item_receive_dark));
            }
        }
    }
}
