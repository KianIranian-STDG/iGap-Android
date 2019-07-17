package net.iGap.fragments.beepTunes.album;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toolbar;

import net.iGap.R;
import net.iGap.adapter.beepTunes.AlbumTrackAdapter;
import net.iGap.adapter.beepTunes.ItemAdapter;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.ImageLoadingService;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.api.beepTunes.Album;

public class AlbumFragment extends BaseFragment implements ToolbarListener {
    private static final String TAG = "aabolfazlAlbumView";

    private View rootView;
    private ImageView albumAvatarIv;
    private ViewGroup actionButton;
    private ProgressBar progressBar;
    private TextView albumNameTv;
    private TextView artistNameTv;
    private TextView albumPriceTv;
    private TextView otherAlbumTv;
    private TextView statusTv;
    private RecyclerView otherAlbumRecyclerView;
    private AppBarLayout appBarLayout;
    private NestedScrollView scrollView;

    private AlbumViewModel viewModel;
    private AlbumTrackAdapter adapter;
    private ItemAdapter albumAdapter;
    private Album album;

    public AlbumFragment getInstance(Album album) {
        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.album = album;
        return albumFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_album, container, false);
        viewModel = new AlbumViewModel();
        adapter = new AlbumTrackAdapter();
        albumAdapter = new ItemAdapter();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setUpViews();
        setUpAlbumInfo(album);

        viewModel.getAlbumSong(album.getId());
        viewModel.getArtistOtherAlbum(album.getArtists().get(0).getId());

        viewModel.getTrackMutableLiveData().observe(this, tracks -> {
            adapter.setTracks(tracks);
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

        viewModel.getProgressMutableLiveData().observe(this, visibility -> {
            if (visibility != null && visibility) {
                progressBar.setVisibility(View.VISIBLE);
                statusTv.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                statusTv.setVisibility(View.VISIBLE);
            }
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
        statusTv = rootView.findViewById(R.id.tv_album_status);
        progressBar = rootView.findViewById(R.id.pb_album_progress);
        otherAlbumTv = rootView.findViewById(R.id.tv_album_artistOtherAlbum);
        appBarLayout = rootView.findViewById(R.id.ab_album);
        scrollView = rootView.findViewById(R.id.ns_album);

        songRecyclerView.setNestedScrollingEnabled(false);
        otherAlbumRecyclerView.setNestedScrollingEnabled(false);
        otherAlbumRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        songRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        songRecyclerView.setAdapter(adapter);
        otherAlbumRecyclerView.setAdapter(albumAdapter);
    }

    private void setUpAlbumInfo(Album album) {
        artistNameTv.setText(album.getArtists().get(0).getName());
        albumNameTv.setText(album.getName());
        ImageLoadingService.load(album.getImage(), albumAvatarIv);
        albumPriceTv.setText(album.getFinalPrice().toString());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }
}
