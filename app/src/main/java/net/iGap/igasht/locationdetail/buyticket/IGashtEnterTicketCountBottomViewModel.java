package net.iGap.igasht.locationdetail.buyticket;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

public class IGashtEnterTicketCountBottomViewModel extends ViewModel {

    private MutableLiveData<Integer> ticketCount = new MutableLiveData<>();

    public MutableLiveData<Integer> getTicketCount() {
        return ticketCount;
    }

    public void onSubmitTicketCountClick(String ticketCount) {
        Log.wtf(this.getClass().getName(), "count: "+ticketCount);
        this.ticketCount.setValue(Integer.parseInt(ticketCount));
    }
}
