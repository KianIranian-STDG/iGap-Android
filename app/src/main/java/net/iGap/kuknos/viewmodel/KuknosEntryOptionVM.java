package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.apiService.BaseAPIViewModel;
import net.iGap.module.SingleLiveEvent;
import net.iGap.kuknos.Repository.UserRepo;

public class KuknosEntryOptionVM extends BaseAPIViewModel {

    private SingleLiveEvent<Boolean> goNewTPage;
    private SingleLiveEvent<Boolean> goRestoreTPage;
    private SingleLiveEvent<Boolean> goRestoreSeedPage;
    private UserRepo userRepo = new UserRepo();

    public KuknosEntryOptionVM() {
        goNewTPage = new SingleLiveEvent<>();
        goRestoreTPage = new SingleLiveEvent<>();
        goRestoreSeedPage = new SingleLiveEvent<>();
    }

    public boolean loginStatus() {
        if (userRepo.getSeedKey() != null && userRepo.getPIN() != null) {
            return !userRepo.getSeedKey().equals("-1");
        }
        return false;
    }

    public void onNewTokenClick() {
        goNewTPage.setValue(true);
    }

    public void onRestoreTokenClick() {
        goRestoreTPage.setValue(true);
    }

    public void onRestoreSeedClick() {
        goRestoreSeedPage.setValue(true);
    }

    // Setter and Getter

    public MutableLiveData<Boolean> getGoNewTPage() {
        return goNewTPage;
    }

    public MutableLiveData<Boolean> getGoRestoreTPage() {
        return goRestoreTPage;
    }

    public MutableLiveData<Boolean> getGoRestoreSeedPage() {
        return goRestoreSeedPage;
    }

}
