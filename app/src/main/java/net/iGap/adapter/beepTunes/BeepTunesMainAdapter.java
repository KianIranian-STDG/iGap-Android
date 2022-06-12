package net.iGap.adapter.beepTunes;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.fragments.beepTunes.main.BeepTunesMainFragment;
import net.iGap.libs.bannerslider.BannerSlider;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.Datum;
import net.iGap.module.api.beepTunes.Slide;

import java.util.ArrayList;
import java.util.List;

public class BeepTunesMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ROW = 0;
    private static final int AD = 1;
    private static final String TYPE_AD = "advertisement";
    private static final String TYPE_ROW = "beepTunesCategory";
    private List<Datum> data = new ArrayList<>();
    private BeepTunesMainFragment.OnItemClick onItemClick;

    public void setData(List<Datum> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setOnItemClick(BeepTunesMainFragment.OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        RecyclerView.ViewHolder viewHolder;
        switch (i) {
            case ROW:
                View rowViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_row, viewGroup, false);
                viewHolder = new RowViewHolder(rowViewHolder);
                break;
            case AD:
                View slideViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_slide, viewGroup, false);
                viewHolder = new SlideViewHolder(slideViewHolder);
                break;
            default:
                View defaultViewHolder = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_beeptunes_row, viewGroup, false);
                viewHolder = new RowViewHolder(defaultViewHolder);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        int viewType = viewHolder.getItemViewType();
        switch (viewType) {
            case ROW:
                RowViewHolder holder = (RowViewHolder) viewHolder;
                holder.bindRow(data.get(i).getAlbums());
                holder.headerTv.setText(data.get(i).getInfo().getTitle());
                break;
            case AD:
                SlideViewHolder slideViewHolder = (SlideViewHolder) viewHolder;
                String[] scales = data.get(i).getInfo().getScale().split(":");
                float height = Resources.getSystem().getDisplayMetrics().widthPixels * 1.0f * Integer.parseInt(scales[1]) / Integer.parseInt(scales[0]);
                slideViewHolder.itemView.getLayoutParams().height = Math.round(height);
                slideViewHolder.bindSlid(data.get(i).getSlides(), data.get(i).getInfo().getPlaybackTime());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position).getType().equals(TYPE_ROW))
            return ROW;
        else if (data.get(position).getType().equals(TYPE_AD))
            return AD;
        else return 2;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class SlideViewHolder extends RecyclerView.ViewHolder {
        private BannerSlider slider;

        SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            slider = itemView.findViewById(R.id.bs_advertisementItem);

        }

        void bindSlid(List<Slide> slides, long interval) {

            slider.postDelayed(() -> {
                slider.setAdapter(new BeepTunesBannerSliderAdapter(slides));
                slider.setSelectedSlide(0);
                slider.setInterval((int) interval);
            }, 100);
        }
    }

    class RowViewHolder extends RecyclerView.ViewHolder {
        private RecyclerView recyclerView;
        private TextView headerTv;
        private BeepTunesAlbumAdapter adapter;

        RowViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.rv_rowItem);
            headerTv = itemView.findViewById(R.id.tv_rowItem_header);
            headerTv.setTextColor(Theme.getColor(Theme.key_title_text));
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            adapter = new BeepTunesAlbumAdapter();
            recyclerView.setAdapter(adapter);
        }

        void bindRow(List<Album> albums) {
            adapter.setAlbums(albums);
            if (onItemClick != null)
                adapter.setOnItemClick(onItemClick);
        }
    }
}
