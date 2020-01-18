package net.iGap.mobileBank.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.R;
import net.iGap.Theme;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.bottomNavigation.Util.Utils;
import net.iGap.mobileBank.view.adapter.TransferMoneyTypeAdapter;

public class DialogParsian {

    private Dialog mDialog;
    private Context mContext;
    private String mTitle;
    private String mActiveButtonText, mDeActiveButtonText;
    private FrameLayout mContentLayout;
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
        mDialog.setContentView(R.layout.view_bank_parsian_dialog);
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
        mDeActiveButton.setOnClickListener(v -> {
            mDialog.dismiss();
            mListener.onDeActiveButtonClicked(mDialog);
        });

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
        tvMessage.setText(message);

        mActiveButton.setOnClickListener(v -> {
            mDialog.dismiss();
            mListener.onActiveButtonClicked(mDialog);
        });

        mContentLayout.addView(tvMessage, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER, 12f, 12f, 12f, 12f));
        mDialog.show();
    }

    public void showLoaderDialog(boolean cancelable) {

        initDialog();
        ProgressBar progressBar = new ProgressBar(mContext);
        mContentLayout.addView(progressBar, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 12f, 12f, 12f, 12f));
        mDialog.setCancelable(cancelable);
        mDialog.show();
    }

    public void showMoneyTransferDialog() {
        initDialog();

        RecyclerView rvTransferTypes = new RecyclerView(mContext);
        mContentLayout.addView(rvTransferTypes, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT));
        rvTransferTypes.setNestedScrollingEnabled(false);
        rvTransferTypes.setLayoutManager(new LinearLayoutManager(mContext));
        TransferMoneyTypeAdapter adapter = new TransferMoneyTypeAdapter();
        rvTransferTypes.setAdapter(adapter);
        adapter.setTypes();
        rvTransferTypes.hasFixedSize();

        mActiveButton.setOnClickListener(v -> {
            mListener.onTransferMoneyTypeSelected(mDialog, adapter.mSelectedType);
        });

        mDialog.show();
    }

    public void dismiss(){
        if (mDialog != null) mDialog.dismiss();
    }

    private View inflate(int layout) {
        return LayoutInflater.from(mContext).inflate(layout, mContentLayout, false);
    }

    public interface ParsianDialogListener {

        default void onActiveButtonClicked(Dialog dialog) {
        }

        default void onDeActiveButtonClicked(Dialog dialog) {
        }

        default void onTransferMoneyTypeSelected(Dialog dialog, String type) {
        }
    }
}
