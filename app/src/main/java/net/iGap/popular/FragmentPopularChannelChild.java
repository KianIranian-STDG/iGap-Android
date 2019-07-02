package net.iGap.popular;

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
import net.iGap.fragments.BaseFragment;
import net.iGap.popular.adapter.AdapterGridChannel;
import net.iGap.popular.adapter.AdapterTopSlider;

public class FragmentPopularChannelChild extends BaseFragment {
    private AdapterGridChannel adapterGridChannel;
    private AdapterTopSlider adapterTopSlider;
    private RecyclerView rvChildGrid;
    private RecyclerView rvChildSlider;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_child, container, false);
        rvChildGrid = view.findViewById(R.id.rv_fragment_popular_child_grid);
        rvChildSlider = view.findViewById(R.id.rv_fragment_popular_child_slider);
        rvChildGrid.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        adapterGridChannel = new AdapterGridChannel(getContext());
        rvChildGrid.setAdapter(adapterGridChannel);
        rvChildSlider.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapterTopSlider = new AdapterTopSlider(getContext());
        rvChildSlider.setAdapter(adapterTopSlider);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(rvChildSlider);
        return view;
    }
}
