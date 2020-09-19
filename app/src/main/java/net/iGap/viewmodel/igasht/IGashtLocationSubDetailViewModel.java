package net.iGap.viewmodel.igasht;

import android.view.View;

import androidx.databinding.ObservableInt;

import net.iGap.model.igasht.ExtraDetail;
import net.iGap.repository.IGashtRepository;

public class IGashtLocationSubDetailViewModel extends BaseIGashtViewModel {
    private IGashtRepository repository;
    private ObservableInt noDetail = new ObservableInt(View.GONE);
    private ObservableInt galleryShow = new ObservableInt(View.GONE);

    public IGashtLocationSubDetailViewModel() {
        repository = IGashtRepository.getInstance();
        if (getLocationDetail() != null) {
            if (getLocationDetail().getmGallery() != null) {
                galleryShow.set(View.VISIBLE);
            } else {
                galleryShow.set(View.GONE);
            }
            showMainView.set(View.VISIBLE);
            noDetail.set(View.GONE);
        } else {
            showMainView.set(View.GONE);
            noDetail.set(View.VISIBLE);
        }
    }

    public ObservableInt getShowText() {
        return noDetail;
    }

    public ExtraDetail getLocationDetail() {
        return repository.getSelectedLocation().getmExtraDetail();
    }

    @Override
    public void onSuccess(Object data) {

    }

    public ObservableInt getGalleryShow() {
        return galleryShow;
    }
}
