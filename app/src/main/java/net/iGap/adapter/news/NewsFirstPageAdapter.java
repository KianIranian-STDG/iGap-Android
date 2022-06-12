package net.iGap.adapter.news;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import net.iGap.G;
import net.iGap.R;
import net.iGap.messenger.theme.Theme;
import net.iGap.model.news.NewsFPList;
import net.iGap.model.news.NewsFirstPage;
import net.iGap.model.news.NewsMainBTN;

import java.util.List;

public class NewsFirstPageAdapter extends RecyclerView.Adapter {

    private static final int Slider = 5;
    private static final int doubleButton = 4;
    private static final int singleButton = 3;
    private List<NewsFirstPage> mData;
    private onClickListener callBack;

    public NewsFirstPageAdapter(List<NewsFirstPage> mData) {
        this.mData = mData;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case Slider:
                View sliderViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_slider_item, parent, false);
                viewHolder = new SliderViewHolder(sliderViewHolder);
                /*Display display = G.currentActivity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                viewHolder.itemView.getLayoutParams().height = (int) (size.y *0.35);*/
                break;
            case doubleButton:
                View DounbleBTNViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_double_button_item, parent, false);
                viewHolder = new DoubleBtnHolder(DounbleBTNViewHolder);
                break;
            case singleButton:
                View SingleBTNViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_single_button_item, parent, false);
                viewHolder = new SingleBtnHolder(SingleBTNViewHolder);
                break;
            default:
                View NewsCardViewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_main_page_item, parent, false);
                viewHolder = new NewsCardHolder(NewsCardViewHolder);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        switch (viewType) {
            case Slider:
                ((SliderViewHolder) holder).initSlider(position);
                break;
            case doubleButton:
                ((DoubleBtnHolder) holder).initDoubleBTN(position);
                break;
            case singleButton:
                ((SingleBtnHolder) holder).initSingleBTN(position);
                break;
            default:
                ((NewsCardHolder) holder).initNewsListVH(position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getmType();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<NewsFirstPage> getmData() {
        return mData;
    }

    // *************** getter and setter

    public void setmData(List<NewsFirstPage> mData) {
        this.mData = mData;
    }

    public onClickListener getCallBack() {
        return callBack;
    }

    public void setCallBack(onClickListener callBack) {
        this.callBack = callBack;
    }

    public interface onClickListener {
        void onButtonClick(NewsMainBTN btn);

        void onNewsCategoryClick(NewsFPList channel);

        void onSliderClick(NewsFPList.NewsContent slide);
    }

    // *************** View Holders

    public class SliderViewHolder extends RecyclerView.ViewHolder {

        private SliderView sliderView;

        SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            sliderView = itemView.findViewById(R.id.imageSlider);
            sliderView.setIndicatorSelectedColor(Theme.getColor(Theme.key_theme_color));
        }

        void initSlider(int position) {
            NewsSliderAdapter adapter = new NewsSliderAdapter();
            adapter.setData(mData.get(position).getmNews());
            adapter.setCallBack(new NewsSliderAdapter.onClickListener() {
                @Override
                public void onSliderClick(NewsFPList.NewsContent slide) {
                    callBack.onSliderClick(slide);
                }

                @Override
                public void onSliderTouch(boolean down) {
                    if (down)
                        sliderView.setAutoCycle(false);
                    else {
                        sliderView.setAutoCycle(true);
                        sliderView.startAutoCycle();
                    }
                }
            });
//            adapter.setCallBack(slide -> callBack.onSliderClick(slide));
            sliderView.setSliderAdapter(adapter);
            // set animation
            sliderView.setIndicatorAnimation(IndicatorAnimations.THIN_WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
            sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        }

    }

    public class DoubleBtnHolder extends RecyclerView.ViewHolder {

        private Button btn1;
        private Button btn2;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        DoubleBtnHolder(@NonNull View itemView) {
            super(itemView);
            btn1 = itemView.findViewById(R.id.btn1);
            btn1.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_red)));
            btn2 = itemView.findViewById(R.id.btn2);
            btn2.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_red)));
        }

        void initDoubleBTN(int position) {
            btn1.setText(mData.get(position).getmBtns().get(0).getTitle());
            btn2.setText(mData.get(position).getmBtns().get(1).getTitle());

            Drawable buttonDrawable = btn1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.parseColor(mData.get(position).getmBtns().get(0).getColor()));
            btn1.setBackground(buttonDrawable);

            Drawable buttonDrawable2 = btn2.getBackground();
            buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable2, Color.parseColor(mData.get(position).getmBtns().get(1).getColor()));
            btn2.setBackground(buttonDrawable2);

            btn1.setOnClickListener(v -> callBack.onButtonClick(mData.get(position).getmBtns().get(0)));
            btn2.setOnClickListener(v -> callBack.onButtonClick(mData.get(position).getmBtns().get(1)));
        }

    }

    public class SingleBtnHolder extends RecyclerView.ViewHolder {

        private Button btn1;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        SingleBtnHolder(@NonNull View itemView) {
            super(itemView);
            btn1 = itemView.findViewById(R.id.btn1);
            btn1.setBackgroundTintList(ColorStateList.valueOf(Theme.getColor(Theme.key_red)));
        }

        void initSingleBTN(int position) {
            btn1.setText(mData.get(position).getmBtns().get(0).getTitle());

            Drawable buttonDrawable = btn1.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.parseColor(mData.get(position).getmBtns().get(0).getColor()));
            btn1.setBackground(buttonDrawable);

            btn1.setOnClickListener(v -> callBack.onButtonClick(mData.get(position).getmBtns().get(0)));
        }

    }

    public class NewsCardHolder extends RecyclerView.ViewHolder {

        RecyclerView newsList;

        NewsCardHolder(@NonNull View itemView) {
            super(itemView);
            newsList = itemView.findViewById(R.id.news_list);
        }

        void initNewsListVH(int position) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(G.context, RecyclerView.HORIZONTAL, false);
            newsList.setLayoutManager(layoutManager);
            NewsFPCardsAdapter adapter = new NewsFPCardsAdapter(mData.get(position));
            adapter.setCallback(slide -> callBack.onNewsCategoryClick(slide));
            newsList.setAdapter(adapter);
        }
    }

}
