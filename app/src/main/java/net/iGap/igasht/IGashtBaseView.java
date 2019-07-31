package net.iGap.igasht;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import net.iGap.R;

public abstract class IGashtBaseView<G> extends Fragment {

    protected BaseIGashtViewModel<G> viewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel.getUpdateGooglePlay().observe(getViewLifecycleOwner(), isNeedUpdate -> {
            if (getActivity() != null && isNeedUpdate != null && isNeedUpdate) {
                try {
                    if (getActivity() != null) {
                        ProviderInstaller.installIfNeeded(getActivity().getApplicationContext());
                    }
                } catch (GooglePlayServicesRepairableException e) {
                    // Prompt the user to install/update/enable Google Play services.
                    GoogleApiAvailability.getInstance().showErrorNotification(getActivity(), e.getConnectionStatusCode());
                } catch (GooglePlayServicesNotAvailableException e) {
                    // Indicates a non-recoverable error: let the user know.
                    showDialogNeedGooglePlay();

                }
            }
        });

        viewModel.getRequestErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (getActivity() != null && message != null) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogNeedGooglePlay() {
        if (getActivity() != null) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.attention).titleColor(Color.parseColor("#1DE9B6"))
                    .titleGravity(GravityEnum.CENTER)
                    .buttonsGravity(GravityEnum.CENTER)
                    .content("برای استفاده از این بخش نیاز به گوگل سرویس است.").contentGravity(GravityEnum.CENTER)
                    .positiveText(R.string.ok).onPositive((dialog, which) -> dialog.dismiss())
                    .show();
        }
    }
}
