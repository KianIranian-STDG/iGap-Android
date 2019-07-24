package net.iGap.fragments.beepTunes.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.beepTunes.BeepTunesAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.BeepTunesProfileFragment;
import net.iGap.fragments.beepTunes.album.AlbumFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.api.beepTunes.Album;

public class BeepTunesMainFragment extends BaseFragment implements ToolbarListener {
    private View rootView;
    private BeepTunesMainViewModel viewModel;
    private BeepTunesAdapter adapter;

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_beeptunes_main, container, false);
        viewModel = new BeepTunesMainViewModel();
        adapter = new BeepTunesAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ProgressBar loadingProgress = rootView.findViewById(R.id.pb_beepTunes_loading);
        LinearLayout toolBar = rootView.findViewById(R.id.tb_beepTunes);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_beepTunes_main);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        initToolBar(toolBar);


        viewModel.getFirstPageMutableLiveData().observe(this, firstPage -> {
            if (firstPage != null)
                adapter.setData(firstPage.getData());
        });

        adapter.setOnItemClick(album -> {
            new HelperFragment(getFragmentManager(), new AlbumFragment().getInstance(album)).setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), loadingProgress::setVisibility);
    }

    private void initToolBar(ViewGroup viewGroup) {
        HelperToolbar helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setSearchBoxShown(true)
                .setLogoShown(true)
                .setListener(this)
                .setRightSmallAvatarShown(true)
                .setLeftIcon(R.string.back_icon);
        viewGroup.addView(helperToolbar.getView());
        avatarHandler.getAvatar(new ParamWithAvatarType(helperToolbar.getAvatarSmall(), G.userId).avatarType(AvatarHandler.AvatarType.USER).showMain());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }

    @Override
    public void onSmallAvatarClickListener(View view) {
        BeepTunesProfileFragment profileFragment = new BeepTunesProfileFragment();
        profileFragment.show(getChildFragmentManager(), null);
    }

    public interface OnItemClick {
        void onClick(Album album);
    }
}
