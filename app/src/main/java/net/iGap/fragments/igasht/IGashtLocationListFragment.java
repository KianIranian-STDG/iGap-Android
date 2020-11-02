package net.iGap.fragments.igasht;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;

import net.iGap.R;
import net.iGap.adapter.igahst.IGashtLocationListAdapter;
import net.iGap.databinding.FragmentIgashtLocationBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.igasht.IGashtLocationViewModel;

public class IGashtLocationListFragment extends IGashtBaseView<IGashtLocationViewModel> {

    private FragmentIgashtLocationBinding binding;

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

        binding.toolbar.addView(HelperToolbar.create()
                .setContext(getContext())
                .setLifecycleOwner(getViewLifecycleOwner())
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.history_icon)
                .setLogoShown(true)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }

                    @Override
                    public void onRightIconClickListener(View view) {
                        if (getActivity() != null) {
                            new HelperFragment(getActivity().getSupportFragmentManager(), new IGashtHistoryPlaceListFragment()).setReplace(false).load(true);
                        }
                    }
                }).getView());

        binding.locationListView.addItemDecoration(new DividerItemDecoration(binding.locationListView.getContext(), DividerItemDecoration.VERTICAL));
        binding.locationListView.setAdapter(new IGashtLocationListAdapter(viewModel.getSelectedProvinceName(), new IGashtLocationListAdapter.onLocationItemClickListener() {
            @Override
            public void buyTicket(int position) {
                viewModel.buyTicket(position);
            }

            @Override
            public void onItem(int position) {
                viewModel.buyTicket(position);
            }
        }));

        viewModel.getLocationList().observe(getViewLifecycleOwner(), data -> {
            if (binding.locationListView.getAdapter() instanceof IGashtLocationListAdapter && data != null) {
                ((IGashtLocationListAdapter) binding.locationListView.getAdapter()).setItems(data);
            }
        });


        viewModel.getGoToLocationDetail().observe(getViewLifecycleOwner(), isGo -> {
            if (getActivity() != null && isGo != null) {
                if (isGo) {
                    new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(new IGashtLocationDetailFragment()).setReplace(false).load(true);
                } else {
                    Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}
