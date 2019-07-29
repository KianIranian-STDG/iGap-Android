package net.iGap.fragments.beepTunes;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.realm.RealmDownloadSong;

import java.util.List;

public class BeepTunesLocalSongFragment extends BaseFragment implements ToolbarListener, BeepTunesLocalSongAdapter.OnLocalSongAdapterCallBack {
    private static final String TAG = "aabolfazlSyncSong";
    private View rootView;
    private String title;
    private List<RealmDownloadSong> downloadSongs;
    private BeepTunesLocalSongAdapter.OnLocalSongAdapterCallBack onLocalSongAdapterCallBack;

    public static BeepTunesLocalSongFragment getInstance(List<RealmDownloadSong> realmDownloadSongs, String title,
                                                         BeepTunesLocalSongAdapter.OnLocalSongAdapterCallBack onLocalSongAdapterCallBack) {
        BeepTunesLocalSongFragment fragment = new BeepTunesLocalSongFragment();
        fragment.downloadSongs = realmDownloadSongs;
        fragment.title = title;
        fragment.onLocalSongAdapterCallBack = onLocalSongAdapterCallBack;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beeptunes_sync_song, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = rootView.findViewById(R.id.rv_Song);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setAdapter(new BeepTunesLocalSongAdapter(downloadSongs, this));

        LinearLayout toolBarContainer = rootView.findViewById(R.id.ll_syncSong_toolBar);
        HelperToolbar helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(title)
                .setLeftIcon(R.string.back_icon)
                .setListener(this);

        toolBarContainer.addView(helperToolbar.getView());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        if (getActivity() != null)
            getActivity().onBackPressed();
    }

    @Override
    public void OnLocalSongClick(RealmDownloadSong realmDownloadSong) {
        if (onLocalSongAdapterCallBack != null) {
            onLocalSongAdapterCallBack.OnLocalSongClick(realmDownloadSong);
        }
    }
}
