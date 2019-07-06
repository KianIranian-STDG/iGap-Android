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
import net.iGap.adapter.items.popular.AdapterChildGridItem1;
import net.iGap.adapter.items.popular.AdapterTopSliderItem;
import net.iGap.fragments.BaseFragment;

public class FragmentPopularChannelChild1 extends BaseFragment {
    private AdapterChildGridItem1 adapterChildGridItem1;
    private AdapterTopSliderItem adapterTopSlider1;
    private RecyclerView rvChildGrid1;
    private RecyclerView rvChildSlider1;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child1, container, false);
        rvChildGrid1= view.findViewById(R.id.rv_fragment_popular_child_grid);
        rvChildSlider1 = view.findViewById(R.id.rv_fragment_popular_child_slider);
        rvChildGrid1.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        adapterChildGridItem1 = new AdapterChildGridItem1(getContext());
        rvChildGrid1.setAdapter(adapterChildGridItem1);
        rvChildSlider1.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterTopSlider1 = new AdapterTopSliderItem(getContext());
        rvChildSlider1.setAdapter(adapterTopSlider1);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvChildSlider1);
        return view;
    }
}
