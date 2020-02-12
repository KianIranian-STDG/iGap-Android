package net.iGap.viewmodel.kuknos;

import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import net.iGap.repository.kuknos.UserRepo;

public class KuknosRecieveVM extends ViewModel {

    private ObservableField<String> clientKey = new ObservableField<>();
    private UserRepo userRepo = new UserRepo();

    public KuknosRecieveVM() {
        initData();
    }

    private void initData() {
        clientKey.set(userRepo.getAccountID());
    }

    // Setter and Getter

    public ObservableField<String> getClientKey() {
        return clientKey;
    }

}
