package net.iGap.activities;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.fragments.DataStoreageFragment;
import net.iGap.viewmodel.ActivityManageSpaceViewModel;

public class ActivityManageSpace extends ActivityEnhanced {

    private ActivityManageSpaceViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(ActivityManageSpaceViewModel.class);

        setContentView(R.layout.activity_manage_space);

        viewModel.setFragment(getSupportFragmentManager().getBackStackEntryCount());

        viewModel.getLoadFirstPage().observe(this, isFirst -> {
            if (isFirst != null) {
                if (isFirst) {
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(DataStoreageFragment.class.getName())
                            .replace(R.id.dataUsageContainer, new DataStoreageFragment(), DataStoreageFragment.class.getName())
                            .commit();
                } else {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.dataUsageContainer);
                    getSupportFragmentManager().beginTransaction()
                            .addToBackStack(fragment.getClass().getName())
                            .replace(R.id.dataUsageContainer, fragment, fragment.getClass().getName())
                            .commit();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            super.onBackPressed();
        } else {
            finish();
        }
    }

    public void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_exit_in_right, R.anim.slide_exit_out_left)
                .add(R.id.dataUsageContainer, fragment, fragment.getClass().getName())
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }
}
