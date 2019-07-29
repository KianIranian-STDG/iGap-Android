package net.iGap.fragments.beepTunes.main;

import android.arch.lifecycle.MutableLiveData;
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
import net.iGap.fragments.beepTunes.BeepTunesLocalSongAdapter;
import net.iGap.fragments.beepTunes.BeepTunesLocalSongFragment;
import net.iGap.fragments.beepTunes.BeepTunesProfileFragment;
import net.iGap.fragments.beepTunes.album.AlbumFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.realm.RealmDownloadSong;

import java.util.List;

import io.realm.Realm;

import static net.iGap.fragments.beepTunes.BeepTunesProfileFragment.FAVORITE_FRAGMENT;
import static net.iGap.fragments.beepTunes.BeepTunesProfileFragment.SYNC_FRAGMENT;

public class BeepTunesMainFragment extends BaseFragment implements ToolbarListener, BeepTunesLocalSongAdapter.OnLocalSongAdapterCallBack {
    private static final String TAG = "aabolfazlBtMainFragment";
    private View rootView;
    private BeepTunesMainViewModel viewModel;
    private BeepTunesAdapter adapter;
    private Realm realm;
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
        adapter = new BeepTunesAdapter();
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
            new HelperFragment(getFragmentManager(), new AlbumFragment().getInstance(album, toAlbumAdapter, fromAlbumAdapter))
                    .setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
        });

        profileFragment.setCallBack(type -> {
            if (type.equals(SYNC_FRAGMENT)) {
                List<RealmDownloadSong> downloadSongs = getRealm().copyFromRealm(getRealm().where(RealmDownloadSong.class).findAll());
                new HelperFragment(getFragmentManager(), BeepTunesLocalSongFragment.getInstance(downloadSongs, "Sync Song", this))
                        .setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
            } else if (type.equals(FAVORITE_FRAGMENT)) {
                List<RealmDownloadSong> downloadSongs = getRealm().copyFromRealm(getRealm().where(RealmDownloadSong.class)
                        .equalTo("isFavorite", true).findAll());

                new HelperFragment(getFragmentManager(), BeepTunesLocalSongFragment.getInstance(downloadSongs, "Favorite Song", this))
                        .setResourceContainer(R.id.fl_beepTunes_Container).setReplace(false).load();
            }

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
        profileFragment.show(getChildFragmentManager(), null);
    }


    private Realm getRealm() {
        if (realm == null)
            realm = Realm.getDefaultInstance();
        return realm;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!realm.isClosed())
            realm.close();
    }

    @Override
    public void OnLocalSongClick(RealmDownloadSong realmDownloadSong) {
        PlayingSong playingSong = new PlayingSong();
        playingSong.setSongId(realmDownloadSong.getId());
        playingSong.setSongPath(realmDownloadSong.getPath());
        playingSong.setAlbumId(realmDownloadSong.getAlbumId());
        playingSong.setArtistId(realmDownloadSong.getArtistId());
        playingSong.setFromPlayer(false);
        fromAlbumAdapter.postValue(playingSong);
    }

    public interface OnItemClick {
        void onClick(Album album);
    }
}
