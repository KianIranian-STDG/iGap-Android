package net.iGap.kuknos.viewmodel;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

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
