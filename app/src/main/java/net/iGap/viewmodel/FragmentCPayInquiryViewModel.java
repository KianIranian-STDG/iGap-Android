package net.iGap.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

public class FragmentCPayInquiryViewModel extends ViewModel {

    public ObservableField<String> inquiryResultText = new ObservableField<>(getInquiryResult(0));

    public FragmentCPayInquiryViewModel() {
    }

    public void onInquiryClicked(){

    }

    private String getInquiryResult(long amount){
        String textStart = "کاربر گرامی , بدهی عوارضی پلاک انتخاب شده تا ساعت 24 شب گذشته ";
        String textEnd = " تومان می باشد";
        return textStart + amount + textEnd ;
    }
}
