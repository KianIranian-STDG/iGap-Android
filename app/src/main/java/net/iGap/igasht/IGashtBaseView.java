package net.iGap.igasht;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.activities.ActivityMain;
import net.iGap.api.apiService.BaseAPIViewFrag;

public abstract class IGashtBaseView<G> extends BaseAPIViewFrag {

    protected BaseIGashtViewModel<G> viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeedUpdate -> {
            if (getActivity() instanceof ActivityMain && isNeedUpdate != null && isNeedUpdate) {
                ((ActivityMain) getActivity()).checkGoogleUpdate();
            }
        });

        viewModel.getRequestErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (getActivity() != null && message != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
