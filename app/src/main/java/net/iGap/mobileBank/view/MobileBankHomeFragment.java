package net.iGap.mobileBank.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.view.adapter.BankCardsAdapter;

import java.util.ArrayList;
import java.util.List;

public class MobileBankHomeFragment extends Fragment {

    public MobileBankHomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mobile_bank_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
    }

    private void setupViews(View view) {
        ViewPager viewPager = view.findViewById(R.id.vpCards);
        List<RealmMobileBankCards> cards = new ArrayList<>();
        cards.add(new RealmMobileBankCards("6221 6698 2154 4752" , "علیرضا نظری" , "02/99" , true));
        cards.add(new RealmMobileBankCards("6221 6698 3145 3456" , "حسین امینی" , "02/99" , true));
        cards.add(new RealmMobileBankCards("6221 6698 9254 6678" , "احسان زرقلمی" , "02/99" , true));
        viewPager.setAdapter(new BankCardsAdapter(cards));
        viewPager.setOffscreenPageLimit(cards.size() - 1);

    }
}
