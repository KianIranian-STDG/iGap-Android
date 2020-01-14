package net.iGap.mobileBank.view;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iGap.R;
import net.iGap.api.apiService.BaseAPIViewFrag;
import net.iGap.databinding.FragmentMobileBankHomeBinding;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.HelperToolbar;
import net.iGap.interfaces.ToolbarListener;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.view.adapter.BankCardsAdapter;
import net.iGap.mobileBank.viewmoedel.MobileBankHomeViewModel;
import net.iGap.viewmodel.FragmentCPayViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeFragment extends BaseAPIViewFrag<MobileBankHomeViewModel> {

    private FragmentMobileBankHomeBinding binding;

    public MobileBankHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_mobile_bank_home, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setVm(viewModel);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MobileBankHomeViewModel.class);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupToolbar();
        setupViewPager();
        setupListeners();
    }

    private void setupListeners() {

        viewModel.onTempPassListener.observe(getViewLifecycleOwner() , state -> {

            if (state != null && state && getActivity() != null){
                new DialogParsian()
                        .setContext(getActivity())
                        .setTitle("پیام سیستم")
                        .setButtonsText(getString(R.string.ok) , getString(R.string.cancel))
                        .setListener(new DialogParsian.ParsianDialogListener() {
                            @Override
                            public void onActiveButtonClicked(Dialog dialog) {

                            }

                            @Override
                            public void onDeActiveButtonClicked(Dialog dialog) {

                            }
                        }).showSimpleMessage(getString(R.string.kuknos_buyP_MaxAmount));
            }

        });

        viewModel.onMoneyTransferListener.observe(getViewLifecycleOwner() , state -> {

            if (state != null && state && getActivity() != null){
                new DialogParsian()
                        .setContext(getActivity())
                        .setTitle(getString(R.string.transfer_mony))
                        .setButtonsText(getString(R.string.ok) , getString(R.string.cancel))
                        .setListener(new DialogParsian.ParsianDialogListener() {
                            @Override
                            public void onActiveButtonClicked(Dialog dialog) {

                            }

                            @Override
                            public void onTransferMoneyTypeSelected(Dialog dialog, String type) {
                                dialog.dismiss();
                            }
                        }).showMoneyTransferDialog();
            }

        });

        viewModel.onTransactionListener.observe(getViewLifecycleOwner() , state -> {

            if (state != null && state && getActivity() != null){
                new HelperFragment(getActivity().getSupportFragmentManager() , new MobileBankCardHistoryFragment())
                        .setReplace(false)
                        .load();
            }

        });
    }

    private void setupToolbar() {

        HelperToolbar toolbar = HelperToolbar.create()
                .setContext(getContext())
                .setLogoShown(true)
                .setRoundBackground(false)
                .setLeftIcon(R.string.back_icon)
                .setLifecycleOwner(getViewLifecycleOwner())
                .setListener(new ToolbarListener() {
                    @Override
                    public void onLeftIconClickListener(View view) {
                        popBackStackFragment();
                    }
                });

        binding.toolbar.addView(toolbar.getView());
    }

    private void setupViewPager() {
        List<RealmMobileBankCards> cards = new ArrayList<>();
        cards.add(new RealmMobileBankCards("6221 6698 2154 4752" , "علیرضا نظری" , "بانک پارسیان", "02/99" , true));
        cards.add(new RealmMobileBankCards("6221 6698 3145 3456" , "حسین امینی" , "بانک پارسیان", "02/99" , true));
        cards.add(new RealmMobileBankCards("6221 6698 9254 6678" , "احسان زرقلمی" , "بانک پارسیان", "02/99" , true));
        cards.add(null);
        binding.vpCards.setAdapter(new BankCardsAdapter(cards));
        binding.vpCards.setOffscreenPageLimit(cards.size() - 1);

        binding.vpCards.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setEnableIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        createViewPagerIndicators(cards.size());
    }

    private void createViewPagerIndicators(int size) {
        for (int i=0 ; i < size ; i++){
            ImageView iv = new ImageView(binding.lytIndicators.getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(28 , 28);
            lp.setMargins(6 , 6 , 6 , 6 );
            iv.setLayoutParams(lp);
            iv.setBackgroundResource(R.drawable.indicator_slider);
            if (i == 0) iv.setSelected(true);
            binding.lytIndicators.addView(iv);
        }
    }

    private void setEnableIndicator(int position){
        int count = binding.lytIndicators.getChildCount();
        if (position > count) return;
        for (int i=0 ; i < count ; i++){
            binding.lytIndicators.getChildAt(i).setSelected(i == position);
        }
    }
}
