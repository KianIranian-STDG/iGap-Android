package net.iGap.popular;

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
import net.iGap.fragments.BaseFragment;
import net.iGap.popular.adapter.GridAdapter;
import net.iGap.popular.adapter.SliderAdapter;

public class FragmentPopularTwo extends BaseFragment {
    private GridAdapter gridAdapter;
    private SliderAdapter sliderAdapter;
    private RecyclerView rvSliderFragmentTwo;
    private RecyclerView rvGridFragmentTwo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_two, container, false);

        rvSliderFragmentTwo = view.findViewById(R.id.rv_fragment_popular_slider_fragment2);
        rvSliderFragmentTwo.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        SnapHelper snapHelper3 = new PagerSnapHelper();
        snapHelper3.attachToRecyclerView(rvSliderFragmentTwo);
        sliderAdapter = new SliderAdapter(getContext());
        rvSliderFragmentTwo.setAdapter(sliderAdapter);

        rvGridFragmentTwo = view.findViewById(R.id.rv_fragment_popular_grid_fragment2);
        rvGridFragmentTwo.setLayoutManager(new GridLayoutManager(getContext(), 4, LinearLayoutManager.VERTICAL, false));
        gridAdapter = new GridAdapter(getContext());
        rvGridFragmentTwo.setAdapter(gridAdapter);
        return view;
    }
}
