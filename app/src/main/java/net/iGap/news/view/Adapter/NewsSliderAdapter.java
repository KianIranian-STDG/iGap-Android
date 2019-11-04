package net.iGap.news.view.Adapter;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.news.repository.model.NewsFPList;

import java.util.List;

public class NewsSliderAdapter extends SliderViewAdapter {

    private List<NewsFPList> data;
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
        return data.get(0).getNews().size();
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

        @SuppressLint("ClickableViewAccessibility")
        void initView(int position) {
            NewsFPList.NewsContent temp = data.get(0).getNews().get(position).getContents();
            textViewTitle.setText(temp.getTitle());
            textViewDescription.setText(temp.getLead());
            imageViewBackground.setOnClickListener(v -> callBack.onSliderClick(temp));
            imageViewBackground.setOnLongClickListener(null);
            imageViewBackground.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        callBack.onSliderTouch(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        callBack.onSliderTouch(false);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        callBack.onSliderTouch(false);
                        break;
                }
                return false;
            });
            Picasso.get()
//                    .load("https://images.vexels.com/media/users/3/144598/preview2/96a2d7aa32ed86c5e4bd089bdfbd341c-breaking-news-banner-header.jpg")
                    .load(temp.getImage().get(0).getOriginal())
                    .placeholder(R.mipmap.news_temp_banner)
                    .into(imageViewBackground);
        }
    }

    public interface onClickListener {
        void onSliderClick(NewsFPList.NewsContent slide);
        void onSliderTouch(boolean down);
    }

    public List<NewsFPList> getData() {
        return data;
    }

    public void setData(List<NewsFPList> data) {
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
