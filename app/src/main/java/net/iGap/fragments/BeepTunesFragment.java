package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;

public class BeepTunesFragment extends BaseFragment implements ToolbarListener {
    private View rootView;
    private HelperToolbar helperToolbar;

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

    }

    private void initToolBar(ViewGroup viewGroup) {
        helperToolbar = HelperToolbar.create()
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
