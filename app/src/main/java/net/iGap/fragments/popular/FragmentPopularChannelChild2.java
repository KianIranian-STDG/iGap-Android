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
import net.iGap.adapter.items.popular.AdapterChildGridItem2;
import net.iGap.adapter.items.popular.AdapterTopSliderItem;
import net.iGap.fragments.BaseFragment;

public class FragmentPopularChannelChild2 extends BaseFragment {
    private AdapterChildGridItem2 adapterChildGridItem2;
    private AdapterTopSliderItem adapterTopSlider2;
    private RecyclerView rvChildGrid2;
    private RecyclerView rvChildSlider2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child2, container, false);
        rvChildGrid2 = view.findViewById(R.id.rv_fragment_popular_child_grid2);
        rvChildSlider2 = view.findViewById(R.id.rv_fragment_popular_child_slider2);
        rvChildGrid2.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        adapterChildGridItem2 = new AdapterChildGridItem2(getContext());
        rvChildGrid2.setAdapter(adapterChildGridItem2);
        rvChildSlider2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterTopSlider2 = new AdapterTopSliderItem(getContext());
        rvChildSlider2.setAdapter(adapterTopSlider2);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvChildSlider2);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
