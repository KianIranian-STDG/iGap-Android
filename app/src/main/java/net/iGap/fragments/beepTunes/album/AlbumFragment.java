package net.iGap.fragments.beepTunes.album;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.adapter.beepTunes.AlbumTrackAdapter;
import net.iGap.databinding.FragmentBeeptunesAlbumBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageLoadingService;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.api.beepTunes.Album;

public class AlbumFragment extends BaseFragment implements ToolbarListener {
    private FragmentBeeptunesAlbumBinding binding;
    private AlbumViewModel viewModel;
    private AlbumTrackAdapter adapter;
    private Album album;
    private String TAG = "aabolfazlAlbumView";

    public AlbumFragment getInstance(Album album) {
        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.album = album;
        return albumFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beeptunes_album, container, false);
        viewModel = new AlbumViewModel();
        adapter = new AlbumTrackAdapter();
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        viewModel.getAlbumSong(album.getId());
        viewModel.getArtistOtherAlbum(album.getArtists().get(0).getId());

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setListener(this)
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setRightIcons(R.string.more_icon);

        binding.llAlbumToolBar.addView(toolbar.getView());
        binding.rvAlbumSongs.setLayoutManager(linearLayoutManager);
        binding.rvAlbumSongs.setAdapter(adapter);

        binding.tvAlbumArtistName.setText(album.getArtists().get(0).getName());
        binding.tvAlbumName.setText(album.getName());


        ImageLoadingService.load(album.getImage(), binding.ivAlbumFragmentHeader);


        viewModel.getTrackMutableLiveData().observe(this, tracks -> {
            adapter.setTracks(tracks);
        });

        viewModel.getAlbumMutableLiveData().observe(this,albums -> {

        });
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }
}
