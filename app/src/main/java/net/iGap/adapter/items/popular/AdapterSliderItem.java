package net.iGap.adapter.items.popular;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import net.iGap.R;
import net.iGap.helper.ImageLoadingService;
import net.iGap.model.PopularChannel.Slide;

import java.util.List;

public class AdapterSliderItem extends RecyclerView.Adapter<AdapterSliderItem.BottomSliderViewHolder> {
    private List<Slide> sliderList ;
    private Context context;

    public AdapterSliderItem(Context context,List<Slide> sliderList) {
        this.context = context;
        this.sliderList=sliderList;
    }

    @NonNull
    @Override
    public BottomSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular_channel_slider, parent, false);
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
            imageView = itemView.findViewById(R.id.iv_item_popular_slider);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void bindImage(final Slide slide) {
            ImageLoadingService.load(slide.getImageUrl(), imageView);
        }
    }


}
