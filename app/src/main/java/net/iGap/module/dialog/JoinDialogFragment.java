package net.iGap.module.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;
import net.iGap.proto.ProtoGlobal;

public class JoinDialogFragment extends BaseBottomSheet {

    private JoinDialogListener mListener;
    private ProtoGlobal.Room mRoom;

    public JoinDialogFragment setData(ProtoGlobal.Room room, JoinDialogListener listener) {
        this.mListener = listener;
        this.mRoom = room;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_alert_join, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupView(view);
    }

    private void setupView(View view) {
        CircleImageView ivAvatar = view.findViewById(R.id.ivAvatar);
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setTextColor(Theme.getColor(Theme.key_title_text));
        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setTextColor(Theme.getColor(Theme.key_title_text));
        TextView tvMemberCount = view.findViewById(R.id.tvMemberCount);
        tvMemberCount.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        TextView btnCancel = view.findViewById(R.id.btnCancel);
        btnCancel.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        TextView btnJoin = view.findViewById(R.id.btnJoin);
        btnJoin.setTextColor(Theme.getColor(Theme.key_icon));
        View lineViewTop = view.findViewById(R.id.lineViewTop);
        lineViewTop.setBackground(Theme.tintDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bottom_sheet_dialog_line), getContext(), Theme.getColor(Theme.key_theme_color)));
        String title = getString(R.string.do_you_want_to_join_to_this);
        String memberNumber = "";

        switch (mRoom.getType()) {
            case CHANNEL:
                if (HelperCalander.isPersianUnicode) {
                    title += getString(R.string.channel) + " " + "عضو شوید؟";
                } else {
                    title += getString(R.string.channel) + "?";
                }
                memberNumber = mRoom.getChannelRoomExtra().getParticipantsCount() + " " + G.context.getString(R.string.member_chat);
                break;
            case GROUP:
                if (HelperCalander.isPersianUnicode) {
                    title += getString(R.string.group) + " " + "عضو شوید؟";
                } else {
                    title += getString(R.string.group) + "?";
                }
                memberNumber = mRoom.getGroupRoomExtra().getParticipantsCount() + " " + getString(R.string.member_chat);
                break;
        }

        tvTitle.setText(title);
        tvName.setText(mRoom.getTitle());
        tvMemberCount.setText(memberNumber);

        if (getActivity() != null) {
            ((ActivityEnhanced) getActivity()).avatarHandler.getAvatar(new ParamWithAvatarType(ivAvatar, mRoom.getId()).avatarType(AvatarHandler.AvatarType.ROOM));
        }

        btnCancel.setOnClickListener(v -> {
            mListener.onCancelClicked();
            dismiss();
        });
        btnJoin.setOnClickListener(v -> {
            mListener.onJoinClicked();
            dismiss();
        });
    }

    @Override
    public int getTheme() {
        return R.style.BaseBottomSheetDialog;
    }

    public interface JoinDialogListener {
        void onJoinClicked();

        void onCancelClicked();
    }
}
