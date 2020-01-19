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
import net.iGap.mobileBank.repository.model.BankAccountModel;
import net.iGap.mobileBank.repository.util.ExtractBank;

import java.util.List;

import static net.iGap.libs.bottomNavigation.Util.Utils.setTextSize;

public class BankAccountsAdapter extends PagerAdapter {

    private List<BankAccountModel> mAccounts;

    public BankAccountsAdapter(List<BankAccountModel> accounts) {
        this.mAccounts = accounts;
        //cards.add(null); // for add
    }

    @Override
    public int getCount() {
        return mAccounts.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        View layout = LayoutInflater.from(container.getContext()).inflate(R.layout.view_bank_card, container, false);
        TextView tvName, tvNumber , icAdd;
        ImageView ivLogo;
        ConstraintLayout lytRoot ;

        tvName = layout.findViewById(R.id.tvName);
        tvNumber = layout.findViewById(R.id.tvNumber);
        ivLogo = layout.findViewById(R.id.ivBankLogo);
        icAdd = layout.findViewById(R.id.tvAdd);
        lytRoot = layout.findViewById(R.id.lytRoot);

        if (mAccounts.get(position) != null){

            icAdd.setVisibility(View.GONE);
            lytRoot.setBackgroundResource(R.drawable.shape_card_background);
            setTextSize(tvName , R.dimen.smallTextSize);
            tvName.setText(mAccounts.get(position).getTitle());
            tvNumber.setText(checkAndSetPersianNumberIfNeeded(mAccounts.get(position).getAccountNumber()));
            ivLogo.setImageResource(R.drawable.bank_logo_parsian);

        }else {

            icAdd.setVisibility(View.VISIBLE);
            lytRoot.setBackgroundResource(R.drawable.shape_gray_round_stroke_dash);
            tvName.setVisibility(View.GONE);
            tvNumber.setVisibility(View.GONE);
            ivLogo.setVisibility(View.GONE);

        }

        container.addView(layout);
        return layout;
    }

    private String checkAndSetPersianNumberIfNeeded(String cardNumber) {
        String number = cardNumber;
        if (HelperCalander.isPersianUnicode) number = HelperCalander.convertToUnicodeFarsiNumber(cardNumber);
        return number;
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
