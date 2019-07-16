package net.iGap.igasht;

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
import net.iGap.databinding.FragmentIgashtLocationBinding;

public class IGashtLocationListFragment extends Fragment {

    private FragmentIgashtLocationBinding binding;
    private IGashtLocationViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_igasht_location, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.locationListView.setAdapter(new IGashtLocationListAdapter());

        viewModel.getLocationList().observe(getViewLifecycleOwner(), data -> {
            if (binding.locationListView.getAdapter() instanceof IGashtLocationListAdapter && data != null) {
                ((IGashtLocationListAdapter) binding.locationListView.getAdapter()).setItems(data);
            }
        });

    }
}
