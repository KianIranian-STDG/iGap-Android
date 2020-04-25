package net.iGap.adapter.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import net.iGap.G;
import net.iGap.R;
import net.iGap.model.news.NewsImage;

import java.util.List;

public class NewsDetailSliderAdapter extends SliderViewAdapter {

    private List<NewsImage> data;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_detail_slide_item, null);
        return new SliderVH(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        ((SliderVH) viewHolder).initView(position);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    public List<NewsImage> getData() {
        return data;
    }

    public void setData(List<NewsImage> data) {
        this.data = data;
    }

    class SliderVH extends ViewHolder {

        ImageView imageViewBackground;

        SliderVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.sliderImage);
        }

        void initView(int position) {
            Picasso.with(G.context)
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(data.get(position).getOriginal())
                    .placeholder(R.mipmap.news_temp_banner)
                    .into(imageViewBackground);
        }
    }
}
