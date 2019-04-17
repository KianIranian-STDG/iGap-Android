package net.iGap.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.databinding.ObservableField;

import net.iGap.fragments.FragmentCustomerClubProfile;

public class CustomerClubViewModel extends ViewModel {
    public ObservableField<String> profileName = new ObservableField<>("سلام دیتابایندینگ :)");
    private FragmentCustomerClubProfile fragmentChannelProfile;

    public CustomerClubViewModel(FragmentCustomerClubProfile fragmentChannelProfile) {
        this.fragmentChannelProfile = fragmentChannelProfile;
    }
}
