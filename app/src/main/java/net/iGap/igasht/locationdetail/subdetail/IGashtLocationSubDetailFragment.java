package net.iGap.igasht.locationdetail.subdetail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentIgashtLocationSubDetailBinding;

public class IGashtLocationSubDetailFragment extends BaseAPIViewFrag {

    private FragmentIgashtLocationSubDetailBinding binding;
    private IGashtLocationSubDetailViewModel iGashtLocationSubDetailViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iGashtLocationSubDetailViewModel = ViewModelProviders.of(this).get(IGashtLocationSubDetailViewModel.class);
        viewModel = iGashtLocationSubDetailViewModel;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location_sub_detail, container, false);
        binding.setViewModel(iGashtLocationSubDetailViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
