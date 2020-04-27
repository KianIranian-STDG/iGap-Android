package net.iGap.fragments.kuknos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.adapter.kuknos.AddAllAssetAdapter;
import net.iGap.adapter.kuknos.AddAssetCurrentAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosCurrentAssetBinding;
import net.iGap.model.kuknos.Parsian.KuknosAsset;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.viewmodel.kuknos.KuknosCurrentAssetVM;

public class KuknosCurrentAssetFrag extends BaseAPIViewFrag<KuknosCurrentAssetVM> {

    private FragmentKuknosCurrentAssetBinding binding;

    public static KuknosCurrentAssetFrag newInstance(int mode) {
        KuknosCurrentAssetFrag frag = new KuknosCurrentAssetFrag();
        Bundle data = new Bundle();
        data.putInt("mode", mode);
        frag.setArguments(data);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosCurrentAssetVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_current_asset, container, false);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        viewModel.setMode(getArguments().getInt("mode"));

        binding.fragKuknosAddARecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        binding.fragKuknosAddARecycler.setLayoutManager(layoutManager2);

        viewModel.getData();
//        viewModel.getAssetDataFromServer();
        onErrorObserver();
        onDataChanged();
        onProgress();
    }

    private void onErrorObserver() {

        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                showSnack(getResources().getString(errorM.getResID()));
            } else {
                showSnack(errorM.getMessage());
            }
        });

    }

    private void showSnack(String message) {
        Snackbar snackbar = Snackbar.make(binding.fragKuknosAddAContainer, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
        snackbar.show();
    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosAddAProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosAddAProgressV.setVisibility(View.GONE);
            }
        });
    }

    private void onDataChanged() {
        viewModel.getAccountPageMutableLiveData().observe(getViewLifecycleOwner(), this::initCurrentAssets);
        viewModel.getAssetPageMutableLiveData().observe(getViewLifecycleOwner(), this::initAllAssets);
    }

    private void openRegulationsBS(int position) {
        if (viewModel.getRegulationsAddress(position) == null || viewModel.getRegulationsAddress(position).length() < 4) {
            addAsset(position);
            return;
        }
        KuknosRegulationsBottomSheetFrag frag = KuknosRegulationsBottomSheetFrag.newInstance(viewModel.getRegulationsAddress(position), accepted -> {
            if (accepted)
                addAsset(position);
        });
        frag.show(getFragmentManager(), "KuknosTokenRegulationsBottomSheet");
    }

    private void initCurrentAssets(KuknosBalance accountResponse) {
        AddAssetCurrentAdapter adapter = new AddAssetCurrentAdapter(accountResponse.getAssets(), getContext());
        binding.fragKuknosAddARecycler.setAdapter(adapter);
    }

    private void initAllAssets(KuknosAsset accountResponse) {
        AddAllAssetAdapter adapter = new AddAllAssetAdapter(accountResponse.getAssets(), getContext(), this::openRegulationsBS);
        binding.fragKuknosAddARecycler.setAdapter(adapter);
    }

    private void addAsset(int position) {
        viewModel.addAsset(position);
    }

}
