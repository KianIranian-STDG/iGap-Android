package net.iGap.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.databinding.FragmentLanguageBinding;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.ui.toolBar.BackDrawable;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.SHP_SETTING;
import net.iGap.viewmodel.FragmentLanguageViewModel;

import org.jetbrains.annotations.NotNull;

import static android.content.Context.MODE_PRIVATE;

public class FragmentLanguage extends BaseFragment {

    public static boolean languageChanged = false;
    private FragmentLanguageViewModel viewModel;
    private FragmentLanguageBinding binding;
    private Toolbar languageToolbar;


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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        languageToolbar = new Toolbar(getContext());
        languageToolbar.setBackIcon(new BackDrawable(false));
        languageToolbar.setTitle(getString(R.string.language));
        languageToolbar.setListener(i -> {
            switch (i) {
                case -1:
                    popBackStackFragment();
                    break;
            }
        });
        binding.flLayoutToolbar.addView(languageToolbar, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.dp(56), Gravity.TOP));

        viewModel.getRefreshActivityForChangeLanguage().observe(getViewLifecycleOwner(), language -> {
            if (getActivity() instanceof ActivityEnhanced && language != null) {
                G.updateResources(getActivity().getBaseContext());

                if (G.twoPaneMode) {
                    Fragment frg;
                    frg = getActivity().getSupportFragmentManager().findFragmentById(R.id.mainFrame);
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ft.detach(frg);
                    ft.attach(frg);
                    ft.commit();
                }

                /*((ActivityEnhanced) getActivity()).onRefreshActivity(false, language);*/
                /*if (getActivity() instanceof ActivityRegistration) {*/
                    getActivity().onBackPressed();
                /*}*/
            }
        });

        viewModel.getGoBack().observe(getViewLifecycleOwner(), isGoBack -> {
            if (isGoBack != null && isGoBack) {
                removeFromBaseFragment(this);
            }
        });
    }
}
