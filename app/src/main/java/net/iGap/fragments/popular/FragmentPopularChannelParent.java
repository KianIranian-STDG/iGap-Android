package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import net.iGap.adapter.items.popular.AdapterBottomSliderItem;
import net.iGap.adapter.items.popular.AdapterLinearChannelItem;
import net.iGap.adapter.items.popular.AdapterParentGridItem;
import net.iGap.adapter.items.popular.AdapterTopSliderItem;
import net.iGap.fragments.BaseFragment;

public class FragmentPopularChannelParent extends BaseFragment {
    private RecyclerView rvTopSlider;
    private RecyclerView rvLinearOne;
    private RecyclerView rvParentLinearTwo;
    private RecyclerView rvParentLinearThree;
    private RecyclerView rvParentBottomSlider;
    private RecyclerView rvParentGrid;
    private AdapterLinearChannelItem adapterLinearChannel;
    private AdapterTopSliderItem adapterTopSlider;
    private AdapterBottomSliderItem adapterBottomSlider;
    private AdapterParentGridItem adapterParentGridItem;
    private ImageView icMoreOne;
    private ImageView icMoreTwo;
    private ImageView icMoreThree;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_parent, container, false);
        rvTopSlider = view.findViewById(R.id.rv_fragment_popular_p_top_slider);
        rvLinearOne = view.findViewById(R.id.rv_fragment_popular_p_linear_one);
        rvParentLinearTwo = view.findViewById(R.id.rv_fragment_popular_p_linear_two);
        rvParentLinearThree = view.findViewById(R.id.rv_fragment_popular_p_linear_three);
        rvParentBottomSlider = view.findViewById(R.id.rv_fragment_popular_p_bottom_slider);
        rvParentGrid = view.findViewById(R.id.rv_fragment_popular_p_grid);
        icMoreOne = view.findViewById(R.id.ic_fragment_popular_p_more_one);
        icMoreTwo = view.findViewById(R.id.ic_fragment_popular_p_more_two);
        icMoreThree = view.findViewById(R.id.ic_fragment_popular_p_more_three);
        adapterLinearChannel = new AdapterLinearChannelItem(getContext());
        adapterTopSlider = new AdapterTopSliderItem(getContext());
        adapterBottomSlider = new AdapterBottomSliderItem(getContext());
        adapterParentGridItem = new AdapterParentGridItem(getContext());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        rvTopSlider.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvTopSlider.setAdapter(adapterTopSlider);
        SnapHelper snapHelper1 = new PagerSnapHelper();
        snapHelper1.attachToRecyclerView(rvTopSlider);
        rvParentBottomSlider.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvParentBottomSlider.setAdapter(adapterBottomSlider);
        SnapHelper snapHelper2 = new PagerSnapHelper();
        snapHelper2.attachToRecyclerView(rvParentBottomSlider);
        rvLinearOne.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvLinearOne.setAdapter(adapterLinearChannel);
        rvParentLinearTwo.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvParentLinearTwo.setAdapter(adapterLinearChannel);
        rvParentLinearThree.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvParentLinearThree.setAdapter(adapterLinearChannel);
        rvParentGrid.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        rvParentGrid.setAdapter(adapterParentGridItem);

        adapterBottomSlider.setOnClickSliderEventCallBack(new AdapterBottomSliderItem.OnClickSliderEventCallBack() {
            @Override
            public void clickedSlider() {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild2());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        adapterParentGridItem.setOnClickedItemEventCallBack(new AdapterParentGridItem.OnClickedItemEventCallBack() {
            @Override
            public void onClickedItem() {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild2());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        icMoreOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild1());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        icMoreTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild1());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        icMoreThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild1());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });

    }
}
