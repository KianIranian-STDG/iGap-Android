package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import net.iGap.R;
import net.iGap.model.igasht.BaseIGashtResponse;
import net.iGap.model.igasht.IGashtLocationService;
import net.iGap.module.SingleLiveEvent;
import net.iGap.repository.IGashtRepository;

import java.util.ArrayList;
import java.util.List;

public class IGashtBuyTicketViewModel extends BaseIGashtViewModel<BaseIGashtResponse<IGashtLocationService>> {

    private ObservableInt totalPrice = new ObservableInt(0);
    private SingleLiveEvent<Boolean> registerVoucher = new SingleLiveEvent<>();
    private MutableLiveData<List<IGashtLocationService>> serviceList = new MutableLiveData<>();
    private SingleLiveEvent<Integer> showErrorMessage = new SingleLiveEvent<>();
    private IGashtRepository repository;

    public IGashtBuyTicketViewModel() {
        repository = IGashtRepository.getInstance();
        getTicketData();
    }

    public ObservableInt getTotalPrice() {
        return totalPrice;
    }


    public void setTotalPrice(int totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public SingleLiveEvent<Boolean> getRegisterVoucher() {
        return registerVoucher;
    }

    public MutableLiveData<List<IGashtLocationService>> getServiceList() {
        return serviceList;
    }

    public SingleLiveEvent<Integer> getShowErrorMessage() {
        return showErrorMessage;
    }

    @Override
    public void onSuccess(BaseIGashtResponse<IGashtLocationService> data) {
        serviceList.setValue(getPersianTicket(data.getData()));
        showLoadingView.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showViewRefresh.set(View.GONE);
    }

    public void onAddPlaceClick() {

    }

    public void onRetryClick() {
        getTicketData();
    }

    public void onPayClick() {
        if (serviceList.getValue() != null) {
            if (checkEntranceTicketCount(serviceList.getValue())) {
                repository.clearSelectedServiceList();
                repository.createVoucherList(serviceList.getValue());
                registerVoucher.setValue(repository.hasVoucher());
            } else {
                showErrorMessage.setValue(R.string.error);
            }
        }
    }

    private boolean checkEntranceTicketCount(List<IGashtLocationService> list) {
        int tmp = findEntranceLocation(list);
        if (tmp != -1) {
            return findMaxTicketCount(list, tmp) <= list.get(tmp).getCount();
        } else {
            return false;
        }
    }

    private int findEntranceLocation(List<IGashtLocationService> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getModelId().equals("1")) {
                return i;
            }
        }
        return -1;
    }

    private int findMaxTicketCount(List<IGashtLocationService> list, int entrancePosition) {
        int t = -1;
        for (int i = 0; i < list.size(); i++) {
            if (i != entrancePosition) {
                if (list.get(i).getCount() > t) {
                    t = list.get(i).getCount();
                }
            }
        }
        return t;
    }

    private List<IGashtLocationService> getPersianTicket(List<IGashtLocationService> data) {
        List<IGashtLocationService> tmp = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            for (int j = 0; j < data.get(i).getAmounts().size(); j++) {
                if (data.get(i).getAmounts().get(j).getVoucherTypeId() == 3) {
                    tmp.add(data.get(i));
                }
            }
        }
        return tmp;
    }

    private void getTicketData() {
        showLoadingView.set(View.VISIBLE);
        showMainView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        repository.getServiceList(this, this);
    }
}
