package net.iGap.mobileBank.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.libs.bottomNavigation.Util.Utils;

public class DialogParsian {

    private Dialog mDialog;
    private Context mContext;
    private String mTitle;
    private String mActiveButtonText, mDeActiveButtonText;
    private LinearLayout mContentLayout;
    private Button mActiveButton, mDeActiveButton;
    private TextView mTitleTextView, mCloseButton;
    private ParsianDialogListener mListener;

    public DialogParsian setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public DialogParsian setTitle(String title) {
        this.mTitle = title;
        return this;
    }

    public DialogParsian setButtonsText(String active, String deActive) {
        this.mActiveButtonText = active;
        this.mDeActiveButtonText = deActive;
        return this;
    }

    public DialogParsian setListener(ParsianDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    private void initDialog() {

        mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.view_base_parsian_dialog);
        if (mDialog.getWindow() != null)
            mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        mCloseButton = mDialog.findViewById(R.id.tvClose);
        mTitleTextView = mDialog.findViewById(R.id.tvTitle);
        mContentLayout = mDialog.findViewById(R.id.lytContent);
        mActiveButton = mDialog.findViewById(R.id.btnActive);
        mDeActiveButton = mDialog.findViewById(R.id.btnDeActive);

        mTitleTextView.setVisibility(mTitle == null ? View.GONE : View.VISIBLE);
        mActiveButton.setVisibility(mActiveButtonText == null ? View.GONE : View.VISIBLE);
        mDeActiveButton.setVisibility(mDeActiveButtonText == null ? View.GONE : View.VISIBLE);

        mCloseButton.setOnClickListener(v -> mDialog.dismiss());
        mActiveButton.setOnClickListener(v -> mListener.onActiveButtonClicked());
        mDeActiveButton.setOnClickListener(v -> mListener.onDeActiveButtonClicked());

        if (mTitle != null) mTitleTextView.setText(mTitle);
        if (mActiveButtonText != null) mActiveButton.setText(mActiveButtonText);
        if (mDeActiveButtonText != null) mDeActiveButton.setText(mDeActiveButtonText);
    }

    public void showSimpleMessage(String message) {

        initDialog();

        TextView tvMessage = new TextView(mContext);
        Utils.setTextSize(tvMessage, R.dimen.standardTextSize);
        tvMessage.setTextColor(new Theme().getTitleTextColor(mContext));
        tvMessage.setTypeface(ResourcesCompat.getFont(mContext, R.font.main_font));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(12, 12, 12, 12);
        tvMessage.setLayoutParams(lp);
        tvMessage.setText(message);

        mContentLayout.addView(tvMessage);
        mDialog.show();
    }

    private View inflate(int layout) {
        return LayoutInflater.from(mContext).inflate(layout, mContentLayout, false);
    }

    public interface ParsianDialogListener {

        void onActiveButtonClicked();

        void onDeActiveButtonClicked();
    }
}
