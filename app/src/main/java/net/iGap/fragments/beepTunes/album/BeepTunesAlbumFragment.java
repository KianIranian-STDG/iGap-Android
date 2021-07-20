package net.iGap.fragments.beepTunes.album;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.beepTunes.BeepTunesAlbumAdapter;
import net.iGap.adapter.beepTunes.BeepTunesTrackAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.observers.interfaces.OnTrackAdapter;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.api.beepTunes.Album;
import net.iGap.module.api.beepTunes.DownloadSong;
import net.iGap.module.api.beepTunes.PlayingSong;
import net.iGap.module.api.beepTunes.Track;
import net.iGap.realm.RealmDownloadSong;

public class BeepTunesAlbumFragment extends BaseAPIViewFrag<AlbumViewModel> implements ToolbarListener {
    private static final String TAG = "aabolfazlAlbumView";
    private static String PATH;

    private View rootView;
    private ImageView albumAvatarIv;
    private ViewGroup actionButton;
    private ProgressBar progressBar;
    private TextView albumNameTv;
    private TextView artistNameTv;
    private TextView albumPriceTv;
    private TextView otherAlbumTv;
    private TextView statusTv;
    private TextView toolBarTv;
    private TextView backIcon;
    private RecyclerView otherAlbumRecyclerView;
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;

    private Album album;
    private PlayingSong playingSong;

    private BeepTunesTrackAdapter trackAdapter;
    private BeepTunesAlbumAdapter albumAdapter;

    private SharedPreferences sharedPreferences;

    private MutableLiveData<DownloadSong> downloadingSongLiveData = new MutableLiveData<>();
    private MutableLiveData<PlayingSong> fromFragmentLiveData;
    private MutableLiveData<PlayingSong> toFragmentLiveData;

    public BeepTunesAlbumFragment getInstance(Album album, MutableLiveData<PlayingSong> toAlbumAdapter, MutableLiveData<PlayingSong> fromAlbumAdapter) {
        BeepTunesAlbumFragment beepTunesAlbumFragment = new BeepTunesAlbumFragment();
        beepTunesAlbumFragment.album = album;
        beepTunesAlbumFragment.fromFragmentLiveData = toAlbumAdapter;
        beepTunesAlbumFragment.toFragmentLiveData = fromAlbumAdapter;
        return beepTunesAlbumFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_album, container, false);
        viewModel = new AlbumViewModel();
        trackAdapter = new BeepTunesTrackAdapter();
        albumAdapter = new BeepTunesAlbumAdapter();
        PATH = getContext().getFilesDir().getAbsolutePath() + "beepTunes";
        sharedPreferences = getContext().getSharedPreferences(SHP_SETTING.KEY_BEEP_TUNES, Context.MODE_PRIVATE);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpViews();

        setUpAlbumInfo(album);

        progressBar.getIndeterminateDrawable().setColorFilter(getContext().getResources().getColor(R.color.beeptunes_primary), PorterDuff.Mode.SRC_IN);
        viewModel.getAlbumSong(album.getId());
        viewModel.getArtistOtherAlbum(album.getArtists().get(0).getId());

        viewModel.getTrackMutableLiveData().observe(this, tracks -> {
            trackAdapter.setTracks(tracks);
            otherAlbumTv.setVisibility(View.VISIBLE);
            otherAlbumRecyclerView.setVisibility(View.VISIBLE);
        });

        viewModel.getAlbumMutableLiveData().observe(this, albums -> {
            if (albums != null) {
                albumAdapter.setAlbums(albums.getData());
            }
        });

        albumAdapter.setOnItemClick(album -> {
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
            appBarLayout.setExpanded(true, true);
            if (!this.album.getId().equals(album.getId())) {
                this.album = album;
                setUpAlbumInfo(album);
                viewModel.getAlbumSong(album.getId());
            }
        });

        viewModel.getLoadingProgressMutableLiveData().observe(this, visibility -> {
            if (visibility != null && visibility) {
                progressBar.setVisibility(View.VISIBLE);
                statusTv.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                statusTv.setVisibility(View.VISIBLE);
            }
        });

        actionButton.setOnClickListener(v -> {

        });

        trackAdapter.setOnTrackAdapter(new OnTrackAdapter() {
            @Override
            public void onDownloadClick(Track track, BeepTunesTrackAdapter.OnSongProgress onSongProgress) {
                viewModel.onDownloadClick(track, PATH, getFragmentManager(), sharedPreferences);
                downloadingSongLiveData.observe(getViewLifecycleOwner(), onSongProgress::progress);
            }

            @Override
            public void onPlayClick(RealmDownloadSong realmDownloadSong, BeepTunesTrackAdapter.OnSongPlay onSongPlay) {
                if (playingSong == null)
                    playingSong = new PlayingSong();
                playingSong.setSongId(realmDownloadSong.getId());
                playingSong.setSongPath(realmDownloadSong.getPath());
                playingSong.setBehaviorStatus(BottomSheetBehavior.STATE_COLLAPSED);
                playingSong.setArtistId(realmDownloadSong.getArtistId());
                playingSong.setAlbumId(realmDownloadSong.getAlbumId());
                toFragmentLiveData.postValue(playingSong);
                fromFragmentLiveData.observe(getViewLifecycleOwner(), onSongPlay::songStatus);
            }
        });

        viewModel.getDownloadStatusMutableLiveData().observe(getViewLifecycleOwner(), downloadSong -> {
            if (downloadSong != null)
                downloadingSongLiveData.postValue(downloadSong);
        });

        backIcon.setOnClickListener(v -> {
            if (getActivity() != null)
                getActivity().onBackPressed();
        });

        appBarLayout.addOnOffsetChangedListener((appBarLayout1, offset) -> {
            if (offset == 0)
                backIcon.setVisibility(View.GONE);
            else
                backIcon.setVisibility(View.VISIBLE);
        });
    }

    private void setUpViews() {
        albumAvatarIv = rootView.findViewById(R.id.iv_albumFragment_header);
        artistNameTv = rootView.findViewById(R.id.tv_album_artistName);
        albumPriceTv = rootView.findViewById(R.id.tv_album_albumeCost);
        RecyclerView songRecyclerView = rootView.findViewById(R.id.rv_album_songs);
        albumNameTv = rootView.findViewById(R.id.tv_album_name);
        otherAlbumRecyclerView = rootView.findViewById(R.id.rv_album_artistAlbums);
        actionButton = rootView.findViewById(R.id.fl_album_actionButton);
        statusTv = rootView.findViewById(R.id.tv_album_play);
        progressBar = rootView.findViewById(R.id.pb_album_progress);
        otherAlbumTv = rootView.findViewById(R.id.tv_album_artistOtherAlbum);
        appBarLayout = rootView.findViewById(R.id.ab_beepTunes_album);
        scrollView = rootView.findViewById(R.id.ns_album);
        toolBarTv = rootView.findViewById(R.id.tv_album_toolBarName);
        backIcon = rootView.findViewById(R.id.tv_album_backIcon);

        songRecyclerView.setNestedScrollingEnabled(false);
        otherAlbumRecyclerView.setNestedScrollingEnabled(false);
        otherAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        songRecyclerView.setAdapter(trackAdapter);
        otherAlbumRecyclerView.setAdapter(albumAdapter);
    }

    private void setUpAlbumInfo(Album album) {
        artistNameTv.setText(album.getArtists().get(0).getName());
        albumNameTv.setText(album.getEnglishName());
        Glide.with(G.context)
                .load(album.getImage())
                .fitCenter()
                .centerInside()
                .error(R.drawable.ic_error)
                .into(albumAvatarIv);
        albumPriceTv.setText(album.getFinalPrice().toString());
        toolBarTv.setText(album.getArtists().get(0).getName() + " | " + album.getName());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStartFragment(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        trackAdapter.onDestroy();
    }
}
