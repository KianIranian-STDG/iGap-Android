package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.api.apiService.ApiResponse;
import net.iGap.api.repository.BeepTunesRepository;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.api.beepTunes.AlbumTrack;

public class BeepTunesFragment extends BaseFragment implements ToolbarListener {
    private View rootView;
    private BeepTunesRepository repository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beep_tunes, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout toolBar = rootView.findViewById(R.id.tb_beepTunes);
        initToolBar(toolBar);
        repository = new BeepTunesRepository();

        repository.getAlbumTrack(511604614, new ApiResponse<AlbumTrack>() {
            @Override
            public void onResponse(AlbumTrack albumTrack) {
                Log.i("aabolfazl", "onResponse: " + albumTrack.getName());
            }

            @Override
            public void onFailed(String error) {
                Log.i("aabolfazl", "onFailed: " + error);
            }

            @Override
            public void onStart() {
                Log.i("aabolfazl", "onStart: ");
            }

            @Override
            public void onFinish() {
                Log.i("aabolfazl", "onFinish: ");
            }
        });

//        BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();

//
//        Call<AlbumTrack> result = apiService.getAlbumeTrack(511604614);
//        result.enqueue(new Callback<AlbumTrack>() {
//            @Override
//            public void onResponse(retrofit2.Call<AlbumTrack> call, Response<AlbumTrack> response) {
//                Log.i("aabolfazl", "onResponse: "+response.toString());
//            }
//
//            @Override
//            public void onFailure(Call<AlbumTrack> call, Throwable t) {
//
//            }
//        });


    }

    private void initToolBar(ViewGroup viewGroup) {
        HelperToolbar helperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setSearchBoxShown(true)
                .setLogoShown(true)
                .setListener(this)
                .setLeftIcon(R.string.back_icon);
        viewGroup.addView(helperToolbar.getView());
    }

    @Override
    public void onLeftIconClickListener(View view) {
        getActivity().onBackPressed();
    }
}
