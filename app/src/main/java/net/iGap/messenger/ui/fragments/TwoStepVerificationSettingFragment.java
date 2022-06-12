package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.observers.interfaces.UserTwoStepVerificationUnsetPasswordCallback;
import net.iGap.request.RequestUserTwoStepVerificationUnsetPassword;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.messenger.ui.fragments.TwoStepVerificationStepsFragment.TYPE_ENTER_FIRST;

public class TwoStepVerificationSettingFragment extends BaseFragment {

    private final int changePasswordRow = 0;
    private final int turnPasswordOffRow = 1;
    private final int passwordEnabledDetailRow = 2;
    private final int rowCount = 3;
    private String password;
    private RecyclerListView listView;

    public TwoStepVerificationSettingFragment(String password) {
        this.password = password;
    }

    @Override
    public View createView(Context context) {
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        listView = new RecyclerListView(context);
        listView.setItemAnimator(null);
        listView.setLayoutAnimation(null);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        listView.setAdapter(new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof TextSettingsCell) {
                if (position == changePasswordRow) {
                    TwoStepVerificationStepsFragment twoStepVerificationStepsFragment = new TwoStepVerificationStepsFragment(TYPE_ENTER_FIRST);
                    Bundle bundle = new Bundle();
                    bundle.putString("OLD_PASSWORD", password);
                    twoStepVerificationStepsFragment.setArguments(bundle);
                    new HelperFragment(getActivity().getSupportFragmentManager(), twoStepVerificationStepsFragment).setReplace(true).load();
                } else if (position == turnPasswordOffRow) {
                    turnPasswordOff();
                }
            }
        });
        return fragmentView;
    }

    private void turnPasswordOff() {
        new MaterialDialog.Builder(G.fragmentActivity)
                .title(R.string.turn_Password_off)
                .content(R.string.turn_Password_off_desc)
                .positiveText(G.fragmentActivity.getResources().getString(R.string.yes))
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        new RequestUserTwoStepVerificationUnsetPassword().unsetPassword(password, new UserTwoStepVerificationUnsetPasswordCallback() {
                            @Override
                            public void unSetPassword() {
                                G.handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            }

                            @Override
                            public void onUnsetError(int major, int minor) {
                            }
                        });
                    }
                }).negativeText(G.fragmentActivity.getResources().getString(R.string.no))
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.two_step_verification_title));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_CELLBACKGROUNDCOLOR, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_window_background));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type != 1;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(context);
                    break;
                case 1:
                default:
                    view = new TextInfoPrivacyCell(context);
                    view.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
                    view.setBackgroundColor(Theme.getColor(Theme.key_line));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            switch (viewType) {
                case 0:
                    TextSettingsCell textSettingsCell = (TextSettingsCell) holder.itemView;
                    textSettingsCell.setTag(Theme.key_default_text);
                    textSettingsCell.setTextColor(Theme.getColor(Theme.key_default_text));
                    if (position == changePasswordRow) {
                        textSettingsCell.setText(getString(R.string.Change_password), true);
                    } else if (position == turnPasswordOffRow) {
                        textSettingsCell.setText(getString(R.string.turn_Password_off), true);
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == passwordEnabledDetailRow) {
                        privacyCell.setText(getString(R.string.EnabledPasswordText));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == changePasswordRow || position == turnPasswordOffRow) {
                return 0;
            } else if (position == passwordEnabledDetailRow) {
                return 1;
            }
            return position;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }
}
