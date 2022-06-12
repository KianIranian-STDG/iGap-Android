package net.iGap.adapter.items.cells;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.helper.avatar.AvatarHandler;
import net.iGap.helper.avatar.ParamWithAvatarType;
import net.iGap.messenger.theme.Theme;

import de.hdodenhof.circleimageview.CircleImageView;

public class MemberCell extends FrameLayout {
    private TextView memberNameView;
    private TextView memberStatusView;
    private CircleImageView avatarImage;

    private boolean isRtl = G.isAppRtl;

    public MemberCell(@NonNull Context context) {
        super(context);

        avatarImage = new CircleImageView(getContext());
        addView(avatarImage, LayoutCreator.createFrame(55, 55, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 16, 8, isRtl ? 16 : 0, 8));

        memberNameView = new TextView(getContext());
        memberNameView.setTextColor(Theme.getColor(Theme.key_title_text));
        memberNameView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        memberNameView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        memberNameView.setLines(1);
        memberNameView.setMaxLines(1);
        memberNameView.setSingleLine(true);
        memberNameView.setEllipsize(TextUtils.TruncateAt.END);
        memberNameView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        addView(memberNameView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 82, 12, isRtl ? 82 : 0, 0));

        memberStatusView = new TextView(getContext());
        memberStatusView.setTextColor(Theme.getColor(Theme.key_subtitle_text));
        memberStatusView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        memberStatusView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        memberStatusView.setLines(1);
        memberStatusView.setMaxLines(1);
        memberStatusView.setSingleLine(true);
        memberStatusView.setEllipsize(TextUtils.TruncateAt.END);
        memberStatusView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        addView(memberStatusView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, isRtl ? Gravity.RIGHT : Gravity.LEFT, isRtl ? 0 : 82, 38, isRtl ? 82 : 0, 0));
    }

    public void setInfo(AvatarHandler avatarHandler, long userId, CharSequence name, String status) {
        loadInfo(avatarHandler, userId, name, status);
    }

    private void loadInfo(AvatarHandler avatarHandler, long userId, CharSequence name, String status) {
        avatarHandler.getAvatar(new ParamWithAvatarType(avatarImage, userId).avatarType(AvatarHandler.AvatarType.USER).showMain(), true);
        memberNameView.setText(name);
        memberStatusView.setText(status);
    }
}
