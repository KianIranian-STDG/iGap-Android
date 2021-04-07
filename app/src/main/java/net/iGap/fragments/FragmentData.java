package net.iGap.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.activities.ActivityMain;
import net.iGap.databinding.FragmentDataBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentDataViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentData extends BaseFragment {

    private FragmentDataViewModel viewModel;
    private FragmentDataBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentDataViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE));
            }
        }).get(FragmentDataViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_data, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        return attachToSwipeBack(binding.getRoot());
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.fdLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.date))
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                }).getView());

        viewModel.getDateChanged().observe(getViewLifecycleOwner(), isChanged -> {
            if (getActivity() instanceof ActivityMain && isChanged != null && isChanged) {
                Fragment fragment = ((ActivityMain) getActivity()).getFragment(FragmentChatSettings.class.getName());
                if (fragment instanceof FragmentChatSettings) {
                    ((FragmentChatSettings) fragment).dateIsChanged();
                }
                popBackStackFragment();
            }
        });
    }

}
