package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import net.iGap.helper.HelperLog;
import net.iGap.helper.HelperPreferences;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.TextCheckCell;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.model.PassCode;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class LockSettingFragment extends BaseFragment {
    public static String PASSCODE = "PassCode";
    public static String PATTERN = "Pattern";
    private final int rowCount = 9;
    private int patternLockRow;
    private int passCodeRow;
    private int sectionOneDividerRow;
    private int changePassCodeRow;
    private int patternLineRow;
    private int autoLockRow;
    private int sectionTwoDividerRow;
    private int captureRow;
    private int sectionThreeDividerRow;
    private boolean hasPassCode;
    private boolean hasPattern;
    private boolean tactile;
    private long timeLock;
    private boolean screenShot;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private net.iGap.module.NumberPicker numberPicker;
    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();

    @Override
    public View createView(Context context) {
        hasPassCode = PassCode.getInstance().isPassCode();
        hasPattern = PassCode.getInstance().isPattern();
        tactile = sharedPreferences.getBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        timeLock = sharedPreferences.getLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        screenShot = sharedPreferences.getBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, true);
        determineArrangementRows();
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
        listView.setAdapter(listAdapter = new ListAdapter());
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!((TextCheckCell) view).isChecked());
                if (position == passCodeRow) {
                    hasPassCode = !hasPassCode;
                    if (hasPassCode) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new LockFragment(PASSCODE)).setReplace(true).load();
                    } else {
                        removePassCode();
                        determineArrangementRows();
                        listAdapter.notifyDataSetChanged();
                    }
                } else if (position == patternLockRow) {
                    hasPassCode = !hasPassCode;
                    hasPattern = !hasPattern;
                    if (hasPattern) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new LockFragment(PATTERN)).setReplace(true).load();
                    } else {
                        removePattern();
                        determineArrangementRows();
                        listAdapter.notifyDataSetChanged();
                    }
                } else if (position == patternLineRow) {
                    tactile = !tactile;
                    setTactile();
                } else if (position == captureRow) {
                    screenShot = !screenShot;
                    setCapture();
                }
            } else if (view instanceof TextSettingsCell) {
                if (position == changePassCodeRow) {
                    if (hasPattern) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new LockFragment(PATTERN)).setReplace(true).load();
                    } else {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new LockFragment(PASSCODE)).setReplace(true).load();
                    }
                } else if (position == autoLockRow) {
                    setAutoLock();
                }
            }
        });
        return fragmentView;
    }

    private void setAutoLock() {
        final MaterialDialog dialog = new MaterialDialog
                .Builder(context)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .title(getResources().getString(R.string.auto_lock))
                .customView(R.layout.dialog_auto_lock, true)
                .positiveText(R.string.B_ok)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .negativeText(R.string.B_cancel).build();
        View customView = dialog.getCustomView();
        assert customView != null;
        numberPicker = customView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(4);
        if (timeLock == 0) {
            numberPicker.setValue(0);
        } else if (timeLock == 60) {
            numberPicker.setValue(1);
        } else if (timeLock == 60 * 5) {
            numberPicker.setValue(2);
        } else if (timeLock == 60 * 60) {
            numberPicker.setValue(3);
        } else if (timeLock == 60 * 60 * 5) {
            numberPicker.setValue(4);
        }
        numberPicker.setFormatter(new net.iGap.module.NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                if (value == 0) {
                    return getResources().getString(R.string.Disable);
                } else if (value == 1) {
                    return getResources().getString(R.string.in_1_minutes);
                } else if (value == 2) {
                    return getResources().getString(R.string.in_5_minutes);
                } else if (value == 3) {
                    return getResources().getString(R.string.in_1_hours);
                } else if (value == 4) {
                    return getResources().getString(R.string.in_5_hours);
                }
                return "";
            }
        });
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int value = numberPicker.getValue();
                if (value == 0) {
                    timeLock = 0;
                } else if (value == 1) {
                    timeLock = 60;
                } else if (value == 2) {
                    timeLock = 60 * 5;
                } else if (value == 3) {
                    timeLock = 60 * 60;
                } else if (value == 4) {
                    timeLock = 60 * 60 * 5;
                }
                editor.putLong(SHP_SETTING.KEY_TIME_LOCK, timeLock);
                editor.apply();
                dialog.dismiss();
                listAdapter.notifyItemChanged(autoLockRow);
            }
        });
        dialog.show();
    }

    private void setCapture() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, screenShot);
        editor.apply();
        try {
            if (screenShot) {
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
            } else {
                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            }
        } catch (Exception e) {
            HelperLog.getInstance().setErrorLog(e);
        }
    }

    private void setTactile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, tactile);
        editor.apply();
    }

    private void removePattern() {
        PassCode.getInstance().setPassCode(false);
        PassCode.getInstance().setPattern(false);
        PassCode.getInstance().setPassCode("");
        PassCode.getInstance().setPassCode(false);
        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_PATTERN_TACTILE_DRAWN, true);
        editor.apply();
    }

    private void removePassCode() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SHP_SETTING.KEY_SCREEN_SHOT_LOCK, false);
        editor.putLong(SHP_SETTING.KEY_TIME_LOCK, 0);
        editor.apply();
        PassCode.getInstance().setPassCode(false);
        HelperPreferences.getInstance().putBoolean(SHP_SETTING.FILE_NAME, SHP_SETTING.KEY_LOCK_STARTUP_STATE, false);
        PassCode.getInstance().setPassCode(false);
        PassCode.getInstance().setPattern(false);
        PassCode.getInstance().setPassCode("");
    }

    private void determineArrangementRows() {
        if (hasPattern) {
            patternLockRow = 0;
            patternLineRow = 1;
            changePassCodeRow = 2;
            passCodeRow = 3;
            sectionOneDividerRow = 4;
            autoLockRow = 5;
            sectionTwoDividerRow = 6;
            captureRow = 7;
            sectionThreeDividerRow = 8;
        } else if (hasPassCode) {
            passCodeRow = 0;
            changePassCodeRow = 1;
            sectionOneDividerRow = 2;
            autoLockRow = 3;
            sectionTwoDividerRow = 4;
            captureRow = 5;
            sectionThreeDividerRow = 6;
            patternLockRow = 7;
            patternLineRow = 8;
        } else {
            passCodeRow = 0;
            patternLockRow = 1;
            sectionOneDividerRow = 2;
            patternLineRow = 3;
            changePassCodeRow = 4;
            autoLockRow = 5;
            sectionTwoDividerRow = 6;
            captureRow = 7;
            sectionThreeDividerRow = 8;
        }
    }

    private String getTimeLockString() {
        if (timeLock == 1) {
            return G.fragmentActivity.getResources().getString(R.string.in_1_minutes);
        } else if (timeLock == 2) {
            return G.fragmentActivity.getResources().getString(R.string.in_5_minutes);
        } else if (timeLock == 3) {
            return G.fragmentActivity.getResources().getString(R.string.in_1_hours);
        } else if (timeLock == 4) {
            return G.fragmentActivity.getResources().getString(R.string.in_5_hours);
        } else {
            return G.fragmentActivity.getResources().getString(R.string.Disable);
        }
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.two_step_pass_code));
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
            View view = null;
            switch (viewType) {
                case 0:
                    view = new TextCheckCell(context);
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(context);
                    view.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
                    view.setBackgroundColor(Theme.getColor(Theme.key_line));
                    break;
                case 2:
                    view = new TextSettingsCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            int viewType = holder.getItemViewType();
            boolean hideViewHolder = false;
            switch (viewType) {
                case 0:
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    if (position == passCodeRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.two_step_pass_code), hasPassCode, true);
                        if (hasPattern) {
                            hideViewHolder = true;
                        }
                    } else if (position == patternLockRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.repeat_pattern_pattern), hasPattern, true);
                        if (hasPassCode && !hasPattern) {
                            hideViewHolder = true;
                        }
                    } else if (position == captureRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.allow_screen_capture), screenShot, true);
                        if (!hasPassCode || hasPattern) {
                            hideViewHolder = true;
                        }
                    } else if (position == patternLineRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.show_line_pattern), tactile, true);
                        if (!hasPattern) {
                            hideViewHolder = true;
                        }
                    }
                    if (hideViewHolder) {
                        textCheckCell.setVisibility(View.GONE);
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == sectionOneDividerRow) {
                        textInfoPrivacyCell.setText(getString(R.string.set_pass_code_description));
                        if (hasPattern) {
                            hideViewHolder = true;
                        }
                    } else if (position == sectionTwoDividerRow) {
                        textInfoPrivacyCell.setText(getString(R.string.Auto_lock_description));
                        if (!hasPassCode || hasPattern) {
                            hideViewHolder = true;
                        }
                    } else if (position == sectionThreeDividerRow) {
                        textInfoPrivacyCell.setText(getString(R.string.Allow_Screen_Capture_description));
                        if (!hasPassCode || hasPattern) {
                            hideViewHolder = true;
                        }
                    }
                    if (hideViewHolder) {
                        textInfoPrivacyCell.setVisibility(View.GONE);
                    }
                    break;
                case 2:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == changePassCodeRow) {
                        textCell.setTextAndValue(getString(R.string.change_pass_code), "", true);
                        if (!hasPassCode) {
                            hideViewHolder = true;
                        }
                    } else if (position == autoLockRow) {
                        textCell.setTextAndValue(getString(R.string.auto_lock), getTimeLockString(), true);
                        if (!hasPassCode || hasPattern) {
                            hideViewHolder = true;
                        }
                    }
                    if (hideViewHolder) {
                        textCell.setVisibility(View.GONE);
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == passCodeRow || position == patternLockRow || position == captureRow || position == patternLineRow) {
                return 0;
            } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow) {
                return 1;
            } else if (position == changePassCodeRow || position == autoLockRow) {
                return 2;
            }
            return position;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }
    }
}
