package net.iGap.adapter.mobileBank;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.iGap.R;
import net.iGap.helper.HelperMobileBank;
import net.iGap.realm.RealmMobileBankAccounts;

import java.util.List;

import static net.iGap.libs.bottomNavigation.Util.Utils.setTextSize;

public class BankAccountsAdapter extends PagerAdapter {

    private List<RealmMobileBankAccounts> mAccounts;

    public BankAccountsAdapter(List<RealmMobileBankAccounts> accounts) {
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

        View layout = LayoutInflater.from(container.getContext()).inflate(R.layout.view_bank_account, container, false);
        TextView tvName, tvNumber, icAdd;
        ConstraintLayout lytRoot;
        View lytBlocked;

        tvName = layout.findViewById(R.id.tvName);
        tvNumber = layout.findViewById(R.id.tvNumber);
        icAdd = layout.findViewById(R.id.tvAdd);
        lytRoot = layout.findViewById(R.id.lytRoot);
        lytBlocked = layout.findViewById(R.id.ivBlocked);

        if (mAccounts.get(position) != null) {

            icAdd.setVisibility(View.GONE);
            lytRoot.setBackgroundResource(R.drawable.shape_card_background_gray);
            setTextSize(tvName, R.dimen.smallTextSize);
            tvName.setText(mAccounts.get(position).getAccountName());
            tvNumber.setText(HelperMobileBank.checkNumbersInMultiLangs(mAccounts.get(position).getAccountNumber()));

            if (mAccounts.get(position).getStatus() != null && !mAccounts.get(position).getStatus().equals("OPEN") && !mAccounts.get(position).getStatus().equals("OPENING")) {
                lytBlocked.setVisibility(View.VISIBLE);
            } else {
                lytBlocked.setVisibility(View.GONE);
            }
        } /*else {

            icAdd.setVisibility(View.VISIBLE);
            lytRoot.setBackgroundResource(R.drawable.shape_gray_round_stroke_dash);
            tvName.setVisibility(View.GONE);
            tvNumber.setVisibility(View.GONE);

        }*/

        container.addView(layout);
        return layout;
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
