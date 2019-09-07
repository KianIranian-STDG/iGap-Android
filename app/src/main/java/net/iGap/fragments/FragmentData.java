package net.iGap.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import net.iGap.R;
import net.iGap.databinding.FragmentDataBinding;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.viewmodel.FragmentDataViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentData extends BaseFragment {

    public static OnFragmentRemoveData onFragmentRemoveData;
    private FragmentDataViewModel fragmentDataViewModel;
    private FragmentDataBinding fragmentDataBinding;

    public FragmentData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_data, container, false);
        return attachToSwipeBack(fragmentDataBinding.getRoot());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDataBinding();

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setDefaultTitle(getString(R.string.date))
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        fragmentDataBinding.fdLayoutToolbar.addView(toolbar.getView());

        onFragmentRemoveData = new OnFragmentRemoveData() {
            @Override
            public void removeFragment() {
                removeFromBaseFragment(FragmentData.this);
            }
        };
    }

    private void initDataBinding() {
        fragmentDataViewModel = new FragmentDataViewModel();
        fragmentDataBinding.setFragmentDataViewModel(fragmentDataViewModel);
    }

    public interface OnFragmentRemoveData {
        void removeFragment();
    }

}
