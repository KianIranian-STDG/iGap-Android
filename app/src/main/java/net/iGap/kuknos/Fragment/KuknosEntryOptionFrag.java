package net.iGap.kuknos.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.google.gson.Gson;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosEntryOptionBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.kuknos.Model.KuknosSignupM;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.kuknos.viewmodel.KuknosEntryOptionVM;

public class KuknosEntryOptionFrag extends BaseAPIViewFrag<KuknosEntryOptionVM> {

    private FragmentKuknosEntryOptionBinding binding;

    public static KuknosEntryOptionFrag newInstance() {
        return new KuknosEntryOptionFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosEntryOptionVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_entry_option, container, false);
        binding.setViewmodel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        HelperToolbar mHelperToolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.icon_back)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosEToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        if (viewModel.loginStatus() && isRegisteredSharesPref()) {
            FragmentManager fragmentManager = getChildFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(KuknosEnterPinFrag.class.getName());
            if (fragment == null) {
                fragment = KuknosEnterPinFrag.newInstance(() -> {
                    FragmentManager fragmentManager1 = getChildFragmentManager();
                    FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
                    Fragment fragment1 = fragmentManager1.findFragmentByTag(KuknosPanelFrag.class.getName());
                    if (fragment1 == null) {
                        fragment1 = KuknosPanelFrag.newInstance();
                        fragmentTransaction1.addToBackStack(fragment1.getClass().getName());
                    }
                    new HelperFragment(getActivity().getSupportFragmentManager(), fragment1).setReplace(false).load();
                }, true);
                fragmentTransaction.addToBackStack(fragment.getClass().getName());
            }
            new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
        }

        onNewTObserver();
        onRestoreTObserver();
        onRestoreSeedObserver();
    }

    private boolean isRegisteredSharesPref() {
        SharedPreferences sharedpreferences = getContext().getSharedPreferences("KUKNOS_REGISTER", Context.MODE_PRIVATE);
        KuknosSignupM temp = new Gson().fromJson(sharedpreferences.getString("RegisterInfo", null), KuknosSignupM.class);
        if (temp == null)
            return false;
        return temp.isRegistered();
    }

    private void onNewTObserver() {

        viewModel.getGoNewTPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosShowRecoveryKeyFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosShowRecoveryKeyFrag.newInstance();
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });

    }

    private void onRestoreTObserver() {

        viewModel.getGoRestoreTPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosRestoreFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosRestoreFrag.newInstance(false);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }

    private void onRestoreSeedObserver() {
        viewModel.getGoRestoreSeedPage().observe(getViewLifecycleOwner(), nextPage -> {
            if (nextPage) {
                FragmentManager fragmentManager = getChildFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentManager.findFragmentByTag(KuknosRestoreFrag.class.getName());
                if (fragment == null) {
                    fragment = KuknosRestoreFrag.newInstance(true);
                    fragmentTransaction.addToBackStack(fragment.getClass().getName());
                }
                new HelperFragment(getActivity().getSupportFragmentManager(), fragment).setReplace(false).load();
            }
        });
    }
}
