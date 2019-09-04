package net.iGap.dialog;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.Group;
import android.transition.Transition;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.iGap.G;
import net.iGap.R;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ChatAttachmentPopup {

    private static final String TAG = "ChatAttachmentPopup";

    private Context mContext ;
    private View mRootView;
    private ChatPopupListener mPopupListener;
    private PopupWindow mPopup ;

    private ChatAttachmentPopup() {
    }

    public static ChatAttachmentPopup create(){
        return new ChatAttachmentPopup();
    }

    public ChatAttachmentPopup setContext(Context context) {
        this.mContext = context;
        return this;
    }

    public ChatAttachmentPopup setRootView(View view){
        this.mRootView = view ;
        return this;
    }

    public ChatAttachmentPopup setListener(ChatPopupListener listener){
        this.mPopupListener = listener ;
        return this;
    }

    public void show(){


        int height = getKeyboardHeight() ;
        if (height == 0) height = ViewGroup.LayoutParams.WRAP_CONTENT;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.bottom_sheet_new , null , false);

        mPopup = new PopupWindow(mContext);
        mPopup.setContentView(view);
        mPopup.setHeight(height);
        mPopup.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.setBackgroundDrawable(new BitmapDrawable());
        mPopup.setFocusable(true);
        mPopup.setOutsideTouchable(true);
        mPopup.setAnimationStyle(R.style.chatAttachmentAnimation);

        mPopup.showAtLocation(mRootView , Gravity.BOTTOM , 0 , 0 );

        G.handler.postDelayed(() -> setupViews(view) , 250);
    }

    private void setupViews(View view) {
        TextView txtCamera = view.findViewById(R.id.txtCamera);
        TextView textPicture = view.findViewById(R.id.textPicture);
        TextView txtVideo = view.findViewById(R.id.txtVideo);
        TextView txtMusic = view.findViewById(R.id.txtMusic);
        TextView txtFile = view.findViewById(R.id.txtFile);
        TextView txtLocation = view.findViewById(R.id.txtLocation);
        TextView txtContact = view.findViewById(R.id.txtContact);
        TextView send = view.findViewById(R.id.txtSend);

        TextView _txtCamera = view.findViewById(R.id.txtCamera2);
        TextView _textPicture = view.findViewById(R.id.textPicture2);
        TextView _txtVideo = view.findViewById(R.id.txtVideo2);
        TextView _txtMusic = view.findViewById(R.id.txtMusic2);
        TextView _txtFile = view.findViewById(R.id.txtFile2);
        TextView _txtLocation = view.findViewById(R.id.txtLocation2);
        TextView _txtContact = view.findViewById(R.id.txtContact2);
        TextView _send = view.findViewById(R.id.txtNumberItem);

        animateViewWithCircularReveal(txtCamera , _txtCamera);
        animateViewWithCircularReveal(textPicture, _textPicture);
        animateViewWithCircularReveal(txtVideo, _txtVideo);
        animateViewWithCircularReveal(txtMusic , _txtMusic);
        animateViewWithCircularReveal(txtFile , _txtFile);
        animateViewWithCircularReveal(txtLocation , _txtLocation);
        animateViewWithCircularReveal(txtContact , _txtContact);
        animateViewWithCircularReveal(send ,_send);

    }

    private int getKeyboardHeight() {
        try {
            final InputMethodManager imm = (InputMethodManager) mContext.getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            final Class inputMethodManagerClass = imm.getClass();
            final Method visibleHeightMethod = inputMethodManagerClass.getDeclaredMethod("getInputMethodWindowVisibleHeight");
            visibleHeightMethod.setAccessible(true);
            return (int) visibleHeightMethod.invoke(imm);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ViewGroup.LayoutParams.WRAP_CONTENT ;
    }

    private void animateViewWithCircularReveal(View myView , View lbl) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = myView.getMeasuredWidth() / 2;
            int cy = myView.getMeasuredHeight() / 2;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

            // create the animator for this view (the start radius is zero)
            Animator anim = null;
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

            anim.setDuration(500);

            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
            anim.start();

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lbl.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else {
            myView.setVisibility(View.VISIBLE);
        }
        
    }

    public interface ChatPopupListener{

        void onChatPopupItemClicked();
        void onChatPopupImageSelected();
        void onChatPopupShowed();
        void onChatPopupDissmissed();

    }

    public enum ChatPopupAction {
        PHOTO , VIDEO , CAMERA , MUSIC , FILE , CONTACT , LOCATION , CLOSE
    }
}

/**
 *
 *
 *
 *
 *
 *
 *        // To avoid borders and overdraw.
 *
 */