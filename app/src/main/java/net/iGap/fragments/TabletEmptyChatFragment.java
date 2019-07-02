package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.helper.HelperToolbar;

public class TabletEmptyChatFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tablet_empty_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewGroup toolbar = view.findViewById(R.id.toolbar);
        toolbar.addView(HelperToolbar.create().setContext(getContext()).setLogoShown(true).getView());

        view.findViewById(R.id.goToSetting).setOnClickListener(v -> {
            if (getActivity() instanceof ActivityMain) {
                ((ActivityMain) getActivity()).goToUserProfile();
            }
        });
    }
}
