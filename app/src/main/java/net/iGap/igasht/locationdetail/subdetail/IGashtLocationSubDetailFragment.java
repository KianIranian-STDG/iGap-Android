package net.iGap.igasht.locationdetail.subdetail;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationSubDetailBinding;
import net.iGap.helper.HelperLog;
import net.iGap.igasht.locationlist.LocationDetail;

public class IGashtLocationSubDetailFragment extends Fragment {

    private static String LOCATION_DETAIL = "locationDetail";

    private FragmentIgashtLocationSubDetailBinding binding;
    private IGashtLocationSubDetailViewModel viewModel;

    public static IGashtLocationSubDetailFragment getInstance(LocationDetail locationDetail) {
        IGashtLocationSubDetailFragment fragment = new IGashtLocationSubDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(LOCATION_DETAIL, locationDetail);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationSubDetailViewModel.class);
        //todo: create factory provider and remove init function;
        if (getArguments() != null) {
            viewModel.setLocationDetail(getArguments().getParcelable(LOCATION_DETAIL));
        } else {
            HelperLog.setErrorLog(new Exception(this.getClass().getName() + ": selected location data not found"));
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location_sub_detail, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
