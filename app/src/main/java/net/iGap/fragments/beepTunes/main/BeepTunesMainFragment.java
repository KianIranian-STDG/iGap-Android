package net.iGap.fragments.beepTunes.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.iGap.R;
import net.iGap.adapter.beepTunes.BeepTunesLocalSongAdapter;
import net.iGap.adapter.beepTunes.BeepTunesMainAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.beepTunes.BeepTunesLocalSongFragment;
import net.iGap.fragments.beepTunes.BeepTunesProfileFragment;
import net.iGap.fragments.beepTunes.album.BeepTunesAlbumFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.realm.RealmDownloadSong;

import java.util.List;

import static net.iGap.fragments.beepTunes.BeepTunesProfileFragment.FAVORITE_FRAGMENT;
import static net.iGap.fragments.beepTunes.BeepTunesProfileFragment.SYNC_FRAGMENT;

public class BeepTunesMainFragment extends BaseFragment implements ToolbarListener, BeepTunesLocalSongAdapter.OnLocalSongAdapterCallBack {
    private static final String TAG = "aabolfazlBtMainFragment";

    private View rootView;

    private BeepTunesMainViewModel viewModel;
    private BeepTunesMainAdapter adapter;

    private BeepTunesProfileFragment profileFragment;
    private MutableLiveData<PlayingSong> toAlbumAdapter;
    private MutableLiveData<PlayingSong> fromAlbumAdapter;

    public BeepTunesMainFragment getInstance(MutableLiveData<PlayingSong> fromAlbumAdapter, MutableLiveData<PlayingSong> toAlbumAdapter) {
        BeepTunesMainFragment fragment = new BeepTunesMainFragment();
        fragment.fromAlbumAdapter = fromAlbumAdapter;
        fragment.toAlbumAdapter = toAlbumAdapter;
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStartFragment(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_beeptunes_main, container, false);
        viewModel = new BeepTunesMainViewModel();
        adapter = new BeepTunesMainAdapter();
        profileFragment = new BeepTunesProfileFragment();
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
            new HelperFragment(getFragmentManager(), new BeepTunesAlbumFragment().getInstance(album, toAlbumAdapter, fromAlbumAdapter))
                    .setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
        });

        profileFragment.setCallBack(type -> {
            if (type.equals(SYNC_FRAGMENT)) {
                List<RealmDownloadSong> downloadSongs = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.copyFromRealm(realm.where(RealmDownloadSong.class).findAll());
                });
                new HelperFragment(getFragmentManager(), BeepTunesLocalSongFragment.getInstance(downloadSongs, "Sync Song", this))
                        .setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
            } else if (type.equals(FAVORITE_FRAGMENT)) {
                List<RealmDownloadSong> downloadSongs = DbManager.getInstance().doRealmTask(realm -> {
                    return realm.copyFromRealm(realm.where(RealmDownloadSong.class)
                            .equalTo("isFavorite", true).findAll());
                });

                new HelperFragment(getFragmentManager(), BeepTunesLocalSongFragment.getInstance(downloadSongs, "Favorite Song", this))
                        .setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
            }

        });

        viewModel.getProgressMutableLiveData().observe(getViewLifecycleOwner(), loadingProgress::setVisibility);
    }

    private void initToolBar(ViewGroup viewGroup) {
        HelperToolbar helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setSearchBoxShown(true)
                .setLogoShown(true)
                .setListener(this)
                .setRightSmallAvatarShown(true)
                .setLeftIcon(R.string.back_icon);
        viewGroup.addView(helperToolbar.getView());
        avatarHandler.getAvatar(new ParamWithAvatarType(helperToolbar.getAvatarSmall(), AccountManager.getInstance().getCurrentUser().getId()).avatarType(AvatarHandler.AvatarType.USER).showMain());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }

    @Override
    public void onSmallAvatarClickListener(View view) {
        profileFragment.show(getChildFragmentManager(), null);
    }

    @Override
    public void OnLocalSongClick(RealmDownloadSong realmDownloadSong) {
        PlayingSong playingSong = new PlayingSong();
        playingSong.setSongId(realmDownloadSong.getId());
        playingSong.setSongPath(realmDownloadSong.getPath());
        playingSong.setAlbumId(realmDownloadSong.getAlbumId());
        playingSong.setArtistId(realmDownloadSong.getArtistId());
        playingSong.setBehaviorStatus(BottomSheetBehavior.STATE_COLLAPSED);
        fromAlbumAdapter.postValue(playingSong);
    }

    public interface OnItemClick {
        void onClick(Album album);
    }
}
