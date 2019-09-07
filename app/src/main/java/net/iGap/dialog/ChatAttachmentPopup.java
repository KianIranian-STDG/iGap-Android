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

    private final String TAG = "ChatAttachmentPopup";

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

    public ChatAttachmentPopup build(){
        if (mContext == null) throw new IllegalArgumentException(TAG + " : CONTEXT can not be null!");
        if (mRootView == null) throw new IllegalArgumentException(TAG + " : set root view!");
        return this;
    }

    public void show(){

        //get height of keyboard if it was gone set wrap content to popup
        int height = getKeyboardHeight() ;
        if (height == 0) height = ViewGroup.LayoutParams.WRAP_CONTENT;

        //inflate layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.bottom_sheet_new , null , false);

        //setup popup
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

        //animate views after popup showed -> delay set base on xml animation
        G.handler.postDelayed(() -> setupViews(view) , 250);
    }

    private void setupViews(View view) {

        //icons
        TextView icoCamera = view.findViewById(R.id.txtCamera);
        TextView icoPicture = view.findViewById(R.id.textPicture);
        TextView icoVideo = view.findViewById(R.id.txtVideo);
        TextView icoMusic = view.findViewById(R.id.txtMusic);
        TextView icoFile = view.findViewById(R.id.txtFile);
        TextView icoLocation = view.findViewById(R.id.txtLocation);
        TextView icoContact = view.findViewById(R.id.txtContact);
        TextView icoSend = view.findViewById(R.id.txtSend);

        //labels
        TextView lblCamera = view.findViewById(R.id.txtCamera2);
        TextView lblPicture = view.findViewById(R.id.textPicture2);
        TextView lblVideo = view.findViewById(R.id.txtVideo2);
        TextView lblMusic = view.findViewById(R.id.txtMusic2);
        TextView lblFile = view.findViewById(R.id.txtFile2);
        TextView lblLocation = view.findViewById(R.id.txtLocation2);
        TextView lblContact = view.findViewById(R.id.txtContact2);
        TextView lblSend = view.findViewById(R.id.txtNumberItem);

        //animate all icons and after anim show their label
        animateViewWithCircularReveal(icoCamera     , lblCamera);
        animateViewWithCircularReveal(icoPicture    , lblPicture);
        animateViewWithCircularReveal(icoVideo      , lblVideo);
        animateViewWithCircularReveal(icoMusic      , lblMusic);
        animateViewWithCircularReveal(icoFile       , lblFile);
        animateViewWithCircularReveal(icoLocation   , lblLocation);
        animateViewWithCircularReveal(icoContact    , lblContact);
        animateViewWithCircularReveal(icoSend       , lblSend);

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
            Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

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
