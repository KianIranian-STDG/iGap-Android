package net.iGap.mobileBank.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.mobileBank.repository.db.RealmMobileBankCards;
import net.iGap.mobileBank.repository.util.ExtractBank;
import net.iGap.mobileBank.repository.util.JalaliCalendar;

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
        TextView tvName, tvNumber, tvExpire, tvCVV2, icAdd;
        ImageView ivLogo, ivShetabLogo;
        ConstraintLayout lytRoot ;

        tvName = layout.findViewById(R.id.tvName);
        tvNumber = layout.findViewById(R.id.tvNumber);
        tvExpire = layout.findViewById(R.id.tvExpireDate);
        tvCVV2 = layout.findViewById(R.id.tvCVV2);
        ivLogo = layout.findViewById(R.id.ivBankLogo);
        ivShetabLogo = layout.findViewById(R.id.ivBankShetabLogo);
        icAdd = layout.findViewById(R.id.tvAdd);
        lytRoot = layout.findViewById(R.id.lytRoot);

        if (mCards.get(position) != null){

            icAdd.setVisibility(View.GONE);
            lytRoot.setBackgroundResource(R.drawable.shape_card_background);
            tvName.setText(mCards.get(position).getCardName());
            tvNumber.setText(checkAndSetPersianNumberIfNeeded(mCards.get(position).getCardNumber()));
            tvExpire.setText(tvExpire.getContext().getString(R.string.mobile_bank_balance_expireDate2) + " " + getCovertDateForExpire(mCards.get(position).getExpireDate()));
            //tvCVV2.setText("");
            ivLogo.setImageResource(getCardBankLogo(mCards.get(position).getCardNumber()));

        }else {

            icAdd.setVisibility(View.VISIBLE);
            ivShetabLogo.setVisibility(View.GONE);
            lytRoot.setBackgroundResource(R.drawable.shape_gray_round_stroke_dash);
            tvName.setVisibility(View.GONE);
            tvNumber.setVisibility(View.GONE);
            tvExpire.setVisibility(View.GONE);
            tvCVV2.setVisibility(View.GONE);
            ivLogo.setVisibility(View.GONE);

        }

        container.addView(layout);
        return layout;
    }

    private String getCovertDateForExpire(String expireDate) {
        try {
            String[] dateTime = expireDate.split(" ");
            String[] date = dateTime[0].split("-");
            return HelperCalander.getPersianYearMonth(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2]));
        } catch (Exception e) {
            return "-";
        }
    }

    private String checkAndSetPersianNumberIfNeeded(String cardNumber) {
        String number = cardNumber;
        if (HelperCalander.isPersianUnicode) number = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
        try {
            String[] tempArray = Iterables.toArray(Splitter.fixedLength(4).split(number), String.class);
            return tempArray[0] + " - " + tempArray[1] + " - " + tempArray[2] + " - " + tempArray[3];
        }catch (Exception e){
            return number;
        }
    }

    private int getCardBankLogo(String cardNumber) {
        return ExtractBank.bankLogo(cardNumber);
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
