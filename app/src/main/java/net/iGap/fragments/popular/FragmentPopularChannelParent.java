package net.iGap.fragments.popular;

import android.content.Context;
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
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.adapter.items.popular.AdapterLinearItem;
import net.iGap.adapter.items.popular.AdapterGridItem;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class FragmentPopularChannelParent extends BaseFragment implements ToolbarListener {
    private RecyclerView rvTopSlider;
    private RecyclerView rvBottomSlider;
    private RecyclerView rvLinearOne;
    private RecyclerView rvLinearTwo;
    private RecyclerView rvLinearThree;
    private RecyclerView rvParentGrid;
    private ImageView ivMoreOne;
    private ImageView ivMoreTwo;
    private ImageView ivMoreThree;
    private AdapterSliderItem topAdapterSliderItem;
    private AdapterSliderItem bottomAdapterSliderItem;
    private AdapterLinearItem adapterLinearItem;
    private AdapterGridItem parentAdapterGridItem;
    private AdapterGridItem childAdapterGridItem;
    private View rootView;
    private HelperToolbar toolbar;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_parent, container, false);
        rvTopSlider = rootView.findViewById(R.id.rv_fragment_popular_p_top_slider);
        rvBottomSlider = rootView.findViewById(R.id.rv_fragment_popular_p_bottom_slider);
        rvLinearOne = rootView.findViewById(R.id.rv_fragment_popular_p_linear_one);
        rvLinearTwo = rootView.findViewById(R.id.rv_fragment_popular_p_linear_two);
        rvLinearThree = rootView.findViewById(R.id.rv_fragment_popular_p_linear_three);
        rvParentGrid = rootView.findViewById(R.id.rv_fragment_popular_p_grid);
        ivMoreOne = rootView.findViewById(R.id.iv_frag_popular_more_one);
        ivMoreTwo = rootView.findViewById(R.id.iv_frag_popular_more_two);
        ivMoreThree = rootView.findViewById(R.id.iv_frag_popular_more_three);
        topAdapterSliderItem = new AdapterSliderItem(getContext(),false);
        bottomAdapterSliderItem = new AdapterSliderItem(getContext(),true);
        adapterLinearItem = new AdapterLinearItem(getContext());
        parentAdapterGridItem = new AdapterGridItem(getContext(),true);
        childAdapterGridItem = new AdapterGridItem(getContext(),false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayout toolbarContainer = rootView.findViewById(R.id.ll_popular_parent_toolbar);
        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پر مخاطب")
                .setLeftIcon(R.string.back_icon);
        toolbarContainer.addView(toolbar.getView());
    }

    @Override
    public void onStart() {
        super.onStart();
        rvTopSlider.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvTopSlider.setAdapter(topAdapterSliderItem);
        SnapHelper snapHelper1 = new PagerSnapHelper();
        snapHelper1.attachToRecyclerView(rvTopSlider);
        rvBottomSlider.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvBottomSlider.setAdapter(bottomAdapterSliderItem);
        SnapHelper snapHelper2 = new PagerSnapHelper();
        snapHelper2.attachToRecyclerView(rvBottomSlider);
        rvLinearOne.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvLinearOne.setAdapter(adapterLinearItem);
        rvLinearTwo.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvLinearTwo.setAdapter(adapterLinearItem);
        rvLinearThree.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvLinearThree.setAdapter(adapterLinearItem);
        rvParentGrid.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        rvParentGrid.setAdapter(parentAdapterGridItem);
        bottomAdapterSliderItem.setOnClickSliderEventCallBack(new AdapterSliderItem.OnClickSliderEventCallBack() {
            @Override
            public void clickedSlider() {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        parentAdapterGridItem.setOnClickedItemEventCallBack(new AdapterGridItem.OnClickedItemEventCallBack() {
            @Override
        public void onClickedItem() {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChild());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        ivMoreOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChildLinear());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        ivMoreTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChildLinear());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        ivMoreThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelChildLinear());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });

    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
}
