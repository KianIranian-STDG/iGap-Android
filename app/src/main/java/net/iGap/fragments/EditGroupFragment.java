package net.iGap.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iGap.R;

import org.jetbrains.annotations.NotNull;

public class EditGroupFragment extends BaseFragment {

    private static final String ROOM_ID = "RoomId";

    public static EditGroupFragment newInstance(long roomId) {
        Bundle args = new Bundle();
        args.putLong(ROOM_ID, roomId);
        EditGroupFragment fragment = new EditGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return attachToSwipeBack(inflater.inflate(R.layout.fragment_edit_group, container, false));
    }
}
