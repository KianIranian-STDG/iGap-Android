package net.iGap.fragments.beepTunes.main;

import androidx.lifecycle.MutableLiveData;
import android.util.Log;
import android.view.View;

import net.iGap.api.BeepTunesApi;
import net.iGap.api.apiService.ApiServiceProvider;
import net.iGap.fragments.BaseFragment;
import net.iGap.module.api.beepTunes.FirstPage;
import net.iGap.viewmodel.BaseViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BeepTunesMainViewModel extends BaseViewModel {
    private static final String TAG = "aabolfazlBeepTunes";

    private BeepTunesApi apiService = ApiServiceProvider.getBeepTunesClient();

    private MutableLiveData<FirstPage> firstPageMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> progressMutableLiveData = new MutableLiveData<>();

    @Override
    public void onStartFragment(BaseFragment fragment) {
        getFirsPage();
    }

    private void getFirsPage() {
        progressMutableLiveData.postValue(View.VISIBLE);
        apiService.getFirstPage().enqueue(new Callback<FirstPage>() {
            @Override
            public void onResponse(Call<FirstPage> call, Response<FirstPage> response) {
                if (response.isSuccessful()) {
                    firstPageMutableLiveData.postValue(response.body());
                } else {
                    // TODO: 7/15/19 exception message
                }
                progressMutableLiveData.postValue(View.GONE);
            }

            @Override
            public void onFailure(Call<FirstPage> call, Throwable t) {
                progressMutableLiveData.postValue(View.GONE);
                Log.i(TAG, "get first page: " + t.getMessage());
            }
        });
    }

    public MutableLiveData<Integer> getProgressMutableLiveData() {
        return progressMutableLiveData;
    }

    public MutableLiveData<FirstPage> getFirstPageMutableLiveData() {
        return firstPageMutableLiveData;
    }
}
