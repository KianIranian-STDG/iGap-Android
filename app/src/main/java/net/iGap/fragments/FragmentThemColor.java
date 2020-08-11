package net.iGap.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.databinding.FragmentThemColorBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.viewmodel.FragmentThemColorViewModel;

import org.jetbrains.annotations.NotNull;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentThemColor extends BaseFragment {


    private FragmentThemColorViewModel fragmentThemColorViewModel;
    private FragmentThemColorBinding fragmentThemColorBinding;


    public FragmentThemColor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentThemColorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_them_color, container, false);
        return attachToSwipeBack(fragmentThemColorBinding.getRoot());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        fragmentThemColorBinding.stnsRippleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mActivity.getSupportFragmentManager().popBackStack();
                popBackStackFragment();
            }
        });

        fragmentThemColorViewModel.goToThemeColorCustomPage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), new FragmentThemColorCustom()).setReplace(false).load();
            }
        });

        fragmentThemColorViewModel.goToDarkThemePage.observe(this, go -> {
            if (getActivity() != null && go != null && go) {
                new HelperFragment(getActivity().getSupportFragmentManager(), FragmentDarkTheme.newInstance()).setReplace(false).load();
            }
        });

        fragmentThemColorViewModel.showDialogChangeTheme.observe(this, data -> {
            if (getActivity() != null && data != null) {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.customization)
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cansel)
                        .onPositive((dialog, which) -> fragmentThemColorViewModel.setNewTheme(data, dialog.isPromptCheckBoxChecked()))
                        .checkBoxPromptRes(R.string.Apply_colors_to_customize, false, null)
                        .show();
            }
        });

        fragmentThemColorViewModel.reCreateApp.observe(this, isRecreate -> {
            if (getActivity() instanceof ActivityEnhanced && isRecreate != null && isRecreate) {
                ((ActivityEnhanced) getActivity()).onRefreshActivity(true, "");
            }
        });
    }

    private void initDataBinding() {
        fragmentThemColorViewModel = new FragmentThemColorViewModel(this, fragmentThemColorBinding);
        fragmentThemColorBinding.setFragmentThemColorViewModel(fragmentThemColorViewModel);

    }
}
