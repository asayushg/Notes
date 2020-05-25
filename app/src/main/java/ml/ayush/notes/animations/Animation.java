package ml.ayush.notes.animations;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;

import static ml.ayush.notes.utils.Constants.DURATION_RECYCLER_ANIMATION;
import static ml.ayush.notes.utils.Constants.DURATION_RECYCLER_TRANSLATE;

public class Animation {

    public void setAnimation(View itemView, int i, boolean on_attach) {
        if(!on_attach){
            i = -1;
        }
        boolean isNotFirstItem = i == -1;
        i++;
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(itemView, "alpha", 0.f, 0.5f, 1.0f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animator.setStartDelay(isNotFirstItem ? DURATION_RECYCLER_ANIMATION / 2 : (i * DURATION_RECYCLER_ANIMATION / 3));
        animator.setDuration(DURATION_RECYCLER_ANIMATION);
        animatorSet.play(animator);
        animator.start();
    }

    public void FromRightToLeft(View itemView, int i,boolean on_attach) {
        if(!on_attach){
            i = -1;
        }
        boolean not_first_item = i == -1;
        i = i + 1;
        itemView.setTranslationX(itemView.getX() + 400);
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", itemView.getX() + 400, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1.f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animatorTranslateY.setStartDelay(not_first_item ? DURATION_RECYCLER_TRANSLATE : (i * DURATION_RECYCLER_TRANSLATE));
        animatorTranslateY.setDuration((not_first_item ? 2 : 1) * DURATION_RECYCLER_TRANSLATE);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }

    public void FromLeftToRight(View itemView, int i,boolean on_attach) {
        if(!on_attach){
            i = -1;
        }
        boolean not_first_item = i == -1;
        i = i + 1;
        itemView.setTranslationX(-400f);
        itemView.setAlpha(0.f);
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", -400f, 0);
        ObjectAnimator animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1.f);
        ObjectAnimator.ofFloat(itemView, "alpha", 0.f).start();
        animatorTranslateY.setStartDelay(not_first_item ? DURATION_RECYCLER_TRANSLATE : (i * DURATION_RECYCLER_TRANSLATE));
        animatorTranslateY.setDuration((not_first_item ? 2 : 1) * DURATION_RECYCLER_TRANSLATE);
        animatorSet.playTogether(animatorTranslateY, animatorAlpha);
        animatorSet.start();
    }
}
