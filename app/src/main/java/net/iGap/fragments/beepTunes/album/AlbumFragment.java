package net.iGap.fragments.beepTunes.album;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentBeeptunesAlbumBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.ImageLoadingService;
import net.iGap.interfaces.ToolbarListener;

public class AlbumFragment extends BaseFragment implements ToolbarListener {
    private FragmentBeeptunesAlbumBinding binding;
    private HelperToolbar toolbar;
    private AlbumViewModel viewModel;
    private AlbumTrackAdapter adapter;
    private String TAG = "aabolfazlAlbumView";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beeptunes_album, container, false);
        viewModel = new AlbumViewModel();
        adapter = new AlbumTrackAdapter();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        viewModel.loadAlbum();

        toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setListener(this)
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setRightIcons(R.string.more_icon);

        binding.llAlbumToolBar.addView(toolbar.getView());
        binding.rvAlbumSongs.setLayoutManager(linearLayoutManager);
        binding.rvAlbumSongs.setAdapter(adapter);

        viewModel.albumImageUrl.observe(this, imageUrl -> {
            ImageLoadingService.load(imageUrl, binding.ivAlbumFragmentHeader);
        });

        viewModel.albumName.observe(this,albumName ->toolbar.setDefaultTitle(albumName));

        viewModel.albumTrackMutableLiveData.observe(this,tracks -> {
            adapter.setTracks(tracks);
            Log.i(TAG, "onViewCreated: "+tracks.size());
        });



    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
}
