package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;

import java.util.List;

public class BankCardsAdapter extends PagerAdapter {

    private List<RealmMobileBankCards> mCards;

    public BankCardsAdapter(List<RealmMobileBankCards> cards) {
        this.mCards = cards;
    }

    @Override
    public int getCount() {
        return mCards.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View layout = LayoutInflater.from(container.getContext()).inflate(R.layout.view_bank_card, container, false);
        TextView tvName, tvNumber;
        ImageView ivLogo;

        tvName = layout.findViewById(R.id.tvName);
        tvNumber = layout.findViewById(R.id.tvNumber);
        ivLogo = layout.findViewById(R.id.ivBankLogo);

        tvName.setText(mCards.get(position).getCardName());
        tvNumber.setText(mCards.get(position).getCardNumber());
        ivLogo.setImageResource(getCardBankLogo(mCards.get(position).getCardNumber()));

        container.addView(layout);
        return layout;
    }

    private int getCardBankLogo(String cardNumber) {
        return R.drawable.bank_logo_parsian;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }

    @Override
    public float getPageWidth(int position) {
        return super.getPageWidth(position);
    }

}
