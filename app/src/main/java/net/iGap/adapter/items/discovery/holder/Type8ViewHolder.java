package net.iGap.adapter.items.discovery.holder;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentActivity;

import net.iGap.G;
import net.iGap.R;
import net.iGap.adapter.items.discovery.DiscoveryItem;
import net.iGap.fragments.FragmentUserScore;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.HelperFragment;
import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.HandShakeCallback;
import net.iGap.realm.RealmUserInfo;

public class Type8ViewHolder extends BaseViewHolder implements HandShakeCallback {
    public AppCompatTextView tvScore, tvContent;

    public Type8ViewHolder(@NonNull View itemView, FragmentActivity activity) {
        super(itemView, activity);
        tvScore = itemView.findViewById(R.id.tv_score);
        tvContent = itemView.findViewById(R.id.textView2);
    }

    @Override
    public void bindView(DiscoveryItem item) {
        itemView.setOnClickListener(view -> new HelperFragment(G.currentActivity.getSupportFragmentManager(), new FragmentUserScore()).setReplace(false).load());

        DbManager.getInstance().doRealmTask(realm -> {
            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).findFirst();
            if (userInfo != null)
                tvScore.setText(HelperCalander.isPersianUnicode ? HelperCalander.convertToUnicodeFarsiNumber(String.valueOf(userInfo.getIvandScore())) : String.valueOf(userInfo.getIvandScore()));
        });

        Animation animation = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.shake_mode);
        animation.reset();
        tvContent.clearAnimation();
        tvContent.startAnimation(animation);
    }
}
