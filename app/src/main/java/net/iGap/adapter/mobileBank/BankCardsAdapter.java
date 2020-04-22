package net.iGap.adapter.mobileBank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.helper.HelperMobileBank;
import net.iGap.realm.RealmMobileBankCards;

import java.util.List;

public class BankCardsAdapter extends PagerAdapter {

    private List<RealmMobileBankCards> mCards;

    public BankCardsAdapter(List<RealmMobileBankCards> cards) {
        this.mCards = cards;
        //cards.add(null); // for add
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
        TextView tvName, tvNumber, icAdd;
        ImageView ivLogo, ivShetab;
        View lytBlocked;

        tvName = layout.findViewById(R.id.tvName);
        tvNumber = layout.findViewById(R.id.tvNumber);
        ivLogo = layout.findViewById(R.id.ivBankLogo);
        ivShetab = layout.findViewById(R.id.ivBankShetabLogo);
        icAdd = layout.findViewById(R.id.tvAdd);
        lytBlocked = layout.findViewById(R.id.ivBlocked);

        if (mCards.get(position) != null){

            icAdd.setVisibility(View.GONE);
            tvName.setText(mCards.get(position).getCardName());
            tvNumber.setText(HelperMobileBank.getCardNumberPattern(mCards.get(position).getCardNumber()));
            ivLogo.setImageResource(getCardBankLogo(mCards.get(position).getCardNumber()));
            if (mCards.get(position).getStatus() != null && mCards.get(position).getStatus().equals("HOT")) {
                lytBlocked.setVisibility(View.VISIBLE);
                ivShetab.setVisibility(View.GONE);
            } else {
                lytBlocked.setVisibility(View.GONE);
                ivShetab.setVisibility(View.VISIBLE);
            }

        }/*else {

            icAdd.setVisibility(View.VISIBLE);
            ivShetabLogo.setVisibility(View.GONE);
            tvName.setVisibility(View.GONE);
            tvNumber.setVisibility(View.GONE);
            ivLogo.setVisibility(View.GONE);

        }*/

        container.addView(layout);
        return layout;
    }

    private int getCardBankLogo(String cardNumber) {
        return HelperMobileBank.bankLogo(cardNumber);
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
