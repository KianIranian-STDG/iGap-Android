package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterChildLinearGridItem;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.fragments.BaseFragment;

public class FragmentPopularChannelChildLinear extends BaseFragment {
    private RecyclerView recyclerViewSlider1;
    private RecyclerView recyclerViewGridItem1;
    private AdapterChildLinearGridItem adapterGridItem1;
    private AdapterSliderItem adapterSliderItem1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child_linear, container, false);
        recyclerViewSlider1 = view.findViewById(R.id.rv_fragment_popular_child_slider_linear);
        recyclerViewSlider1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerViewSlider1);
        adapterSliderItem1 = new AdapterSliderItem(getContext(), false);
        recyclerViewSlider1.setAdapter(adapterSliderItem1);
        recyclerViewGridItem1 = view.findViewById(R.id.rv_fragment_popular_child_grid_linear);
        recyclerViewGridItem1.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        adapterGridItem1 = new AdapterChildLinearGridItem(getContext());
        recyclerViewGridItem1.setAdapter(adapterGridItem1);
        return view;

    }
}
