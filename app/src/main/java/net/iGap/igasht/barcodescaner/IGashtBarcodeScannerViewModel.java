package net.iGap.igasht.barcodescaner;

import android.arch.lifecycle.MutableLiveData;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import net.iGap.igasht.BaseIGashtViewModel;
import net.iGap.igasht.IGashtRepository;

public class IGashtBarcodeScannerViewModel extends BaseIGashtViewModel<String> {

    private IGashtRepository repository;
    private MutableLiveData<Bitmap> showQRCodeImage = new MutableLiveData<>();

    public IGashtBarcodeScannerViewModel(String voucherNumber){
        repository = IGashtRepository.getInstance();
        repository.getTicketQRCode(voucherNumber,this);
    }

    public MutableLiveData<Bitmap> getShowQRCodeImage() {
        return showQRCodeImage;
    }

    @Override
    public void onSuccess(String data) {
        byte[] decodedString = Base64.decode(data, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        showLoadingView.set(View.GONE);
        showViewRefresh.set(View.GONE);
        showMainView.set(View.VISIBLE);
        showQRCodeImage.setValue(decodedByte);
    }
}
