package net.iGap.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.iGap.R;
import net.iGap.adapter.items.cells.EmptyCell;
import net.iGap.adapter.items.cells.MemberCell;
import net.iGap.adapter.items.cells.TextCell;
import net.iGap.adapter.items.cells.ToggleButtonCell;
import net.iGap.helper.HelperToolbar;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.observers.interfaces.ToolbarListener;

public class ChatRightsEditFragment extends BaseFragment implements ToolbarListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (getContext() == null)
            return super.onCreateView(inflater, container, savedInstanceState);

        HelperToolbar helperToolbar = HelperToolbar.create();

        View toolBar = helperToolbar
                .setContext(getContext())
                .setLogoShown(true)
                .setListener(this)
                .setRightIcons(R.string.check_icon)
                .setLeftIcon(R.string.back_icon)
                .setDefaultTitle(getString(R.string.new_channel))
                .setDefaultTitle("Chat Rights Edit")
                .getView();

        FrameLayout rootView = new FrameLayout(getContext());
        rootView.setBackgroundColor(Theme.getInstance().getRootColor(getContext()));
        rootView.addView(toolBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP));

        ScrollView scrollView;
        rootView.addView(scrollView = new ScrollView(getContext()), LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.TOP, 0, LayoutCreator.getDimen(R.dimen.toolbar_height), 0, 0));

        LinearLayout linearLayout;
        scrollView.addView(linearLayout = new LinearLayout(getContext()), LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(0, 0, 0, LayoutCreator.dp(100));

        MemberCell memberCell = new MemberCell(getContext());
        linearLayout.addView(memberCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));

        EmptyCell emptyCell = new EmptyCell(getContext(), 12);
        emptyCell.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        linearLayout.addView(emptyCell);

        ToggleButtonCell modifyRoomCell = new ToggleButtonCell(getContext(), true);
        modifyRoomCell.setText("Modify room");
        linearLayout.addView(modifyRoomCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell postMessageCell = new ToggleButtonCell(getContext(), true);
        postMessageCell.setText("Post message");
        linearLayout.addView(postMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell editMessageCell = new ToggleButtonCell(getContext(), true);
        editMessageCell.setText("Edit message");
        linearLayout.addView(editMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell deleteMessageCell = new ToggleButtonCell(getContext(), true);
        deleteMessageCell.setText("Delete message");
        linearLayout.addView(deleteMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell pinMessageCell = new ToggleButtonCell(getContext(), true);
        pinMessageCell.setText("Pin message");
        linearLayout.addView(pinMessageCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell addNewMemberCell = new ToggleButtonCell(getContext(), true);
        addNewMemberCell.setText("Add members");
        linearLayout.addView(addNewMemberCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell banMemberCell = new ToggleButtonCell(getContext(), true);
        banMemberCell.setText("Ban members");
        linearLayout.addView(banMemberCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell showMemberListCell = new ToggleButtonCell(getContext(), true);
        showMemberListCell.setText("Show members");
        linearLayout.addView(showMemberListCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        ToggleButtonCell addNewAdminCell = new ToggleButtonCell(getContext(), false);
        addNewAdminCell.setText("Add admin");
        linearLayout.addView(addNewAdminCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        EmptyCell emptyCell2 = new EmptyCell(getContext(), 12);
        emptyCell2.setBackgroundColor(Theme.getInstance().getDividerColor(getContext()));
        linearLayout.addView(emptyCell2);

        TextCell dismissAdminCell = new TextCell(getContext(), true);
        dismissAdminCell.setText("Dismiss admin");
        dismissAdminCell.setTextColor(getContext().getResources().getColor(R.color.red));
        linearLayout.addView(dismissAdminCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 52));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {


    }

    @Override
    public void onLeftIconClickListener(View view) {

    }

    @Override
    public void onRightIconClickListener(View view) {

    }
}
