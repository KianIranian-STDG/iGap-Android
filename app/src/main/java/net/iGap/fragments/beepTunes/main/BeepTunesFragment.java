package net.iGap.fragments.beepTunes.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;

public class BeepTunesFragment extends BaseFragment {
    private static String TAG = "aabolfazlBeepTunes";
    private View rootView;
    private BeepTunesViewModel viewModel;
    private BottomSheetBehavior behavior;
    private ConstraintLayout playerLayout;

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
        playerLayout = rootView.findViewById(R.id.cl_beepTunesPlayer);
        behavior = BottomSheetBehavior.from(playerLayout);

        new HelperFragment(getFragmentManager(), new BeepTunesMainFragment()).setResourceContainer(R.id.fl_beepTunes_Container).setAddToBackStack(false).setReplace(false).load();


        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i(TAG, "hidden");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i(TAG, "expanded");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i(TAG, "collapsed");
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i(TAG, "dragging");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i(TAG, "settiling");

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        viewModel.onStart();
    }
}
