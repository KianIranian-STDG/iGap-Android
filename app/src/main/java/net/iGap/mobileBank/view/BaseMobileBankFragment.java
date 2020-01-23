package net.iGap.mobileBank.view;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperError;
import net.iGap.mobileBank.viewmodel.BaseMobileBankViewModel;

public abstract class BaseMobileBankFragment<T extends BaseMobileBankViewModel> extends BaseAPIViewFrag<T> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getGoToLoginPage().observe(getViewLifecycleOwner(), goToLoginPage -> {
            if (goToLoginPage != null && goToLoginPage) {
                // handel go to login page
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });
    }
}
