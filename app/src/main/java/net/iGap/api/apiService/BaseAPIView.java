package net.iGap.api.apiService;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.iGap.activities.ActivityMain;

public abstract class BaseAPIView<G> extends Fragment {

    protected BaseAPIViewModel<G> viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeedUpdate -> {
            if (getActivity() instanceof ActivityMain && isNeedUpdate != null && isNeedUpdate) {
                ((ActivityMain) getActivity()).checkGoogleUpdate();
            }
        });

    }
}
