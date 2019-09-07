package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import net.iGap.kuknos.service.Repository.UserRepo;

public class KuknosRecieveVM extends ViewModel {

    private ObservableField<String> clientKey = new ObservableField<>();
    private UserRepo userRepo = new UserRepo();

    public KuknosRecieveVM() {
        initData();
    }

    public void initData() {
        clientKey.set(userRepo.getAccountID());
    }

    // Setter and Getter

    public ObservableField<String> getClientKey() {
        return clientKey;
    }

    public void setClientKey(ObservableField<String> clientKey) {
        this.clientKey = clientKey;
    }
}
