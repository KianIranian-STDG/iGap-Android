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

public class BeepTunesFragment extends BaseFragment implements ToolbarListener {
    private View rootView;
    private BeepTunesViewModel viewModel;
    private BeepTunesAdapter adapter;
    private static String TAG = "aabolfazlBeepTunes";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beep_tunes, container, false);
        viewModel = new BeepTunesViewModel();
        adapter = new BeepTunesAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout toolBar = rootView.findViewById(R.id.tb_beepTunes);
        initToolBar(toolBar);

        RecyclerView recyclerView = rootView.findViewById(R.id.rv_beepTunes_main);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        viewModel.getFirstPageMutableLiveData().observe(this, firstPage -> {
            if (firstPage != null)
                adapter.setData(firstPage.getData());
        });
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClick(album -> {
            new HelperFragment(getFragmentManager(), new AlbumFragment().getInstance(album)).setReplace(false).load();
        });

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
    public void onStart() {
        super.onStart();
        viewModel.onStart();
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
