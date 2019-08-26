package net.iGap.viewmodel;

import android.databinding.ObservableField;

import net.iGap.G;
import net.iGap.model.cPay.CPayUserWalletModel;

public class FragmentCPayChargeViewModel extends BaseCPayViewModel<CPayUserWalletModel> {

    public ObservableField<Boolean> isDark = new ObservableField<>(false);

    public FragmentCPayChargeViewModel() {
        isDark.set(G.isDarkTheme);
    }

    @Override
    public void onSuccess(CPayUserWalletModel data) {

    }
}
