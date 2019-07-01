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

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<Slider> sliderList = new ArrayList<>();
    private Context context;


    public SliderAdapter(Context context) {
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
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_slider, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.bindImage(sliderList.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderList.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_popular_slider);
        }

        public void bindImage(Slider slider) {
            imageView.setImageDrawable(slider.getSliderImage());
        }
    }

}
