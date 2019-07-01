package net.iGap.popular;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.popular.adapter.ChannelAdapter;
import net.iGap.popular.adapter.GridAdapter;
import net.iGap.popular.adapter.SliderAdapter;

public class FragmentPopularChannelOne extends BaseFragment {
    private RecyclerView recyclerViewSliderTop;
    private RecyclerView recyclerViewOne;
    private RecyclerView recyclerViewTwo;
    private RecyclerView recyclerViewThree;
    private RecyclerView recyclerViewSliderBottom;
    private RecyclerView recyclerViewGrid;
    private ChannelAdapter channelAdapter;
    private GridAdapter gridAdapter;
    private SliderAdapter sliderAdapter;
    private ImageView imageViewMore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel, container, false);
        recyclerViewSliderTop = view.findViewById(R.id.rv_fragment_popular_slider_one);
        recyclerViewOne = view.findViewById(R.id.rv_fragment_popular_one);
        recyclerViewTwo = view.findViewById(R.id.rv_fragment_popular_two);
        recyclerViewThree = view.findViewById(R.id.rv_fragment_popular_three);
        recyclerViewSliderBottom = view.findViewById(R.id.rv_fragment_popular_slider_two);
        recyclerViewGrid = view.findViewById(R.id.rv_fragment_popular_grid_bottom);
        imageViewMore=view.findViewById(R.id.iv_more);
        channelAdapter = new ChannelAdapter(getContext());
        gridAdapter = new GridAdapter(getContext());
        sliderAdapter = new SliderAdapter(getContext());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        recyclerViewSliderTop.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewSliderTop.setAdapter(sliderAdapter);
        SnapHelper snapHelper1 = new PagerSnapHelper();
        snapHelper1.attachToRecyclerView(recyclerViewSliderTop);
        recyclerViewSliderBottom.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewSliderBottom.setAdapter(sliderAdapter);
        SnapHelper snapHelper2 = new PagerSnapHelper();
        snapHelper2.attachToRecyclerView(recyclerViewSliderBottom);
        recyclerViewOne.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewOne.setAdapter(channelAdapter);
        recyclerViewTwo.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewTwo.setAdapter(channelAdapter);
        recyclerViewThree.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        recyclerViewThree.setAdapter(channelAdapter);
        recyclerViewGrid.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        recyclerViewGrid.setAdapter(gridAdapter);


        imageViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransition = getFragmentManager().beginTransaction();
                fragmentTransition.add(R.id.rl_fragmentContainer, new FragmentPopularTwo());
                fragmentTransition.commit();
            }
        });

    }
}
