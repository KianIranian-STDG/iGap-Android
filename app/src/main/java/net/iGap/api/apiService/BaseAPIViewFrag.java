package net.iGap.api.apiService;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.activities.ActivityMain;
import net.iGap.fragments.BaseFragment;

public abstract class BaseAPIViewFrag<T extends BaseAPIViewModel> extends BaseFragment {

    protected T viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeedUpdate -> {
            if (getActivity() instanceof ActivityMain && isNeedUpdate != null && isNeedUpdate) {
                ((ActivityMain) getActivity()).checkGoogleUpdate();
            }
        });

    }

    public void setViewModel(T viewModel) {
        this.viewModel = viewModel;
    }
}
