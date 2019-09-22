package net.iGap.news.view.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.news.repository.model.NewsSlider;

import java.util.List;

public class NewsSliderAdapter extends SliderViewAdapter {

    private List<NewsSlider> data;
    private onClickListener callBack;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_slide_item, null);
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

    class SliderVH extends SliderViewAdapter.ViewHolder {

        ImageView imageViewBackground;
        TextView textViewTitle;
        TextView textViewDescription;

        SliderVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.sliderImage);
            textViewTitle = itemView.findViewById(R.id.sliderTitle);
            textViewDescription = itemView.findViewById(R.id.sliderDesc);
        }

        void initView(int position) {
            textViewTitle.setText("" + data.get(position).getTitle());
            textViewDescription.setText("" + data.get(position).getDesc());
            imageViewBackground.setOnClickListener(v -> callBack.onSliderClick(data.get(position)));
            Picasso.get()
                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
//                    .load(data.get(position).getImage())
                    .placeholder(R.mipmap.news_temp_banner)
                    .into(imageViewBackground);
        }
    }

    public interface onClickListener {
        void onSliderClick(NewsSlider slide);
    }

    public List<NewsSlider> getData() {
        return data;
    }

    public void setData(List<NewsSlider> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public onClickListener getCallBack() {
        return callBack;
    }

    public void setCallBack(onClickListener callBack) {
        this.callBack = callBack;
    }
}
