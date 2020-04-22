package net.iGap.fragments.kuknos;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.adapter.kuknos.AddAssetAdvAdapter;
import net.iGap.adapter.kuknos.AddAssetCurrentAdapter;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentKuknosAddAssetBinding;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperToolbar;
import net.iGap.model.kuknos.Parsian.KuknosAsset;
import net.iGap.model.kuknos.Parsian.KuknosBalance;
import net.iGap.module.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.observers.interfaces.ToolbarListener;
import net.iGap.viewmodel.kuknos.KuknosAddAssetVM;

import java.util.ArrayList;
import java.util.List;

public class KuknosAddAssetFrag extends BaseAPIViewFrag<KuknosAddAssetVM> {

    private FragmentKuknosAddAssetBinding binding;

    public static KuknosAddAssetFrag newInstance() {
        return new KuknosAddAssetFrag();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(KuknosAddAssetVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_add_asset, container, false);
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
                .setLeftIcon(R.string.back_icon)
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                })
                .setLogoShown(true);

        LinearLayout toolbarLayout = binding.fragKuknosAddAToolbar;
        toolbarLayout.addView(mHelperToolbar.getView());

        binding.fragKuknosAddARecyclerAdv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.fragKuknosAddARecyclerAdv.setHasFixedSize(true);
        binding.fragKuknosAddARecyclerAdv.setLayoutManager(layoutManager);
        new PagerSnapHelper().attachToRecyclerView(binding.fragKuknosAddARecyclerAdv);

        binding.fragKuknosAddARecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        binding.fragKuknosAddARecycler.setLayoutManager(layoutManager2);

        viewModel.getAccountDataFromServer();
        viewModel.getAssetDataFromServer();
        onErrorObserver();
        onDataChanged();
        onProgress();
        onAddBTN();
    }

    private void onErrorObserver() {

        viewModel.getError().observe(getViewLifecycleOwner(), errorM -> {
            if (errorM.getState()) {
                Snackbar snackbar = Snackbar.make(binding.fragKuknosAddAContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), v -> snackbar.dismiss());
                snackbar.show();
            }
        });

    }

    private void onProgress() {
        viewModel.getProgressState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosAddAProgressV.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosAddAProgressV.setVisibility(View.GONE);
            }
        });

        /*viewModel.getProgressStateAdv().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                binding.fragKuknosAddAProgressVAdv.setVisibility(View.VISIBLE);
            } else {
                binding.fragKuknosAddAProgressVAdv.setVisibility(View.GONE);
            }
        });*/
    }

    private void onDataChanged() {
        viewModel.getAccountPageMutableLiveData().observe(getViewLifecycleOwner(), this::initCurrentAssets);
        viewModel.getAdvAssetPageMutableLiveData().observe(getViewLifecycleOwner(), this::initAdvPager);
    }

    private void onAddBTN() {
        viewModel.getOpenAddList().observe(getViewLifecycleOwner(), integer -> initNewAssetBS(viewModel.getAssetPageMutableLiveData().getValue()));
    }

    private void initNewAssetBS(KuknosAsset response) {
        if (response == null || response.getAssets() == null || response.getAssets().size() == 0) {
            HelperError.showSnackMessage(getResources().getString(R.string.no_item), false);
            return;
        }
        List<String> items = new ArrayList<>();
        for (KuknosAsset.Asset temp : response.getAssets()) {
            items.add(temp.getLabel());
//            items.add(temp.getAsset().getType().equals("native") ? "PMN" : temp.getAssetCode());
        }
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, this::openRegulationsBS);
        bottomSheetFragment.show(getFragmentManager(), "AddAssetBottomSheet");
    }

    private void openRegulationsBS(int position) {
        if (viewModel.getRegulationsAddress(position) == null || viewModel.getRegulationsAddress(position).length() == 0) {
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

    private void initAdvPager(KuknosAsset response) {
        AddAssetAdvAdapter adapter = new AddAssetAdvAdapter(response.getAssets(), getContext(), getDisplayMetrics());
        adapter.setItemMargin((int) getResources().getDimension(R.dimen.pager_margin));
        adapter.updateDisplayMetrics();
        adapter.setListener(this::addAsset);
        binding.fragKuknosAddARecyclerAdv.setAdapter(adapter);
        //binding.fragKuknosAddARecyclerAdv.addItemDecoration(new LinePagerIndicatorDecoration());
    }

    private DisplayMetrics getDisplayMetrics() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics;
    }

    private void addAsset(int position) {
        viewModel.addAsset(position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AddAssetAdvAdapter adapter = new AddAssetAdvAdapter(viewModel.getAdvAssetPageMutableLiveData().getValue().getAssets(), getContext(), getDisplayMetrics());
        adapter.setItemMargin((int) getResources().getDimension(R.dimen.pager_margin));
        adapter.updateDisplayMetrics();
        binding.fragKuknosAddARecyclerAdv.setAdapter(adapter);
        //binding.fragKuknosAddARecyclerAdv.addItemDecoration(new LinePagerIndicatorDecoration());
    }
}
