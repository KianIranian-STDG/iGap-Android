package net.iGap.kuknos.viewmodel;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class KuknosEntryOptionVM extends ViewModel {

    private MutableLiveData<Boolean> goNewTPage;
    private MutableLiveData<Boolean> goRestoreTPage;

    public KuknosEntryOptionVM() {
        if (goNewTPage == null) {
            goNewTPage = new MutableLiveData<Boolean>();
            goNewTPage.setValue(false);
        }
        if (goRestoreTPage == null) {
            goRestoreTPage = new MutableLiveData<Boolean>();
            goRestoreTPage.setValue(false);
        }
    }

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

    public void onNewTokenClick() {
        goNewTPage.setValue(true);
    }

    public void onRestoreTokenClick() {
        goRestoreTPage.setValue(true);
    }

}
