package net.iGap.fragments;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.databinding.FragmentLanguageBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentLanguageViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLanguage extends BaseFragment {

    public static boolean languageChanged = false;
    private FragmentLanguageViewModel viewModel;
    private FragmentLanguageBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new FragmentLanguageViewModel(getContext().getSharedPreferences(SHP_SETTING.FILE_NAME, MODE_PRIVATE));
            }
        }).get(FragmentLanguageViewModel.class);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_language, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);
        if (getArguments() != null && getArguments().containsKey("canSwipeBack")) {
            return binding.getRoot();
        } else {
            return attachToSwipeBack(binding.getRoot());
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.flLayoutToolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.language))
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getArguments() != null && getArguments().containsKey("canSwipeBack")) {
                            FragmentLanguage.this.removeFromBaseFragment(FragmentLanguage.this);
                        } else {
                            popBackStackFragment();
                        }
                    }
                }).getView());

        viewModel.getRefreshActivityForChangeLanguage().observe(getViewLifecycleOwner(), language -> {
            if (getActivity() instanceof ActivityEnhanced && language != null) {
                G.updateResources(getActivity());
                ((ActivityEnhanced) getActivity()).onRefreshActivity(false, language);
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (isGoBack != null && isGoBack) {
                removeFromBaseFragment(this);
            }
        });

    }


}
