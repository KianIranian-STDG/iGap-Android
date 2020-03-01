package net.iGap.fragments.mobileBank;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperFragment;
import net.iGap.viewmodel.mobileBank.BaseMobileBankViewModel;

public abstract class BaseMobileBankFragment<T extends BaseMobileBankViewModel> extends BaseAPIViewFrag<T> {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getGoToLoginPage().observe(getViewLifecycleOwner(), goToLoginPage -> {
            if (goToLoginPage != null && goToLoginPage) {
                // handel go to login page
                if (getActivity() != null) {
                    new HelperFragment(getActivity().getSupportFragmentManager(), MobileBankLoginFragment.newInstance(true))
                            .setReplace(false)
                            .load();
                }
            }
        });

        viewModel.getShowRequestErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null) {
                HelperError.showSnackMessage(errorMessage, false);
            }
        });
    }

    void showComingSoon() {
        if (getContext() == null) return;
        HelperError.showSnackMessage(getString(R.string.soon), false);
        /*new DialogParsian()
                .setContext(getContext())
                .setTitle(null)
                .setButtonsText(getString(R.string.ok), null)
                .showSimpleMessage(getString(R.string.soon));*/
    }

}
