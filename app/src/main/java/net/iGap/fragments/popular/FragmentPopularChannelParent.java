package net.iGap.fragments.popular;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.adapter.items.popular.AdapterGridItem;
import net.iGap.adapter.items.popular.AdapterRowItem;
import net.iGap.adapter.items.popular.AdapterSliderItem;
import net.iGap.api.PopularChannelApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.api.popularChannel.ParentChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FragmentPopularChannelParent extends BaseFragment implements ToolbarListener {
    private RecyclerView rvTopSlider;
    private RecyclerView rvBottomSlider;
    private RecyclerView rvGridItem;

    private LinearLayout linearLayoutItemContainer;
    private RecyclerView rvRowItem;
    private ImageView ivMore;
    private TextView textViewTitle;

    private AdapterSliderItem adapterSliderItemTop;
    private AdapterSliderItem adapterSliderItemBottom;

    private AdapterRowItem adapterRowItem;

    private AdapterGridItem adapterGridItemParent;
    private AdapterGridItem adapterGridItemChild;

    private View rootView;
    private HelperToolbar toolbar;
    private LinearLayout linearLayoutItemRow;

    private PopularChannelApi api;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @NonNull Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_popular_channel_parent, container, false);
//RecyclerView
        rvTopSlider = rootView.findViewById(R.id.rv_fragment_popular_p_top_slider);
        rvBottomSlider = rootView.findViewById(R.id.rv_fragment_popular_p_bottom_slider);
        rvGridItem = rootView.findViewById(R.id.rv_fragment_popular_p_grid);
//Adapter
        adapterSliderItemTop = new AdapterSliderItem(getContext(), false);
        adapterSliderItemBottom = new AdapterSliderItem(getContext(), true);
        adapterRowItem = new AdapterRowItem(getContext());

        adapterGridItemParent = new AdapterGridItem(getContext(), true);
        adapterGridItemChild = new AdapterGridItem(getContext(), false);
//LinearLayoutRowsContainer
        linearLayoutItemContainer = rootView.findViewById(R.id.ll_frag_pop_parent_container_row_item);
        View viewItemRow = LayoutInflater.from(getContext()).inflate(R.layout.item_popular_channel_rowes, container, false);
        rvRowItem = viewItemRow.findViewById(R.id.rv_item_popular_row);
        rvRowItem.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvRowItem.setAdapter(adapterRowItem);
        ivMore = viewItemRow.findViewById(R.id.iv_item_popular_more);
        textViewTitle = viewItemRow.findViewById(R.id.tv_item_popular_title);
        linearLayoutItemContainer.addView(viewItemRow);
//api
        api = ApiServiceProvider.getChannelApi();
        return rootView;

    }


    @Override
    public void onStart() {
        super.onStart();
        rvTopSlider.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvTopSlider.setAdapter(adapterSliderItemTop);
        SnapHelper snapHelper1 = new PagerSnapHelper();
        snapHelper1.attachToRecyclerView(rvTopSlider);

        rvBottomSlider.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        rvBottomSlider.setAdapter(adapterSliderItemBottom);
        SnapHelper snapHelper2 = new PagerSnapHelper();
        snapHelper2.attachToRecyclerView(rvBottomSlider);

        rvGridItem.setLayoutManager(new GridLayoutManager(getContext(), 4, RecyclerView.VERTICAL, false));
        rvGridItem.setAdapter(adapterGridItemParent);
//onClick
        adapterSliderItemBottom.setOnClickSliderEventCallBack(new AdapterSliderItem.OnClickSliderEventCallBack() {
            @Override
            public void clickedSlider() {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelGridInfo());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        adapterGridItemParent.setOnClickedItemEventCallBack(new AdapterGridItem.OnClickedItemEventCallBack() {
            @Override
            public void onClickedItem() {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelGridInfo());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.rl_fragmentContainer, new FragmentPopularChannelRowInfo());
                fragmentTransaction.addToBackStack(null).commit();
            }
        });
        LinearLayout toolbarContainer = rootView.findViewById(R.id.ll_popular_parent_toolbar);
        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پر مخاطب")
                .setLeftIcon(R.string.back_icon);
        toolbarContainer.addView(toolbar.getView());


        api.getParentChannel().enqueue(new Callback<ParentChannel>() {
            @Override
            public void onResponse(Call<ParentChannel> call, Response<ParentChannel> response) {
                for (int i = 0; i < response.body().getData().get(i).getType().length(); i++) {
                    if (response.body().getData().get(i).getType() == "advertisement") {
                        adapterSliderItemTop.setSliderList(response.body().getData().get(i).getSlides());
                    }
                }
                Log.i("nazanin", "onResponse: " + response.body().getData().size());
            }

            @Override
            public void onFailure(Call<ParentChannel> call, Throwable t) {
                Log.i("nazanin", "onFailure: " + t.getMessage());
            }
        });

    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
}
