package net.iGap.kuknos.view;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.google.android.material.snackbar.Snackbar;

import net.iGap.R;
import net.iGap.databinding.FragmentKuknosAddAssetBinding;
import net.iGap.dialog.BottomSheetItemClickCallback;
import net.iGap.dialog.bottomsheet.BottomSheetFragment;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.kuknos.service.model.ErrorM;
import net.iGap.kuknos.view.adapter.AddAssetAdvAdapter;
import net.iGap.kuknos.view.adapter.AddAssetCurrentAdapter;
import net.iGap.kuknos.viewmodel.KuknosAddAssetVM;

import org.stellar.sdk.responses.AccountResponse;
import org.stellar.sdk.responses.AssetResponse;
import org.stellar.sdk.responses.Page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KuknosAddAssetFrag extends BaseFragment {

    private FragmentKuknosAddAssetBinding binding;
    private KuknosAddAssetVM kuknosAddAssetVM;
    private HelperToolbar mHelperToolbar;

    public static KuknosAddAssetFrag newInstance() {
        KuknosAddAssetFrag kuknosLoginFrag = new KuknosAddAssetFrag();
        return kuknosLoginFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        kuknosAddAssetVM = ViewModelProviders.of(this).get(KuknosAddAssetVM.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_kuknos_add_asset, container, false);
        binding.setViewmodel(kuknosAddAssetVM);
        binding.setLifecycleOwner(this);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        mHelperToolbar = HelperToolbar.create()
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

        kuknosAddAssetVM.getAccountDataFromServer();
        kuknosAddAssetVM.getAssetDataFromServer();
        onErrorObserver();
        onDataChanged();
        onProgress();
        onAddBTN();
    }

    private void onErrorObserver() {

        kuknosAddAssetVM.getError().observe(getViewLifecycleOwner(), new Observer<ErrorM>() {
            @Override
            public void onChanged(@Nullable ErrorM errorM) {
                if (errorM.getState() == true) {
                    Snackbar snackbar = Snackbar.make(binding.fragKuknosAddAContainer, getString(errorM.getResID()), Snackbar.LENGTH_LONG);
                    snackbar.setAction(getText(R.string.kuknos_Restore_Error_Snack), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        });

    }

    private void onProgress() {
        kuknosAddAssetVM.getProgressState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosAddAProgressV.setVisibility(View.VISIBLE);
                } else {
                    binding.fragKuknosAddAProgressV.setVisibility(View.GONE);
                }
            }
        });

        kuknosAddAssetVM.getProgressStateAdv().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (aBoolean == true) {
                    binding.fragKuknosAddAProgressVAdv.setVisibility(View.VISIBLE);
                } else {
                    binding.fragKuknosAddAProgressVAdv.setVisibility(View.GONE);
                }
            }
        });
    }

    private void onDataChanged() {
        kuknosAddAssetVM.getAccountPageMutableLiveData().observe(getViewLifecycleOwner(), new Observer<AccountResponse>() {
            @Override
            public void onChanged(@Nullable AccountResponse accountResponse) {
                initCurrentAssets(accountResponse);
            }
        });
        kuknosAddAssetVM.getAdvAssetPageMutableLiveData().observe(getViewLifecycleOwner(), new Observer<Page<AssetResponse>>() {
            @Override
            public void onChanged(@Nullable Page<AssetResponse> assetResponsePage) {
                initAdvPager(assetResponsePage);
            }
        });
    }

    private void onAddBTN() {
        kuknosAddAssetVM.getOpenAddList().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                initNewAssetBS(kuknosAddAssetVM.getAssetPageMutableLiveData().getValue());
            }
        });
    }

    private void initNewAssetBS(Page<AssetResponse> response) {
        List<String> items = new ArrayList<>();
        for (AssetResponse temp : response.getRecords()) {
            items.add(temp.getAsset().getType().equals("native") ? "PMN" : temp.getAssetCode());
        }
        BottomSheetFragment bottomSheetFragment = new BottomSheetFragment().setData(items, -1, new BottomSheetItemClickCallback() {
            @Override
            public void onClick(int position) {
                addAsset(position);
            }
        });
        bottomSheetFragment.show(getFragmentManager(), "AddAssetBottomSheet");
    }

    private void initCurrentAssets(AccountResponse accountResponse) {
        AddAssetCurrentAdapter adapter = new AddAssetCurrentAdapter(Arrays.asList(accountResponse.getBalances()), getContext());
        binding.fragKuknosAddARecycler.setAdapter(adapter);
    }

    private void initAdvPager(Page<AssetResponse> response) {
        AddAssetAdvAdapter adapter = new AddAssetAdvAdapter(response.getRecords(), getContext(), getDisplayMetrics());
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
        kuknosAddAssetVM.addAsset(position);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        AddAssetAdvAdapter adapter = new AddAssetAdvAdapter(kuknosAddAssetVM.getAdvAssetPageMutableLiveData().getValue().getRecords(), getContext(), getDisplayMetrics());
        adapter.setItemMargin((int) getResources().getDimension(R.dimen.pager_margin));
        adapter.updateDisplayMetrics();
        binding.fragKuknosAddARecyclerAdv.setAdapter(adapter);
        //binding.fragKuknosAddARecyclerAdv.addItemDecoration(new LinePagerIndicatorDecoration());
    }
}
