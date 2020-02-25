package net.iGap.fragments.igasht;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.viewmodel.igasht.BaseIGashtViewModel;

public abstract class IGashtBaseView<T extends BaseIGashtViewModel<?>> extends BaseAPIViewFrag<T> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getRequestErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (getActivity() != null && message != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
