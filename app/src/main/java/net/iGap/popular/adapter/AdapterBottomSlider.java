package net.iGap.popular.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.popular.model.Slider;

import java.util.ArrayList;
import java.util.List;

public class AdapterBottomSlider extends RecyclerView.Adapter<AdapterBottomSlider.BottomSliderViewHolder> {
    private List<Slider> sliderList = new ArrayList<>();
    private Context context;
    private OnClickSliderEventCallBack onClickSliderEventCallBack;

    public AdapterBottomSlider(Context context) {
        this.context = context;
        Slider sliderBottom=new Slider();
        sliderBottom.setSliderImage(ResourcesCompat.getDrawable(context.getResources(), R.drawable.image_sample, null));
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
        sliderList.add(sliderBottom);
    }

    @NonNull
    @Override
    public BottomSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_slider_bottom, parent, false);
        return new BottomSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BottomSliderViewHolder holder, int i) {
        holder.bindImage(sliderList.get(i));

    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }


    public class BottomSliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public BottomSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_popular_slider_bottom);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickSliderEventCallBack.clickedSlider();
                }
            });
        }
        public void bindImage(final Slider slider) {
            imageView.setImageDrawable(slider.getSliderImage());

        }
    }

    public void setOnClickSliderEventCallBack(OnClickSliderEventCallBack onClickSliderEventCallBack) {
        this.onClickSliderEventCallBack = onClickSliderEventCallBack;
    }

    public interface OnClickSliderEventCallBack {
        void clickedSlider();
    }
}
