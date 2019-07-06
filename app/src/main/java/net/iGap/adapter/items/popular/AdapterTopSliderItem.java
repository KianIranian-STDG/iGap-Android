package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.adapter.items.popular.model.Slider;

import java.util.ArrayList;
import java.util.List;

public class AdapterTopSliderItem extends RecyclerView.Adapter<AdapterTopSliderItem.SliderViewHolder> {
    private List<Slider> sliderList = new ArrayList<>();
    private Context context;

    public AdapterTopSliderItem(Context context) {

        this.context = context;
        Slider sliderTop = new Slider();
        sliderTop.setSliderImage(ResourcesCompat.getDrawable(context.getResources(), R.drawable.image_sample, null));
        sliderList.add(sliderTop);
        sliderList.add(sliderTop);
        sliderList.add(sliderTop);
        sliderList.add(sliderTop);
        sliderList.add(sliderTop);
        sliderList.add(sliderTop);
        sliderList.add(sliderTop);
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_slider_top, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int i) {
        holder.bindImage(sliderList.get(i));
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewHolder(@NonNull final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_popular_slider_top);
        }

        public void bindImage(final Slider slider) {
            imageView.setImageDrawable(slider.getSliderImage());
        }
    }
}
