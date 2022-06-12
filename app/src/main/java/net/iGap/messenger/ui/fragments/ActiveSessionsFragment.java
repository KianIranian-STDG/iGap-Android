package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;

import net.iGap.G;
import net.iGap.R;
import net.iGap.activities.ActivityEnhanced;
import net.iGap.activities.ActivityMain;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.HelperError;
import net.iGap.helper.HelperPermission;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.dialog.AlertDialog;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.SessionCell;
import net.iGap.messenger.ui.cell.TextInfoPrivacyCell;
import net.iGap.messenger.ui.cell.TextSettingsCell;
import net.iGap.messenger.ui.components.EmptyTextProgressView;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.module.customView.RecyclerListView;
import net.iGap.module.structs.StructSessions;
import net.iGap.observers.interfaces.OnGetPermission;
import net.iGap.observers.interfaces.OnUserSessionTerminate;
import net.iGap.request.RequestUserSessionGetActiveList;
import net.iGap.request.RequestUserSessionTerminate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.AndroidUtils.displaySize;

public class ActiveSessionsFragment extends BaseFragment {

    private final List<StructSessions> structSessionsList = new ArrayList<>();
    private ListAdapter listAdapter;
    private boolean loading = true;
    private LinearLayout emptyLayout;
    private RecyclerListView listView;
    private ImageView imageView;
    private TextView textView1;
    private TextView textView2;
    private EmptyTextProgressView emptyView;

    private int sessionListSize;
    private int currentSessionSectionRow;
    private int currentSessionRow;
    private int terminateAllSessionsRow;
    private int terminateAllSessionsDetailRow;
    private int sessionsSectionRow;
    private int qrCodeRow;
    private int sessionsTerminateDetail;
    private int noOtherSessionsRow;
    private int rowCount;

    @Override
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        getUserSessionGetActiveList();
        return true;
    }

    @Override
    public View createView(Context context) {

        listAdapter = new ListAdapter(context);
        fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_window_background));

        emptyLayout = new LinearLayout(context);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER);
        emptyLayout.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
        emptyLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, displaySize.y));

        imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.devices);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
        emptyLayout.addView(imageView, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        textView1 = new TextView(context);
        textView1.setTextColor(Theme.getColor(Theme.key_title_text));
        textView1.setGravity(Gravity.CENTER);
        textView1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        textView1.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        textView1.setText(getString(R.string.NoOtherSessions));

        emptyLayout.addView(textView1, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 16, 0, 0));

        textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor(Theme.key_title_text));
        textView2.setGravity(Gravity.CENTER);
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        textView2.setPadding(LayoutCreator.dp(20), 0, LayoutCreator.dp(20), 0);
        textView2.setText(getString(R.string.NoOtherSessionsInfo));
        emptyLayout.addView(textView2, LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 0, 14, 0, 0));

        emptyView = new EmptyTextProgressView(context);
        emptyView.showProgress();
        frameLayout.addView(emptyView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER));

        listView = new RecyclerListView(context);
        listView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        listView.setVerticalScrollBarEnabled(false);
        listView.setEmptyView(emptyView);
        frameLayout.addView(listView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((view, position) -> {
            if (view instanceof TextSettingsCell) {
                if (position == qrCodeRow) {
                    loginWithBarcodeScanner();
                } else if (position == terminateAllSessionsRow) {
                    if (getActivity() == null) {
                        return;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    String buttonText;
                    builder.setMessage(getString(R.string.AreYouSureSessions));
                    builder.setTitle(getString(R.string.AreYouSureSessionsTitle));
                    buttonText = getString(R.string.Terminate);
                    builder.setPositiveButton(buttonText, (dialogInterface, i) -> {
                        terminateAllSessions();
                        listAdapter.notifyDataSetChanged();
                    });
                    builder.setNegativeButton(getString(R.string.Cancel), null);
                    AlertDialog alertDialog = builder.create();
                    showDialog(alertDialog);
                    TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    if (button != null) {
                        button.setTextColor(Theme.getColor(Theme.key_dark_red));
                    }
                }
            } else if (view instanceof SessionCell) {
                SessionCell sessionCell = (SessionCell) view;
                if (getActivity() == null) {
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final boolean[] param = new boolean[1];
                String buttonText;
                builder.setMessage(getString(R.string.TerminateSessionText));
                builder.setTitle(getString(R.string.AreYouSureSessionTitle));
                buttonText = getString(R.string.Terminate);
                builder.setPositiveButton(buttonText, (dialogInterface, option) -> {
                    G.fragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    new RequestUserSessionTerminate().userSessionTerminate(sessionCell.getSessionId());
                });
                builder.setNegativeButton(getString(R.string.Cancel), null);
                AlertDialog alertDialog = builder.create();
                showDialog(alertDialog);
                TextView button = (TextView) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Theme.getColor(Theme.key_dark_red));
                }
            }
        });

        return fragmentView;
    }

    private void terminateAllSessions() {
        for (int index = 1; index < sessionListSize; index++) {
            new RequestUserSessionTerminate().userSessionTerminate(structSessionsList.get(index).getSessionId());
        }
    }

    private void loginWithBarcodeScanner() {
        if (getActivity() instanceof ActivityEnhanced) {
            try {
                HelperPermission.getCameraPermission(getActivity(), new OnGetPermission() {
                    @Override
                    public void Allow() throws IllegalStateException {
                        IntentIntegrator integrator = new IntentIntegrator(getActivity());
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                        integrator.setRequestCode(ActivityMain.requestCodeQrCode);
                        integrator.setBeepEnabled(false);
                        integrator.setPrompt("");
                        integrator.initiateScan();
                    }

                    @Override
                    public void deny() {
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    private void updateRows() {
        loading = false;
        sessionListSize = structSessionsList.size();
        currentSessionSectionRow = 0;
        currentSessionRow = 1;
        if (sessionListSize == 1) {
            noOtherSessionsRow = 2;
            rowCount = 3;
        } else {
            terminateAllSessionsRow = 2;
            terminateAllSessionsDetailRow = 3;
            sessionsSectionRow = 4;
            qrCodeRow = 5;
            sessionsTerminateDetail = 5 + sessionListSize;
            rowCount = 6 + sessionListSize;
        }
    }

    private void getUserSessionGetActiveList() {
        G.onUserSessionGetActiveList = session -> G.handler.post(() -> {
            for (int i = 0; i < session.size(); i++) {
                StructSessions structSessions = new StructSessions();
                structSessions.setSessionId(session.get(i).getSessionId());
                structSessions.setName(session.get(i).getAppName());
                structSessions.setAppId(session.get(i).getAppId());
                structSessions.setBuildVersion(session.get(i).getAppBuildVersion());
                structSessions.setAppVersion(session.get(i).getAppVersion());
                structSessions.setPlatform(session.get(i).getPlatform());
                structSessions.setPlatformVersion(session.get(i).getPlatformVersion());
                structSessions.setDevice(session.get(i).getDevice());
                structSessions.setDeviceName(session.get(i).getDeviceName());
                structSessions.setLanguage(session.get(i).getLanguage());
                structSessions.setCountry(session.get(i).getCountry());
                structSessions.setCurrent(session.get(i).getCurrent());
                structSessions.setCreateTime(session.get(i).getCreateTime());
                structSessions.setActiveTime(session.get(i).getActiveTime());
                structSessions.setIp(session.get(i).getIp());
                if (structSessions.isCurrent()) {
                    structSessionsList.add(0, structSessions);
                } else {
                    structSessionsList.add(structSessions);
                }
            }
            updateRows();
            listAdapter.notifyDataSetChanged();
        });
        new RequestUserSessionGetActiveList().userSessionGetActiveList();
        G.onUserSessionTerminate = new OnUserSessionTerminate() {
            @Override
            public void onUserSessionTerminate(final Long messageId) {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    for (int i = 1; i < structSessionsList.size(); i++) {
                        if (structSessionsList.get(i).getSessionId() == messageId) {
                            structSessionsList.remove(i);
                            updateRows();
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }

            @Override
            public void onTimeOut() {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);
                });
            }

            @Override
            public void onError() {
                G.handler.post(() -> {
                    G.fragmentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    HelperError.showSnackMessage(G.fragmentActivity.getResources().getString(R.string.error), false);
                });
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        G.onUserSessionGetActiveList = null;
        G.onUserSessionTerminate = null;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.ActiveSessions));
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
        themeDescriptors.add(new ThemeDescriptor(imageView, ThemeDescriptor.FLAG_IMAGECOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(textView1, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(textView2, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(emptyView, ThemeDescriptor.FLAG_PROGRESSBAR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(emptyView, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }

    private class ListAdapter extends RecyclerListView.SelectionAdapter {

        private Context context;

        public ListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return position != currentSessionSectionRow && position != terminateAllSessionsDetailRow && position != sessionsTerminateDetail;
        }

        @Override
        public int getItemCount() {
            return loading ? 0 : rowCount;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            switch (viewType) {
                case 0:
                    view = new TextSettingsCell(context);
                    break;
                case 1:
                    view = new TextInfoPrivacyCell(context);
                    view.setBackground(Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_line));
                    view.setBackgroundColor(Theme.getColor(Theme.key_line));
                    break;
                case 2:
                    view = new HeaderCell(context);
                    break;
                case 3:
                    view = emptyLayout;
                    break;
                default:
                    view = new SessionCell(context);
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case 0:
                    TextSettingsCell textCell = (TextSettingsCell) holder.itemView;
                    if (position == terminateAllSessionsRow) {
                        textCell.setTextColor(Theme.getColor(Theme.key_dark_red));
                        textCell.setTag(Theme.key_dark_red);
                        textCell.setText(getString(R.string.TerminateAllSessions), false);
                    } else if (position == qrCodeRow) {
                        textCell.setTextColor(Theme.getColor(Theme.key_title_text));
                        textCell.setTag(Theme.key_title_text);
                        textCell.setText(getString(R.string.AuthAnotherClient), true);
                    }
                    if (sessionListSize == 1){
                        textCell.setVisibility(View.GONE);
                    } else {
                        textCell.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    TextInfoPrivacyCell privacyCell = (TextInfoPrivacyCell) holder.itemView;
                    if (position == terminateAllSessionsDetailRow) {
                        privacyCell.setText(getString(R.string.ClearOtherSessionsHelp));
                    } else if (position == sessionsTerminateDetail) {
                        privacyCell.setText(getString(R.string.TerminateSessionInfo));
                    }
                    break;
                case 2:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == currentSessionSectionRow) {
                        headerCell.setText(getString(R.string.CurrentSession));
                    } else if (position == sessionsSectionRow) {
                        headerCell.setText(getString(R.string.OtherSessions));
                    }
                    break;
                case 3:
                    ViewGroup.LayoutParams layoutParams = emptyLayout.getLayoutParams();
                    if (layoutParams != null) {
                        layoutParams.height = Math.max(LayoutCreator.dp(220), displaySize.y - LayoutCreator.dp(128 + (qrCodeRow == -1 ? 0 : 30)));
                        emptyLayout.setLayoutParams(layoutParams);
                    }
                    break;
                case 4:
                    SessionCell sessionCell = (SessionCell) holder.itemView;
                    if (position == currentSessionRow) {
                        sessionCell.setSession(structSessionsList.get(0));
                    } else {
                        sessionCell.setSession(structSessionsList.get(position - 5));
                    }
                    break;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if ((position == terminateAllSessionsRow || position == qrCodeRow) && sessionListSize > 1) {
                return 0;
            } else if ((position == terminateAllSessionsDetailRow || position == sessionsTerminateDetail) && sessionListSize > 1) {
                return 1;
            } else if (position == currentSessionSectionRow || (position == sessionsSectionRow && sessionListSize > 1)) {
                return 2;
            } else if (position == noOtherSessionsRow && sessionListSize == 1) {
                return 3;
            } else {
                return 4;
            }
        }
    }
}
