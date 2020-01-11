package net.iGap.mobileBank.view;


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
import net.iGap.databinding.FragmentMobileBankHomeBinding;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.view.adapter.BankCardsAdapter;
import net.iGap.mobileBank.viewmoedel.MobileBankHomeViewModel;
import net.iGap.viewmodel.FragmentCPayViewModel;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeFragment extends Fragment {

    private MobileBankHomeViewModel viewModel;
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
        setupViews(view);
    }

    private void setupViews(View view) {
        List<RealmMobileBankCards> cards = new ArrayList<>();
        cards.add(new RealmMobileBankCards("6221 6698 2154 4752" , "علیرضا نظری" , "02/99" , true));
        cards.add(new RealmMobileBankCards("6221 6698 3145 3456" , "حسین امینی" , "02/99" , true));
        cards.add(new RealmMobileBankCards("6221 6698 9254 6678" , "احسان زرقلمی" , "02/99" , true));
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
