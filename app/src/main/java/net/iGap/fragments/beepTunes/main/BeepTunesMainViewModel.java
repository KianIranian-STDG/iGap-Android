package net.iGap.fragments.beepTunes.main;

import androidx.lifecycle.MutableLiveData;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.RetrofitFactory;
import net.iGap.fragments.BaseFragment;
import net.iGap.module.api.beepTunes.FirstPage;
import net.iGap.viewmodel.BaseViewModel;

public class BeepTunesMainViewModel extends BaseViewModel {
    private static final String TAG = "aabolfazlBeepTunes";

    private BeepTunesApi apiService = new RetrofitFactory().getBeepTunesRetrofit();

    private MutableLiveData<FirstPage> firstPageMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> progressMutableLiveData = new MutableLiveData<>();

    @Override
    public void onStartFragment(BaseFragment fragment) {
        getFirsPage();
    }

    private void getFirsPage() {
        /*new ApiInitializer<FirstPage>().initAPI(apiService.getFirstPage(), this, new ResponseCallback<FirstPage>() {
            @Override
            public void onSuccess(FirstPage data) {
                firstPageMutableLiveData.postValue(data);
            }

            @Override
            public void onError(ErrorModel error) {

            }

            @Override
            public void setProgressIndicator(boolean visibility) {
                progressMutableLiveData.postValue(visibility ? 0 : 1);
            }
        });*/
    }

    public MutableLiveData<Integer> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<FirstPage> getFirstPageMutableLiveData() {
        return firstPageMutableLiveData;
    }
}
