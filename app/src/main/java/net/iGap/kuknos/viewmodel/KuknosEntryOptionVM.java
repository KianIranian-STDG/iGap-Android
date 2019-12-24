package net.iGap.kuknos.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import net.iGap.kuknos.service.Repository.UserRepo;

public class KuknosEntryOptionVM extends ViewModel {

    private MutableLiveData<Boolean> goNewTPage;
    private MutableLiveData<Boolean> goRestoreTPage;
    private MutableLiveData<Boolean> goRestoreSeedPage;
    private UserRepo userRepo = new UserRepo();

    public KuknosEntryOptionVM() {
        goNewTPage = new MutableLiveData<>();
        goRestoreTPage = new MutableLiveData<>();
        goRestoreSeedPage = new MutableLiveData<>();
    }

    public boolean loginStatus() {
        if (userRepo.getSeedKey() != null) {
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

    public void setGoNewTPage(MutableLiveData<Boolean> goNewTPage) {
        this.goNewTPage = goNewTPage;
    }

    public MutableLiveData<Boolean> getGoRestoreTPage() {
        return goRestoreTPage;
    }

    public void setGoRestoreTPage(MutableLiveData<Boolean> goRestoreTPage) {
        this.goRestoreTPage = goRestoreTPage;
    }

    public MutableLiveData<Boolean> getGoRestoreSeedPage() {
        return goRestoreSeedPage;
    }

    public void setGoRestoreSeedPage(MutableLiveData<Boolean> goRestoreSeedPage) {
        this.goRestoreSeedPage = goRestoreSeedPage;
    }
}
