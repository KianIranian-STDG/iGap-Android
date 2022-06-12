package net.iGap.messenger.ui.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.theme.ThemeDescriptor;
import net.iGap.messenger.ui.cell.EditTextCell;
import net.iGap.messenger.ui.cell.HeaderCell;
import net.iGap.messenger.ui.cell.ShadowSectionCell;
import net.iGap.messenger.ui.toolBar.Toolbar;
import net.iGap.messenger.ui.toolBar.ToolbarItems;

import java.util.ArrayList;
import java.util.List;

import static net.iGap.module.AppUtils.error;

public class TwoStepVerificationQuestionFragment extends BaseFragment {

    private final static int done_button = 1;
    private View doneButton;
    private HeaderCell tvQuestion1;
    private EditTextCell etQuestion1;
    private EditTextCell etAnswer1;
    private ShadowSectionCell divider1;
    private HeaderCell tvQuestion2;
    private EditTextCell etQuestion2;
    private EditTextCell etAnswer2;
    private ShadowSectionCell divider2;
    private ScrollView scrollView;
    private TwoStepVerificationStepsFragment.QuestionListener questionListener;
    private String questionOne;
    private String questionTwo;

    public TwoStepVerificationQuestionFragment(String questionOne,String questionTwo,TwoStepVerificationStepsFragment.QuestionListener questionListener) {
        this.questionListener = questionListener;
        this.questionOne = questionOne;
        this.questionTwo = questionTwo;
    }

    @Override
    public View createView(Context context) {

        tvQuestion1 = new HeaderCell(context);
        tvQuestion1.setText(getString(R.string.password_Question_title_one));

        etQuestion1 = new EditTextCell(context);
        etQuestion1.setValues(getString(R.string.password_Question_one), Theme.getColor(Theme.key_default_text), Theme.getColor(Theme.key_window_background),true);
        if (!questionOne.isEmpty()) {
            etQuestion1.setText(questionOne);
        }

        etAnswer1 = new EditTextCell(context);
        etAnswer1.setValues(getString(R.string.password_answer), Theme.getColor(Theme.key_default_text), Theme.getColor(Theme.key_window_background),true);

        divider1 = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));

        tvQuestion2 = new HeaderCell(context);
        tvQuestion2.setText(getString(R.string.password_Question_title_two));

        etQuestion2 = new EditTextCell(context);
        etQuestion2.setValues(getString(R.string.password_Question_two), Theme.getColor(Theme.key_default_text), Theme.getColor(Theme.key_window_background),true);
        if (!questionTwo.isEmpty()) {
            etQuestion2.setText(questionTwo);
        }

        etAnswer2 = new EditTextCell(context);
        etAnswer2.setValues(getString(R.string.password_answer), Theme.getColor(Theme.key_default_text), Theme.getColor(Theme.key_window_background),true);

        divider2 = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_line));

        ViewGroup container = new ViewGroup(context) {
                @Override
                protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                    int width = MeasureSpec.getSize(widthMeasureSpec);
                    int height = MeasureSpec.getSize(heightMeasureSpec);
                    scrollView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), heightMeasureSpec);
                    setMeasuredDimension(width, height);
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    scrollView.layout(0, 0, scrollView.getMeasuredWidth(), scrollView.getMeasuredHeight());
                }
            };
            scrollView = new ScrollView(context) {
                private int[] location = new int[2];
                private Rect tempRect = new Rect();
                private boolean isLayoutDirty = true;
                private int scrollingUp;

                @Override
                protected void onScrollChanged(int l, int t, int oldl, int oldt) {
                    super.onScrollChanged(l, t, oldl, oldt);
                    if (tvQuestion1 == null) {
                        return;
                    }
                    tvQuestion1.getLocationOnScreen(location);
                }

                @Override
                public void scrollToDescendant(View child) {
                    child.getDrawingRect(tempRect);
                    offsetDescendantRectToMyCoords(child, tempRect);
                    tempRect.bottom += LayoutCreator.dp(120);
                    int scrollDelta = computeScrollDeltaToGetChildRectOnScreen(tempRect);
                    if (scrollDelta < 0) {
                        scrollDelta -= (scrollingUp = (getMeasuredHeight() - child.getMeasuredHeight()) / 2);
                    } else {
                        scrollingUp = 0;
                    }
                    if (scrollDelta != 0) {
                        smoothScrollBy(0, scrollDelta);
                    }
                }

                @Override
                public void requestChildFocus(View child, View focused) {
                    if (Build.VERSION.SDK_INT < 29) {
                        if (focused != null && !isLayoutDirty) {
                            scrollToDescendant(focused);
                        }
                    }
                    super.requestChildFocus(child, focused);
                }

                @Override
                public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
                    if (Build.VERSION.SDK_INT < 23) {
                        rectangle.bottom += LayoutCreator.dp(120);

                        if (scrollingUp != 0) {
                            rectangle.top -= scrollingUp;
                            rectangle.bottom -= scrollingUp;
                            scrollingUp = 0;
                        }
                    }
                    return super.requestChildRectangleOnScreen(child, rectangle, immediate);
                }

                @Override
                public void requestLayout() {
                    isLayoutDirty = true;
                    super.requestLayout();
                }

                @Override
                protected void onLayout(boolean changed, int l, int t, int r, int b) {
                    isLayoutDirty = false;
                    super.onLayout(changed, l, t, r, b);
                }
            };
            scrollView.setVerticalScrollBarEnabled(false);
            container.addView(scrollView);

            LinearLayout scrollViewLinearLayout = new LinearLayout(context);
            scrollViewLinearLayout.setOrientation(LinearLayout.VERTICAL);
            scrollView.addView(scrollViewLinearLayout, LayoutCreator.createScroll(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.LEFT | Gravity.TOP));
            scrollViewLinearLayout.addView(tvQuestion1, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 24, 0, 0));
            scrollViewLinearLayout.addView(etQuestion1, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));
            scrollViewLinearLayout.addView(etAnswer1, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));
            scrollViewLinearLayout.addView(divider1, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));
            scrollViewLinearLayout.addView(tvQuestion2, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 24, 0, 0));
            scrollViewLinearLayout.addView(etQuestion2, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));
            scrollViewLinearLayout.addView(etAnswer2, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));
            scrollViewLinearLayout.addView(divider2, LayoutCreator.createLinear(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0, 0, 0));

            FrameLayout frameLayout = new FrameLayout(context);
            scrollViewLinearLayout.addView(frameLayout, LayoutCreator.createLinear(220, 36, Gravity.TOP | Gravity.CENTER_HORIZONTAL, 40, 32, 40, 0));

            fragmentView = container;
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
            return fragmentView;
    }

    @SuppressLint("ResourceType")
    @Override
    public View createToolBar(Context context) {
        toolbar = new Toolbar(context);
        toolbar.setTitle(context.getString(R.string.recovery_question));
        toolbar.setBackIcon(R.drawable.ic_ab_back);
        ToolbarItems toolbarItems = toolbar.createToolbarItems();
        doneButton = toolbarItems.addItem(done_button, R.string.icon_check_ok, Color.WHITE);
        doneButton.setContentDescription(context.getString(R.string.Done));
        toolbar.setListener(id -> {
            if (id == -1) {
                finish();
            } else if (id == done_button) {
                if (etQuestion1.length() > 0 && etAnswer1.length() > 0 && etQuestion2.length() > 0 && etAnswer2.length() > 0) {
                   finish();
                   questionListener.questionDetailsInfo(etQuestion1.getText().toString(), etAnswer1.getText().toString(), etQuestion2.getText().toString(), etAnswer2.getText().toString());
                } else {
                    closeKeyboard(toolbar);
                    error(getString(R.string.please_complete_all_item));
                }
            }
        });
        return toolbar;
    }

    @Override
    public List<ThemeDescriptor> getThemeDescriptor() {
        List<ThemeDescriptor> themeDescriptors = new ArrayList<>();
        themeDescriptors.add(new ThemeDescriptor(tvQuestion1, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etQuestion1, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etQuestion1, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etAnswer1, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etAnswer1, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(tvQuestion2, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etQuestion2, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etQuestion2, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etAnswer2, ThemeDescriptor.FLAG_TEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(etAnswer2, ThemeDescriptor.FLAG_HINTTEXTCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(scrollView, ThemeDescriptor.FLAG_LISTGLOWCOLOR, Theme.key_default_text));
        themeDescriptors.add(new ThemeDescriptor(toolbar, ThemeDescriptor.FLAG_BACKGROUND, Theme.key_toolbar_background));
        return themeDescriptors;
    }
}
