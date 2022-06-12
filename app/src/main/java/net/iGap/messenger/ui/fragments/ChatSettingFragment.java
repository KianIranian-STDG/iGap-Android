package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.NotificationCenter;
import net.iGap.messenger.SharedConfig;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.cell.TextCell;
import net.iGap.messenger.ui.cell.TextCheckCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.cell.TextSizeCell;
import net.iGap.messenger.ui.cell.ThemesHorizontalListCell;
import net.iGap.messenger.ui.components.SeekBarView;
import net.iGap.messenger.ui.components.TintRecyclerListView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.SHP_SETTING;
import net.iGap.module.StartupActions;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.List;

public class ChatSettingFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {

    private final int sectionOneHeaderRow = 0;
    private final int messageTextSizeRow = 1;
    private final int backgroundRow = 2;
    private final int sectionOneDividerRow = 3;
    private final int sectionTwoHeaderRow = 4;
    private final int themeListRow = 5;
    private final int themeAccentListRow = 6;
    private final int sectionTwoDividerRow = 7;
    private final int sectionThreeHeaderRow = 8;
    /*private final int systemDarkModeRow = 9;*/
    private final int dateRow = 10;
    private final int wholeTimeRow = 11;
    private final int convertVoiceRow = 12;
    private final int convertTextRow = 13;
    private final int voteRow = 14;
    private final int showSenderNameRow = 15;
    private final int sendByEnterRow = 16;
    private final int soundInChatRow = 17;
    private final int appBrowserRow = 18;
    private final int sectionThreeDividerRow = 19;
    private final int sectionFourHeaderRow = 20;
    private final int compressRow = 21;
    private final int cropRow = 22;
    private final int defaultPlayerRow = 23;
    private final int trimRow = 24;
    private final int cameraButtonSheetRow = 25;
    private final int sectionFourDividerRow = 26;
    private final int rowCount = 27;

    private final int MIN_TEXT_SIZE = 12;
    private final int MAX_TEXT_SIZE = 30;
    private final int progressValue;
    private final SharedPreferences sharedPreferences = getSharedManager().getSettingSharedPreferences();
    private int date;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private ThemesHorizontalListCell themesHorizontalListCell;
    private boolean systemDarkMode;
    private boolean wholeTime;
    private boolean convertVoice;
    private boolean convertText;
    private int vote;
    private int showSenderName;
    private int sendByEnter;
    private int soundInChat;
    private int appBrowser;
    private int compress;
    private int crop;
    private int defaultPlayer;
    private int trim;
    private boolean cameraButtonSheet;
    private final int startFontSize = 12;
    private final int endFontSize = 30;

    public ChatSettingFragment() {
        systemDarkMode = false;/*sharedPreferences.getBoolean(SHP_SETTING.KEY_SYSTEM_DARK_MODE, false)*/
        SharedConfig.fontSize = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14);
        progressValue = sharedPreferences.getInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, 14) - MIN_TEXT_SIZE;
        date = sharedPreferences.getInt(SHP_SETTING.KEY_DATA, 1);
        wholeTime = sharedPreferences.getBoolean(SHP_SETTING.KEY_WHOLE_TIME, false);
        convertVoice = sharedPreferences.getBoolean(SHP_SETTING.KEY_CONVERT_VOICE_MESSAGE, true);
        convertText = sharedPreferences.getBoolean(SHP_SETTING.KEY_CONVERT_TEXT_MESSAGE, true);
        vote = sharedPreferences.getInt(SHP_SETTING.KEY_VOTE, 1);
        showSenderName = sharedPreferences.getInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, 0);
        sendByEnter = sharedPreferences.getInt(SHP_SETTING.KEY_SEND_BT_ENTER, 0);
        soundInChat = sharedPreferences.getInt(SHP_SETTING.KEY_PLAY_SOUND_IN_CHAT, 1);
        appBrowser = sharedPreferences.getInt(SHP_SETTING.KEY_IN_APP_BROWSER, 1);
        compress = sharedPreferences.getInt(SHP_SETTING.KEY_COMPRESS, 1);
        crop = sharedPreferences.getInt(SHP_SETTING.KEY_CROP, 1);
        defaultPlayer = sharedPreferences.getInt(SHP_SETTING.KEY_DEFAULT_PLAYER, 1);
        trim = sharedPreferences.getInt(SHP_SETTING.KEY_TRIM, 1);
        cameraButtonSheet = sharedPreferences.getBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, true);
    }

    @Override
    public View createView(Context context) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        fragmentView = frameLayout;
        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setAdapter(listAdapter = new ListAdapter(context));
        ((DefaultItemAnimator) listView.getItemAnimator()).setDelayAnimations(false);
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        listView.setOnItemClickListener((view, position, x, y) -> {
            if (view instanceof TextCheckCell) {
                ((TextCheckCell) view).setChecked(!((TextCheckCell) view).isChecked());
                if (position == wholeTimeRow) {
                    wholeTime = !wholeTime;
                    sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_WHOLE_TIME, wholeTime).apply();
                    G.isTimeWhole = wholeTime;
                    if (G.onNotifyTime != null) {
                        G.onNotifyTime.notifyTime();
                    }
                } else if (position == convertVoiceRow) {
                    convertVoice = !convertVoice;
                    sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_CONVERT_VOICE_MESSAGE, convertVoice).apply();
                } else if (position == convertTextRow) {
                    convertText = !convertText;
                    sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_CONVERT_TEXT_MESSAGE, convertText).apply();
                }  else if (position == voteRow) {
                    vote = vote == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_VOTE, vote).apply();
                } else if (position == showSenderNameRow) {
                    showSenderName = showSenderName == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_SHOW_SENDER_NEME_IN_GROUP, showSenderName).apply();
                    G.showSenderNameInGroup = showSenderName == 1;
                } else if (position == sendByEnterRow) {
                    sendByEnter = sendByEnter == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_SEND_BT_ENTER, sendByEnter).apply();
                } else if (position == soundInChatRow) {
                    soundInChat = soundInChat == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_PLAY_SOUND_IN_CHAT, soundInChat).apply();
                } else if (position == appBrowserRow) {
                    appBrowser = appBrowser == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_IN_APP_BROWSER, appBrowser).apply();
                } else if (position == compressRow) {
                    compress = compress == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_COMPRESS, compress).apply();
                } else if (position == cropRow) {
                    crop = crop == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_CROP, crop).apply();
                } else if (position == defaultPlayerRow) {
                    defaultPlayer = defaultPlayer == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_DEFAULT_PLAYER, defaultPlayer).apply();
                } else if (position == trimRow) {
                    trim = trim == 1 ? 0 : 1;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_TRIM, trim).apply();
                } else if (position == cameraButtonSheetRow) {
                    cameraButtonSheet = !cameraButtonSheet;
                    sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_CAMERA_BUTTON_SHEET, cameraButtonSheet).apply();
                }/* else if (position == systemDarkModeRow){
                    systemDarkMode = !systemDarkMode;
                    sharedPreferences.edit().putBoolean(SHP_SETTING.KEY_SYSTEM_DARK_MODE, systemDarkMode).apply();
                }*/
            } else if (view instanceof TextSettingsCell) {
                if (position == dateRow) {
                    showDateDialog();
                }
            } else if (view instanceof TextCell) {
                if (position == backgroundRow) {
                    if (getActivity() != null) {
                        new HelperFragment(getActivity().getSupportFragmentManager(), new ChatBackgroundFragment()).setReplace(false).load();
                    }
                }
            }
        });
        return fragmentView;
    }

    private String setDateText(int dateType) {
        switch (dateType) {
            case 1:
                return context.getResources().getString(R.string.shamsi);
            case 2:
                return context.getResources().getString(R.string.ghamari);
            default:
                return context.getResources().getString(R.string.miladi);
        }
    }

    private void showDateDialog() {
        new MaterialDialog.Builder(context)
                .backgroundColor(Theme.getColor(Theme.key_popup_background))
                .title(G.fragmentActivity.getResources().getString(R.string.self_destructs))
                .titleGravity(GravityEnum.START)
                .items(R.array.date_type)
                .negativeColor(Theme.getColor(Theme.key_button_background))
                .positiveColor(Theme.getColor(Theme.key_button_background))
                .choiceWidgetColor(ColorStateList.valueOf(Theme.getColor(Theme.key_button_background)))
                .alwaysCallSingleChoiceCallback()
                .positiveText(R.string.B_ok).negativeText(R.string.B_cancel)
                .itemsCallback((dialog, itemView, position, text) -> {
                    date = position;
                    sharedPreferences.edit().putInt(SHP_SETTING.KEY_DATA, date).apply();
                    listAdapter.notifyItemChanged(dateRow);
                })
                .show();
    }

    @SuppressLint("ResourceType")
    @Override
    public Toolbar createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.chat_setting_title));
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
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(listView, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.didSetNewTheme) {
            if (getThemeDescriptor() != null) {
                for (int i = 0; i < getThemeDescriptor().size(); i++) {
                    getThemeDescriptor().get(i).setColor();
                }
                if (listAdapter != null) {
                    listAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private final Context context;
        private boolean first = true;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return rowCount;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
            return type != 0 && type != 5;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new HeaderCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    break;
                case 1:
                    view = new TextSizeCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_chat_background));
                    TextSizeCell cell = (TextSizeCell) view;
                    cell.sizeBar.setDelegate(new SeekBarView.SeekBarViewDelegate() {
                        @Override
                        public void onSeekBarDrag(boolean stop, float progress) {
                            setFontSize(Math.round(startFontSize + (endFontSize - startFontSize) * progress));
                        }

                        @Override
                        public void onSeekBarPressed(boolean pressed) {
                        }

                        @Override
                        public CharSequence getContentDescription() {
                            return String.valueOf(Math.round(startFontSize + (endFontSize - startFontSize) * cell.sizeBar.getProgress()));
                        }

                        @Override
                        public int getStepsCount() {
                            return endFontSize - startFontSize;
                        }
                    });
                    view = cell;
                    break;
                case 2:
                default:
                    view = new TextCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    break;
                case 3:
                    first = true;
                    themesHorizontalListCell = new ThemesHorizontalListCell(context){
                        @Override
                        public void setTheme(){
                            Theme.applyTheme(Theme.getCurrentTheme(),Theme.getCurrentThemeType());
                        }
                    };
                    themesHorizontalListCell.setDrawDivider(true);
                    themesHorizontalListCell.setFocusable(false);
                    view = themesHorizontalListCell;
                    view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutCreator.dp(148)));
                    break;
                case 4:
                    view = new TextSettingsCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    break;
                case 5:
                    view = new TextCheckCell(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    break;
                case 6:
                    view = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));
                    break;
                case 7:
                    RecyclerListView accentsListView = new TintRecyclerListView(context) {
                        @Override
                        public boolean onInterceptTouchEvent(MotionEvent e) {
                            if (getParent() != null && getParent().getParent() != null) {
                                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                            }
                            return super.onInterceptTouchEvent(e);
                        }

                        @Override
                        public void setTheme(){
                            Theme.applyTheme(Theme.getCurrentTheme(),Theme.getCurrentThemeType());
                        }
                    };
                    view = accentsListView;
                    view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutCreator.dp(90)));
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0: {
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == sectionFourHeaderRow) {
                        headerCell.setText(context.getString(R.string.st_title_Media));
                    } else if (position == sectionThreeHeaderRow) {
                        headerCell.setText(context.getString(R.string.settings));
                    } else if (position == sectionTwoHeaderRow) {
                        headerCell.setText(context.getString(R.string.theme));
                    } else if (position == sectionOneHeaderRow) {
                        headerCell.setText(context.getString(R.string.st_title_message));
                    }
                    break;
                }
                case 2: {
                    TextCell cell = (TextCell) holder.itemView;
                    if (position == backgroundRow) {
                        cell.setTextAndIcon(context.getString(R.string.st_chatBackground), R.drawable.msg_background, false);
                        cell.setColors(Theme.key_icon, Theme.key_default_text);
                    }
                    break;
                }
                case 3: {
                    if (first) {
                        first = false;
                    } else {
                        themesHorizontalListCell.getAdapter().notifyDataSetChanged();
                    }
                    break;
                }
                case 4: {
                    TextSettingsCell cell = (TextSettingsCell) holder.itemView;
                    if (position == dateRow) {
                        cell.setTextAndValue(context.getString(R.string.date), setDateText(date), true);
                    }
                    break;
                }
                case 5: {
                    TextCheckCell textCheckCell = (TextCheckCell) holder.itemView;
                    textCheckCell.setBackgroundColor(Theme.getColor(Theme.key_window_background));
                    /*if (position == systemDarkModeRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_dark_theme), systemDarkMode, true);
                    }
                    else */if (position == wholeTimeRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_24_Clock), wholeTime, true);
                    } else if (position == convertVoiceRow){
                        textCheckCell.setTextAndCheck(context.getString(R.string.convert_voice_message), convertVoice, true);
                    } else if (position == convertTextRow){
                        textCheckCell.setTextAndCheck(context.getString(R.string.convert_text_message), convertText, true);
                    } else if (position == voteRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.show_vote_layout_in_channel), vote == 1, true);
                    } else if (position == showSenderNameRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.show_sender_name_in_group), showSenderName == 1, true);
                    } else if (position == sendByEnterRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_send_by_enter), sendByEnter == 1, true);
                    } else if (position == soundInChatRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.play_sound_in_chat), soundInChat == 1, true);
                    } else if (position == appBrowserRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_inApp_Browser), appBrowser == 1, false);
                    } else if (position == compressRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_compress), compress == 1, true);
                    } else if (position == cropRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_crop), crop == 1, true);
                    } else if (position == defaultPlayerRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.open_default_player), defaultPlayer == 1, true);
                    } else if (position == trimRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_trim), trim == 1, true);
                    } else if (position == cameraButtonSheetRow) {
                        textCheckCell.setTextAndCheck(context.getString(R.string.st_cameraBottomSheet), cameraButtonSheet, false);
                    }
                    break;
                }
                case 6: {
                    ShadowSectionCell shadowSectionCell = (ShadowSectionCell) holder.itemView;
                    shadowSectionCell.setColors(Theme.getColor(Theme.key_line));
                    break;
                }
                case 7: {
                    RecyclerListView accentsList = (RecyclerListView) holder.itemView;
                    TintRecyclerListView.ThemeAccentsListAdapter adapter = (TintRecyclerListView.ThemeAccentsListAdapter) accentsList.getAdapter();
                    adapter.notifyDataSetChanged();
                    int pos = Theme.findCurrentAccent();
                    if (pos == -1) {
                        pos = adapter.getItemCount() - 1;
                    }
                    if (pos != -1) {
                        ((LinearLayoutManager) accentsList.getLayoutManager()).scrollToPositionWithOffset(pos, listView.getMeasuredWidth() / 2 - LayoutCreator.dp(42));
                    }
                    break;
                }
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            int type = holder.getItemViewType();
        }

        @Override
        public int getItemViewType(int position) {
            if (position == sectionOneHeaderRow || position == sectionTwoHeaderRow || position == sectionThreeHeaderRow || position == sectionFourHeaderRow) {
                return 0;
            } else if (position == messageTextSizeRow) {
                return 1;
            } else if (position == backgroundRow) {
                return 2;
            } else if (position == themeListRow) {
                return 3;
            } else if (position == dateRow) {
                return 4;
            } else if (position == showSenderNameRow || position == sendByEnterRow || position == compressRow || position == soundInChatRow || position == wholeTimeRow || /*position == systemDarkModeRow ||*/
                       position == voteRow || position == trimRow || position == defaultPlayerRow || position == appBrowserRow || position == cropRow || position == cameraButtonSheetRow ||
                       position == convertVoiceRow || position == convertTextRow) {
                return 5;
            } else if (position == sectionOneDividerRow || position == sectionTwoDividerRow || position == sectionThreeDividerRow || position == sectionFourDividerRow) {
                return 6;
            } else if (position == themeAccentListRow) {
                return 7;
            }
            return 0;
        }

        private boolean setFontSize(int size) {
            SharedConfig.fontSize = size;
            StartupActions.textSizeDetection(size);
            sharedPreferences.edit().putInt(SHP_SETTING.KEY_MESSAGE_TEXT_SIZE, size).apply();
            RecyclerView.ViewHolder holder = listView.findViewHolderForAdapterPosition(messageTextSizeRow);
            if (holder != null && holder.itemView instanceof TextSizeCell) {
                TextSizeCell cell = (TextSizeCell) holder.itemView;
                cell.messagesCell.setPreviewValue(size);
            }
            return true;
        }
    }
}
