package net.iGap.kuknos.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosEntryOptionBinding;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosEntryOptionVM;

public class KuknosEntryOptionFrag extends BaseFragment {

    private FragmentKuknosEntryOptionBinding binding;
    private KuknosEntryOptionVM kuknosEntryOptionVM;

    public static KuknosEntryOptionFrag newInstance() {
        return new KuknosEntryOptionFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosEntryOptionVM = ViewModelProviders.of(this).get(KuknosEntryOptionVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_entry_option, container, false);
        binding.setViewmodel(kuknosEntryOptionVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosEToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        if (kuknosEntryOptionVM.loginStatus()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(KuknosPanelFrag.class.getName());
            if (fragment == null) {
                fragment = KuknosPanelFrag.newInstance();
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            popBackStackFragment();
        }

        onNewTObserver();
        onRestoreTObserver();
        onRestoreSeedObserver();
    }

    private void onNewTObserver() {

        kuknosEntryOptionVM.getGoNewTPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosSignupInfoFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosSignupInfoFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

    }

    private void onRestoreTObserver() {

        kuknosEntryOptionVM.getGoRestoreTPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosRestoreFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosRestoreFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    private void onRestoreSeedObserver() {
        kuknosEntryOptionVM.getGoRestoreSeedPage().observe(getViewLifecycleOwner(), aBoolean -> {

        });
    }
}
