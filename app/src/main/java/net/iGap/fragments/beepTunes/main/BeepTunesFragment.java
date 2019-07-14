package net.iGap.fragments.beepTunes.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.fragments.BeepTunesProfileFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class BeepTunesFragment extends BaseFragment implements ToolbarListener {
    private View rootView;
    private BeepTunesViewModel viewModel;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_beep_tunes, container, false);
        viewModel = new BeepTunesViewModel();
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout toolBar = rootView.findViewById(R.id.tb_beepTunes);
        initToolBar(toolBar);

        recyclerView = rootView.findViewById(R.id.rv_beepTunes_main);




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
    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.onResume();
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
}
