package net.iGap.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.G;
import net.iGap.R;
import net.iGap.databinding.FragmentLanguageBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentLanguageViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLanguage extends BaseFragment {

    private HelperToolbar mHelperToolbar;
    public static boolean languageChanged = false;
    private FragmentLanguageViewModel fragmentLanguageViewModel;
    private FragmentLanguageBinding fragmentLanguageBinding;


    public FragmentLanguage() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentLanguageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_language, container, false);
        if (getArguments() != null && getArguments().containsKey("canSwipeBack")) {
            return fragmentLanguageBinding.getRoot();
        } else {
            return attachToSwipeBack(fragmentLanguageBinding.getRoot());
        }
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDataBinding();
        initToolbar();

    }

    private void initToolbar() {

        mHelperToolbar = HelperToolbar.create()
                .setContext(G.context)
                .setLeftIcon(R.drawable.ic_back_btn)
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
                });

        fragmentLanguageBinding.flLayoutToolbar.addView(mHelperToolbar.getView());
    }

    private void initDataBinding() {
        fragmentLanguageViewModel = new FragmentLanguageViewModel(this);
        fragmentLanguageBinding.setFragmentLanguageViewModel(fragmentLanguageViewModel);

    }
}
