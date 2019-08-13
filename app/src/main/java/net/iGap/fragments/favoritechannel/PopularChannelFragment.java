package net.iGap.fragments.favoritechannel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.model.FavoriteChannel.Category;
import net.iGap.model.FavoriteChannel.Channel;

public class PopularChannelFragment extends BaseFragment implements ToolbarListener {
    private RecyclerView recyclerView;
    private PopularChannelViewModel viewModel;
    private PopularChannelHomeAdapter adapter;

    private View rootView;
    private HelperToolbar toolbar;
    private LinearLayout toolBall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_popular_channel, container, false);
        viewModel = new PopularChannelViewModel();
        adapter = new PopularChannelHomeAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews();
        viewModel.onStartFragment(this);

        viewModel.getFirstPageMutableLiveData().observe(getViewLifecycleOwner(), parentChannel -> {
            if (parentChannel != null)
                adapter.setData(parentChannel.getData());
        });

        adapter.setCallBack(new PopularChannelHomeAdapter.OnFavoriteChannelCallBack() {
            @Override
            public void onCategoryClick(Category category) {
                viewModel.onCategoryClick(category);
            }

            @Override
            public void onChannelClick(Channel channel) {
                viewModel.onChannelClick(channel);
            }

            @Override
            public void onSlideClick(int position) {
                viewModel.onSlideClick(position);
            }

        });
    }

    private void setupViews() {
        toolbar = HelperToolbar.create()
                .setContext(G.fragmentActivity)
                .setListener(this)
                .setLogoShown(true)
                .setDefaultTitle("کانال های پرمخاطب")
                .setLeftIcon(R.string.back_icon);
        if (G.selectedLanguage.equals("en")) {
            toolbar.setDefaultTitle("Favorite Channel");
        }

        recyclerView = rootView.findViewById(R.id.rv_popularChannel_home);
        toolBall = rootView.findViewById(R.id.ll_popularChannel_toolBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        toolBall.addView(toolbar.getView());

    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }
}
