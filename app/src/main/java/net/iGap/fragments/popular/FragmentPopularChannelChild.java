package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterGridItem;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.fragments.BaseFragment;

public class FragmentPopularChannelChild extends BaseFragment {
    private RecyclerView recyclerViewSlider2;
    private RecyclerView recyclerViewGridItem2;
    private AdapterGridItem adapterGridItem2;
    private AdapterSliderItem adapterSliderItem2;


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child, container, false);
        recyclerViewSlider2 = view.findViewById(R.id.rv_fragment_popular_child_slider);
        recyclerViewSlider2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewSlider2);
        adapterSliderItem2 = new AdapterSliderItem(getContext(), false);
        recyclerViewSlider2.setAdapter(adapterSliderItem2);
        recyclerViewGridItem2 = view.findViewById(R.id.rv_fragment_popular_child_grid);
        recyclerViewGridItem2.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        adapterGridItem2 = new AdapterGridItem(getContext(), false);
        recyclerViewGridItem2.setAdapter(adapterGridItem2);
        return view;
    }
}
