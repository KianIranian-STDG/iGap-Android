package net.iGap.payment;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class PaymentViewModelFactory implements ViewModelProvider.Factory {

    private String token;

    public PaymentViewModelFactory(String token){
        this.token = token;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new PaymentViewModel(token);
    }
}
