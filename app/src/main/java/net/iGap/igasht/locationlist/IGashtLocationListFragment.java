package net.iGap.igasht.locationlist;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import net.iGap.R;
import net.iGap.databinding.FragmentIgashtLocationBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperToolbar;
import net.iGap.igasht.provinceselect.IGashtProvince;
import net.iGap.igasht.locationdetail.IGashtLocationDetailFragment;
import net.iGap.interfaces.ToolbarListener;

public class IGashtLocationListFragment extends Fragment {

    private FragmentIgashtLocationBinding binding;
    private IGashtLocationViewModel viewModel;

    private static String SELECTED_PROVINCE = "selectedProvince";

    public static IGashtLocationListFragment getInstance(IGashtProvince province) {
        IGashtLocationListFragment fragment = new IGashtLocationListFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SELECTED_PROVINCE, province);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(IGashtLocationViewModel.class);
        //todo: create factory provider and remove init function;
        if (getArguments() != null) {
            viewModel.init(getArguments().getParcelable(SELECTED_PROVINCE));
        } else {
            HelperLog.setErrorLog(new Exception(this.getClass().getName() + ": selected province data not found"));
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        }
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
                .setLeftIcon(R.string.back_icon)
                .setRightIcons(R.string.score_star_icon, R.string.history_icon)
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

                    }

                    @Override
                    public void onSecondRightIconClickListener(View view) {

                    }
                }).getView());

        binding.locationListView.addItemDecoration(new DividerItemDecoration(binding.locationListView.getContext(), DividerItemDecoration.VERTICAL));
        binding.locationListView.setAdapter(new IGashtLocationListAdapter());

        viewModel.getLocationList().observe(getViewLifecycleOwner(), data -> {
            if (binding.locationListView.getAdapter() instanceof IGashtLocationListAdapter && data != null) {
                ((IGashtLocationListAdapter) binding.locationListView.getAdapter()).setItems(data, new IGashtLocationListAdapter.onLocationItemClickListener() {
                    @Override
                    public void addToFavorite(int position) {
                        viewModel.addToFavorite(position);
                    }

                    @Override
                    public void buyTicket(int position) {
                        viewModel.buyTicket(position);
                    }

                    @Override
                    public void onItem(int position) {
                        viewModel.buyTicket(position);
                    }
                });
            }
        });

        viewModel.getGoToLocationDetail().observe(getViewLifecycleOwner(), data -> {
            if (getActivity() != null && data != null) {
                new HelperFragment(getActivity().getSupportFragmentManager()).setFragment(IGashtLocationDetailFragment.getInstance(data)).setReplace(false).load(true);
            }
        });

        viewModel.getAddToFavorite().observe(getViewLifecycleOwner(), aBoolean -> {
            Toast.makeText(getContext(), "add to favorite", Toast.LENGTH_SHORT).show();
        });
    }
}
